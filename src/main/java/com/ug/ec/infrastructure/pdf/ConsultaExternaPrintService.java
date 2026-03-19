package com.ug.ec.infrastructure.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaQueryHandler;
import com.ug.ec.application.consultaexterna.queries.BuscarConsultaExternaPorIdQuery;
import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaNotFoundException;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import com.ug.ec.domain.signosvitales.SignosVitales;
import com.ug.ec.infrastructure.pdf.dto.*;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaExternaPrintService {

    private final ConsultaExternaQueryHandler queryHandler;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final SignosVitalesRepository signosVitalesRepository;
    private final TemplateEngine templateEngine;
    private final CoreApiClientService coreApiClientService;

    /**
     * Genera un PDF del formulario HCU-002 de Consulta Externa
     *
     * @param consultaId ID de la consulta externa
     * @param authToken  Bearer token de autenticación para el Core
     * @return byte[] con el contenido del PDF
     */
    public byte[] generarPdf(String consultaId, String authToken) {
        log.info("Generando PDF para consulta externa con ID: {}", consultaId);

        // 1. Obtener la consulta externa
        BuscarConsultaExternaPorIdQuery query = BuscarConsultaExternaPorIdQuery.builder()
                .id(consultaId)
                .build();
        ConsultaExternaDto consulta = queryHandler.handle(query);

        if (consulta == null) {
            throw new ConsultaExternaNotFoundException("Consulta externa no encontrada con ID: " + consultaId);
        }

        // 2. Obtener historia clínica si existe
        HistoriaClinica historiaClinica = null;
        if (consulta.getHistoriaClinicaId() != null) {
            historiaClinica = historiaClinicaRepository.buscarPorId(consulta.getHistoriaClinicaId())
                    .orElse(null);
        } else if (consulta.getCedulaPaciente() != null) {
            historiaClinica = historiaClinicaRepository.buscarPorCedulaPaciente(consulta.getCedulaPaciente())
                    .orElse(null);
        }

        // 3. Obtener signos vitales si existe
        SignosVitales signosVitales = null;
        if (consulta.getSignosVitalesId() != null) {
            signosVitales = signosVitalesRepository.buscarPorId(consulta.getSignosVitalesId())
                    .orElse(null);
        } else if (consulta.getCitaId() != null) {
            List<SignosVitales> signosList = signosVitalesRepository.buscarPorCitaId(consulta.getCitaId());
            if (!signosList.isEmpty()) {
                signosVitales = signosList.get(0);
            }
        }

        // 4. Construir DTOs para el PDF enriquecidos desde el Core
        PacientePdfDto paciente = construirPacienteDto(consulta, historiaClinica, authToken);
        MedicoPdfDto medico = construirMedicoDto(consulta, authToken);

        // 5. Preparar contexto Thymeleaf
        Context context = new Context();
        context.setVariable("consulta", consulta);
        context.setVariable("historiaClinica", historiaClinica);
        context.setVariable("signosVitales", signosVitales);
        context.setVariable("paciente", paciente);
        context.setVariable("medico", medico);

        // 6. Renderizar HTML
        String html = templateEngine.process("pdf/002", context);

        // 7. Convertir HTML a PDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(os);
            builder.run();

            log.info("PDF generado exitosamente para consulta externa con ID: {}", consultaId);
            return os.toByteArray();
        } catch (Exception ex) {
            log.error("Error generando PDF para consulta externa con ID: {}", consultaId, ex);
            throw new RuntimeException("Error generando PDF", ex);
        }
    }

    /**
     * Genera un PDF para una consulta externa buscando por cita ID
     *
     * @param citaId    ID de la cita
     * @param authToken Bearer token de autenticación para el Core
     * @return byte[] con el contenido del PDF
     */
    public byte[] generarPdfPorCitaId(String citaId, String authToken) {
        log.info("Generando PDF para consulta externa por cita ID: {}", citaId);

        var query = com.ug.ec.application.consultaexterna.queries.BuscarConsultaExternaPorCitaIdQuery.builder()
                .id(citaId)
                .build();
        ConsultaExternaDto consulta = queryHandler.handle(query);

        if (consulta == null) {
            throw new ConsultaExternaNotFoundException("Consulta externa no encontrada para cita ID: " + citaId);
        }

        return generarPdf(consulta.getId(), authToken);
    }

    private PacientePdfDto construirPacienteDto(ConsultaExternaDto consulta, HistoriaClinica historiaClinica,
                                                 String authToken) {
        CorePacientePdfResponse coreData =
                coreApiClientService.obtenerPaciente(consulta.getCedulaPaciente(), authToken);

        PersonaPdfDto persona;
        Integer edad = null;

        if (coreData != null) {
            persona = PersonaPdfDto.builder()
                    .cedula(coreData.getCedula())
                    .primerNombre(coreData.getPrimerNombre())
                    .segundoNombre(coreData.getSegundoNombre())
                    .primerApellido(coreData.getPrimerApellido())
                    .segundoApellido(coreData.getSegundoApellido())
                    .build();
            edad = coreData.getEdad();
        } else {
            persona = PersonaPdfDto.builder()
                    .cedula(consulta.getCedulaPaciente())
                    .build();
        }

        return PacientePdfDto.builder()
                .persona(persona)
                .edad(edad)
                .build();
    }

    private MedicoPdfDto construirMedicoDto(ConsultaExternaDto consulta, String authToken) {
        String codigoMedico = consulta.getDatosConsulta() != null
                ? consulta.getDatosConsulta().getCodigoMedico()
                : null;

        CoreMedicoPdfResponse coreData = coreApiClientService.obtenerMedico(codigoMedico, authToken);

        PersonaPdfDto persona;
        List<EspecialidadPdfDto> especialidades = null;
        String firma = null;

        if (coreData != null) {
            persona = PersonaPdfDto.builder()
                    .cedula(coreData.getCedula())
                    .primerNombre(coreData.getPrimerNombre())
                    .segundoNombre(coreData.getSegundoNombre())
                    .primerApellido(coreData.getPrimerApellido())
                    .segundoApellido(coreData.getSegundoApellido())
                    .build();
            if (coreData.getEspecialidades() != null && !coreData.getEspecialidades().isEmpty()) {
                especialidades = coreData.getEspecialidades().stream()
                        .map(e -> EspecialidadPdfDto.builder().nombre(e).build())
                        .collect(Collectors.toList());
            }
            firma = coreData.getFirma();
        } else {
            // Fallback: usar datos almacenados en la consulta
            String nombreMedico = consulta.getDatosConsulta() != null
                    ? consulta.getDatosConsulta().getMedicoTratante()
                    : null;
            persona = PersonaPdfDto.builder()
                    .primerNombre(nombreMedico)
                    .build();
            if (consulta.getDatosConsulta() != null && consulta.getDatosConsulta().getEspecialidad() != null) {
                especialidades = List.of(
                        EspecialidadPdfDto.builder()
                                .nombre(consulta.getDatosConsulta().getEspecialidad())
                                .build()
                );
            }
        }

        return MedicoPdfDto.builder()
                .persona(persona)
                .especialidades(especialidades)
                .firma(firma)
                .build();
    }
}