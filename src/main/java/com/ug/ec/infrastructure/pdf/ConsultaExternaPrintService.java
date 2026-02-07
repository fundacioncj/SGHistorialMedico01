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

    /**
     * Genera un PDF del formulario HCU-002 de Consulta Externa
     *
     * @param consultaId ID de la consulta externa
     * @return byte[] con el contenido del PDF
     */
    public byte[] generarPdf(String consultaId) {
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

        // 4. Construir DTOs para el PDF
        PacientePdfDto paciente = construirPacienteDto(consulta, historiaClinica);
        MedicoPdfDto medico = construirMedicoDto(consulta);

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
     * @param citaId ID de la cita
     * @return byte[] con el contenido del PDF
     */
    public byte[] generarPdfPorCitaId(String citaId) {
        log.info("Generando PDF para consulta externa por cita ID: {}", citaId);

        var query = com.ug.ec.application.consultaexterna.queries.BuscarConsultaExternaPorCitaIdQuery.builder()
                .id(citaId)
                .build();
        ConsultaExternaDto consulta = queryHandler.handle(query);

        if (consulta == null) {
            throw new ConsultaExternaNotFoundException("Consulta externa no encontrada para cita ID: " + citaId);
        }

        return generarPdf(consulta.getId());
    }

    private PacientePdfDto construirPacienteDto(ConsultaExternaDto consulta, HistoriaClinica historiaClinica) {
        PersonaPdfDto persona = PersonaPdfDto.builder()
                .cedula(consulta.getCedulaPaciente())
                .build();

        // Si tenemos datos adicionales de la historia clínica, podríamos usarlos
        // Por ahora solo tenemos la cédula en la consulta

        return PacientePdfDto.builder()
                .persona(persona)
                .edad(null) // Se puede calcular si se tiene la fecha de nacimiento
                .build();
    }

    private MedicoPdfDto construirMedicoDto(ConsultaExternaDto consulta) {
        // Obtener datos del médico desde la auditoría o datos de consulta
        String nombreMedico = null;
        if (consulta.getDatosConsulta() != null) {
            nombreMedico = consulta.getDatosConsulta().getMedicoTratante();
        }

        PersonaPdfDto persona = PersonaPdfDto.builder()
                .primerNombre(nombreMedico)
                .build();

        // Obtener especialidad si existe
        List<EspecialidadPdfDto> especialidades = null;
        if (consulta.getDatosConsulta() != null && consulta.getDatosConsulta().getEspecialidad() != null) {
            especialidades = List.of(
                    EspecialidadPdfDto.builder()
                            .nombre(consulta.getDatosConsulta().getEspecialidad())
                            .build()
            );
        }

        return MedicoPdfDto.builder()
                .persona(persona)
                .especialidades(especialidades)
                .firma(null) // La firma se puede agregar si existe en la base de datos
                .build();
    }
}