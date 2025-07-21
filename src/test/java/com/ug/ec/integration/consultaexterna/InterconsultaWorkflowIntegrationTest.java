package com.ug.ec.integration.consultaexterna;

import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaCommandHandler;
import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaQueryHandler;
import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.application.consultaexterna.queries.BuscarConsultaExternaPorIdQuery;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.enums.EstadoInterconsulta;
import com.ug.ec.domain.consultaexterna.enums.PrioridadInterconsulta;
import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
import com.ug.ec.domain.consultaexterna.exceptions.InterconsultaInvalidaException;
import com.ug.ec.domain.consultaexterna.services.DiagnosticoInterconsultaRulesService;
import com.ug.ec.domain.consultaexterna.services.InterconsultaValidationService;
import com.ug.ec.domain.consultaexterna.valueobjects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Interconsulta Workflow - Tests de Integración")
class InterconsultaWorkflowIntegrationTest {

    @Autowired
    private ConsultaExternaCommandHandler commandHandler;

    @Autowired
    private ConsultaExternaQueryHandler queryHandler;

    @Autowired
    private InterconsultaValidationService interconsultaValidationService;

    @Autowired
    private DiagnosticoInterconsultaRulesService diagnosticoRulesService;

    @Autowired
    @Qualifier("consultaExternaRepositoryImpl")
    private ConsultaExternaRepository consultaExternaRepository;

    private String consultaExternaId;

    @BeforeEach
    void setUp() {
        // Crear una consulta externa para usar en las pruebas
        consultaExternaId = crearConsultaExternaParaPruebas();
    }

    @AfterEach
    void tearDown() {
        // Limpiar datos de prueba
        if (consultaExternaId != null) {
            try {
                consultaExternaRepository.deleteById(consultaExternaId);
            } catch (Exception e) {
                // Ignorar errores al limpiar
            }
        }
    }

    @Test
    @DisplayName("Crear y recuperar interconsulta debe funcionar correctamente")
    void crearYRecuperarInterconsulta_DebeFuncionarCorrectamente() {
        // Arrange
        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
        ConsultaExternaDto consultaDto = queryHandler.handle(query);
        
        // Verificar que la consulta existe y no tiene interconsultas
        assertNotNull(consultaDto);
        assertTrue(consultaDto.getPlanTratamiento().getInterconsultas().isEmpty());
        
        // Act - Agregar una interconsulta a la consulta
        Interconsulta nuevaInterconsulta = crearInterconsultaCardiologia();
        
        // Validar la interconsulta antes de agregarla
        assertDoesNotThrow(() -> {
            interconsultaValidationService.validarInterconsulta(nuevaInterconsulta, "I10");
        });
        
        // Actualizar la consulta con la nueva interconsulta
        ConsultaExterna consultaExistente = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
        ConsultaExterna consultaActualizada = consultaExistente.agregarInterconsultas(List.of(nuevaInterconsulta));
        consultaExternaRepository.save(consultaActualizada);
        
        // Assert - Verificar que la interconsulta se guardó correctamente
        ConsultaExternaDto consultaActualizadaDto = queryHandler.handle(query);
        
        assertNotNull(consultaActualizadaDto);
        assertFalse(consultaActualizadaDto.getPlanTratamiento().getInterconsultas().isEmpty());
        assertEquals(1, consultaActualizadaDto.getPlanTratamiento().getInterconsultas().size());
        
        Interconsulta interconsultaGuardada = consultaActualizadaDto.getPlanTratamiento().getInterconsultas().get(0);
        assertEquals("CARDIOLOGÍA", interconsultaGuardada.getEspecialidad());
        assertEquals(EstadoInterconsulta.SOLICITADA, interconsultaGuardada.getEstado());
        assertEquals(PrioridadInterconsulta.MEDIA, interconsultaGuardada.getPrioridad());
        assertEquals("I10", interconsultaGuardada.getCodigoDiagnosticoRelacionado());
    }

    @Test
    @DisplayName("Crear interconsulta con especialidad inadecuada debe fallar")
    void crearInterconsultaConEspecialidadInadecuada_DebeFallar() {
        // Arrange
        Interconsulta interconsultaInvalida = crearInterconsultaCardiologia().toBuilder()
                .especialidad("DERMATOLOGÍA")
                .build();
        
        // Act & Assert
        InterconsultaInvalidaException exception = assertThrows(InterconsultaInvalidaException.class, () -> {
            interconsultaValidationService.validarInterconsulta(interconsultaInvalida, "I10");
        });
        
        assertTrue(exception.getMessage().contains("no es adecuada para el diagnóstico"));
    }

    @Test
    @DisplayName("Crear interconsulta con prioridad inadecuada debe fallar")
    void crearInterconsultaConPrioridadInadecuada_DebeFallar() {
        // Arrange
        Interconsulta interconsultaInvalida = crearInterconsultaCardiologia().toBuilder()
                .prioridad(PrioridadInterconsulta.BAJA)
                .codigoDiagnosticoRelacionado("I21") // Infarto agudo de miocardio (requiere URGENTE)
                .build();
        
        // Act & Assert
        InterconsultaInvalidaException exception = assertThrows(InterconsultaInvalidaException.class, () -> {
            interconsultaValidationService.validarInterconsulta(interconsultaInvalida, "I21");
        });
        
        assertTrue(exception.getMessage().contains("requiere una interconsulta URGENTE"));
    }

    @Test
    @DisplayName("Crear múltiples interconsultas debe funcionar correctamente")
    void crearMultiplesInterconsultas_DebeFuncionarCorrectamente() {
        // Arrange
        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
        
        // Act - Agregar múltiples interconsultas
        Interconsulta interconsultaCardiologia = crearInterconsultaCardiologia();
        Interconsulta interconsultaNeurologia = crearInterconsultaNeurologia();
        
        // Validar las interconsultas
        assertDoesNotThrow(() -> {
            interconsultaValidationService.validarInterconsulta(interconsultaCardiologia, "I10");
            interconsultaValidationService.validarInterconsulta(interconsultaNeurologia, "G40");
        });
        
        // Actualizar la consulta con las nuevas interconsultas
        ConsultaExterna consultaExistente = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
        ConsultaExterna consultaActualizada = consultaExistente.agregarInterconsultas(
                Arrays.asList(interconsultaCardiologia, interconsultaNeurologia));
        consultaExternaRepository.save(consultaActualizada);
        
        // Assert
        ConsultaExternaDto consultaActualizadaDto = queryHandler.handle(query);
        
        assertNotNull(consultaActualizadaDto);
        assertEquals(2, consultaActualizadaDto.getPlanTratamiento().getInterconsultas().size());
        
        // Verificar que ambas interconsultas se guardaron correctamente
        boolean tieneCardiologia = consultaActualizadaDto.getPlanTratamiento().getInterconsultas().stream()
                .anyMatch(i -> "CARDIOLOGÍA".equals(i.getEspecialidad()));
        boolean tieneNeurologia = consultaActualizadaDto.getPlanTratamiento().getInterconsultas().stream()
                .anyMatch(i -> "NEUROLOGÍA".equals(i.getEspecialidad()));
        
        assertTrue(tieneCardiologia, "Debe tener interconsulta de cardiología");
        assertTrue(tieneNeurologia, "Debe tener interconsulta de neurología");
    }

    @Test
    @DisplayName("Actualizar estado de interconsulta debe funcionar correctamente")
    void actualizarEstadoInterconsulta_DebeFuncionarCorrectamente() {
        // Arrange - Crear consulta con interconsulta
        Interconsulta interconsulta = crearInterconsultaCardiologia();
        ConsultaExterna consultaExistente = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
        ConsultaExterna consultaConInterconsulta = consultaExistente.agregarInterconsultas(List.of(interconsulta));
        consultaExternaRepository.save(consultaConInterconsulta);
        
        // Act - Actualizar estado de la interconsulta
        ConsultaExterna consultaParaActualizar = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
        List<Interconsulta> interconsultasActualizadas = new ArrayList<>();
        
        // Programar la interconsulta (cambiar estado a AGENDADA)
        for (Interconsulta i : consultaParaActualizar.getPlanTratamiento().getInterconsultas()) {
            if ("CARDIOLOGÍA".equals(i.getEspecialidad())) {
                interconsultasActualizadas.add(i.programar(LocalDateTime.now().plusDays(7)));
            } else {
                interconsultasActualizadas.add(i);
            }
        }
        
        // Crear nuevo plan de tratamiento con interconsultas actualizadas
        PlanTratamiento planActualizado = consultaParaActualizar.getPlanTratamiento().toBuilder()
                .interconsultas(interconsultasActualizadas)
                .build();
        
        // Actualizar la consulta con el nuevo plan
        ConsultaExterna consultaActualizada = consultaParaActualizar.actualizarPlanTratamiento(planActualizado);
        consultaExternaRepository.save(consultaActualizada);
        
        // Assert
        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
        ConsultaExternaDto consultaActualizadaDto = queryHandler.handle(query);
        
        assertNotNull(consultaActualizadaDto);
        assertEquals(1, consultaActualizadaDto.getPlanTratamiento().getInterconsultas().size());
        
        Interconsulta interconsultaActualizada = consultaActualizadaDto.getPlanTratamiento().getInterconsultas().get(0);
        assertEquals(EstadoInterconsulta.AGENDADA, interconsultaActualizada.getEstado());
        assertNotNull(interconsultaActualizada.getFechaProgramada());
    }

    // ========== MÉTODOS AUXILIARES ==========

    private String crearConsultaExternaParaPruebas() {
        CrearConsultaExternaCommand command = CrearConsultaExternaCommand.builder()
                .usuarioCreador("usuario_test")
                .datosFormulario(DatosFormulario.builder()
                        .numeroFormulario("HCU-002")
                        .establecimiento("Hospital Universitario")
                        .codigoEstablecimiento("001")
                        .build())
                .datosPaciente(DatosPaciente.builder()
                        .cedula("0987654321")
                        .numeroHistoriaClinica("HC-12345")
                        .primerNombre("Juan")
                        .apellidoPaterno("Pérez")
                        .fechaNacimiento(LocalDate.now().minusYears(45))
                        .sexo(com.ug.ec.domain.consultaexterna.enums.Sexo.MASCULINO)
                        .direccion("Dirección de prueba")
                        .build())
                .datosConsulta(DatosConsulta.builder()
                        .numeroConsulta("CE-123456")
                        .fechaConsulta(LocalDateTime.now())
                        .especialidad("Medicina General")
                        .medicoTratante("Dr. Ejemplo")
                        .tipoConsulta(TipoConsulta.PRIMERA_VEZ)
                        .motivoConsulta("Dolor abdominal")
                        .build())
                .anamnesis(Anamnesis.builder()
                        .enfermedadActual("Dolor abdominal de 2 días de evolución")
                        .build())
                .examenFisico(ExamenFisico.builder()
                        .signosVitales(SignosVitales.builder()
                                .presionArterial("140/90")
                                .frecuenciaCardiaca(new BigDecimal("75"))
                                .frecuenciaRespiratoria(new BigDecimal("16"))
                                .temperatura(new BigDecimal("36.5"))
                                .saturacionOxigeno(new BigDecimal("98"))
                                .build())
                        .build())
                .diagnosticos(List.of(
                        Diagnostico.builder()
                                .codigoCie10("I10")
                                .descripcion("Hipertensión esencial (primaria)")
                                .tipo(com.ug.ec.domain.consultaexterna.enums.TipoDiagnostico.PRINCIPAL)
                                .fechaDiagnostico(LocalDateTime.now())
                                .build(),
                        Diagnostico.builder()
                                .codigoCie10("G40")
                                .descripcion("Epilepsia")
                                .tipo(com.ug.ec.domain.consultaexterna.enums.TipoDiagnostico.SECUNDARIO)
                                .fechaDiagnostico(LocalDateTime.now())
                                .build()
                ))
                .planTratamiento(PlanTratamiento.builder()
                        .prescripciones(new ArrayList<>())
                        .indicacionesGenerales(List.of("Reposo relativo", "Dieta blanda"))
                        .interconsultas(new ArrayList<>())
                        .build())
                .build();
        
        return commandHandler.handle(command);
    }

    private Interconsulta crearInterconsultaCardiologia() {
        return Interconsulta.builder()
                .especialidad("CARDIOLOGÍA")
                .motivo("Evaluación por hipertensión arterial de difícil control")
                .observaciones("Paciente con valores de presión arterial elevados a pesar de tratamiento")
                .hallazgosRelevantes("Soplo sistólico grado II/VI en foco aórtico")
                .fechaSolicitud(LocalDateTime.now())
                .estado(EstadoInterconsulta.SOLICITADA)
                .prioridad(PrioridadInterconsulta.MEDIA)
                .medicoSolicitante("Dr. Juan Pérez")
                .codigoDiagnosticoRelacionado("I10")
                .descripcionDiagnosticoRelacionado("Hipertensión esencial (primaria)")
                .examenesRealizados(Arrays.asList("Electrocardiograma", "Ecocardiograma"))
                .resultadosExamenes("ECG: Hipertrofia ventricular izquierda")
                .preguntaEspecifica("¿Se requiere ajuste de medicación antihipertensiva?")
                .build();
    }

    private Interconsulta crearInterconsultaNeurologia() {
        return Interconsulta.builder()
                .especialidad("NEUROLOGÍA")
                .motivo("Evaluación por epilepsia")
                .observaciones("Paciente con crisis convulsivas recurrentes")
                .hallazgosRelevantes("Última crisis hace 2 semanas")
                .fechaSolicitud(LocalDateTime.now())
                .estado(EstadoInterconsulta.SOLICITADA)
                .prioridad(PrioridadInterconsulta.ALTA)
                .medicoSolicitante("Dr. Juan Pérez")
                .codigoDiagnosticoRelacionado("G40")
                .descripcionDiagnosticoRelacionado("Epilepsia")
                .examenesRealizados(Arrays.asList("Electroencefalograma", "Resonancia magnética cerebral"))
                .resultadosExamenes("EEG: Actividad epileptiforme temporal izquierda")
                .preguntaEspecifica("¿Se requiere ajuste de medicación anticonvulsivante?")
                .hallazgosRelevantes("Crisis convulsivas tónico-clónicas generalizadas")
                .build();
    }
}