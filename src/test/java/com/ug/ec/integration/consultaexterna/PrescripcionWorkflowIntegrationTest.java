//package com.ug.ec.integration.consultaexterna;
//
//import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
//import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
//import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaCommandHandler;
//import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaQueryHandler;
//import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
//import com.ug.ec.application.consultaexterna.queries.BuscarConsultaExternaPorIdQuery;
//import com.ug.ec.domain.consultaexterna.ConsultaExterna;
//import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
//import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
//import com.ug.ec.domain.consultaexterna.exceptions.PrescripcionInvalidaException;
//import com.ug.ec.domain.consultaexterna.services.InteraccionesMedicamentosService;
//import com.ug.ec.domain.consultaexterna.services.PrescripcionValidationService;
//import com.ug.ec.domain.consultaexterna.valueobjects.*;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@DisplayName("Prescripcion Workflow - Tests de Integración")
//class PrescripcionWorkflowIntegrationTest {
//
//    @Autowired
//    private ConsultaExternaCommandHandler commandHandler;
//
//    @Autowired
//    private ConsultaExternaQueryHandler queryHandler;
//
//    @Autowired
//    private PrescripcionValidationService prescripcionValidationService;
//
//    @Autowired
//    private InteraccionesMedicamentosService interaccionesService;
//
//    @Autowired
//    @Qualifier("consultaExternaRepositoryImpl")
//    private ConsultaExternaRepository consultaExternaRepository;
//
//    private String consultaExternaId;
//
//    @BeforeEach
//    void setUp() {
//        // Crear una consulta externa para usar en las pruebas
//        consultaExternaId = crearConsultaExternaParaPruebas();
//    }
//
//    @AfterEach
//    void tearDown() {
//        // Limpiar datos de prueba
//        if (consultaExternaId != null) {
//            try {
//                consultaExternaRepository.deleteById(consultaExternaId);
//            } catch (Exception e) {
//                // Ignorar errores al limpiar
//            }
//        }
//    }
//
//    @Test
//    @DisplayName("Crear y recuperar prescripción debe funcionar correctamente")
//    void crearYRecuperarPrescripcion_DebeFuncionarCorrectamente() {
//        // Arrange
//        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
//        ConsultaExternaDto consultaDto = queryHandler.handle(query);
//
//        // Verificar que la consulta existe y no tiene prescripciones
//        assertNotNull(consultaDto);
//        assertTrue(consultaDto.getPlanTratamiento().getPrescripciones().isEmpty());
//
//        // Act - Agregar una prescripción a la consulta
//        Prescripcion nuevaPrescripcion = crearPrescripcionParacetamol();
//
//        // Validar la prescripción antes de agregarla
//        DatosPaciente datosPaciente = consultaDto.getDatosPaciente();
//        List<Prescripcion> prescripcionesExistentes = new ArrayList<>();
//
//        assertDoesNotThrow(() -> {
//            prescripcionValidationService.validarPrescripcion(nuevaPrescripcion, prescripcionesExistentes, datosPaciente);
//        });
//
//        // Actualizar la consulta con la nueva prescripción
//        ConsultaExterna consultaExistente = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
//        PlanTratamiento planActualizado = consultaExistente.getPlanTratamiento().toBuilder()
//                .prescripciones(List.of(nuevaPrescripcion))
//                .build();
//        ConsultaExterna consultaActualizada = consultaExistente.actualizarPlanTratamiento(planActualizado);
//        consultaExternaRepository.save(consultaActualizada);
//
//        // Assert - Verificar que la prescripción se guardó correctamente
//        ConsultaExternaDto consultaActualizadaDto = queryHandler.handle(query);
//
//        assertNotNull(consultaActualizadaDto);
//        assertFalse(consultaActualizadaDto.getPlanTratamiento().getPrescripciones().isEmpty());
//        assertEquals(1, consultaActualizadaDto.getPlanTratamiento().getPrescripciones().size());
//
//        Prescripcion prescripcionGuardada = consultaActualizadaDto.getPlanTratamiento().getPrescripciones().get(0);
//        assertEquals("Paracetamol", prescripcionGuardada.getMedicamento());
//        assertEquals("500 mg", prescripcionGuardada.getDosis());
//        assertEquals("cada 8 horas", prescripcionGuardada.getFrecuencia());
//        assertEquals("Oral", prescripcionGuardada.getViaAdministracion());
//        assertEquals(5, prescripcionGuardada.getDuracionDias());
//    }
//
//    @Test
//    @DisplayName("Crear prescripción con dosis excesiva debe fallar")
//    void crearPrescripcionConDosisExcesiva_DebeFallar() {
//        // Arrange
//        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
//        ConsultaExternaDto consultaDto = queryHandler.handle(query);
//
//        Prescripcion prescripcionDosisExcesiva = Prescripcion.builder()
//                .medicamento("Paracetamol")
//                .dosis("2000 mg")
//                .frecuencia("cada 4 horas")
//                .viaAdministracion("Oral")
//                .duracionDias(5)
//                .fechaPrescripcion(LocalDate.now())
//                .build();
//
//        DatosPaciente datosPaciente = consultaDto.getDatosPaciente();
//        List<Prescripcion> prescripcionesExistentes = new ArrayList<>();
//
//        // Act & Assert
//        PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//            prescripcionValidationService.validarPrescripcion(prescripcionDosisExcesiva, prescripcionesExistentes, datosPaciente);
//        });
//
//        assertTrue(exception.getMessage().contains("excede el máximo recomendado"));
//    }
//
//    @Test
//    @DisplayName("Crear múltiples prescripciones debe funcionar correctamente")
//    void crearMultiplesPrescripciones_DebeFuncionarCorrectamente() {
//        // Arrange
//        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
//        ConsultaExternaDto consultaDto = queryHandler.handle(query);
//
//        // Act - Agregar múltiples prescripciones
//        Prescripcion paracetamol = crearPrescripcionParacetamol();
//        Prescripcion ibuprofeno = crearPrescripcionIbuprofeno();
//
//        // Validar las prescripciones
//        DatosPaciente datosPaciente = consultaDto.getDatosPaciente();
//        List<Prescripcion> prescripcionesExistentes = new ArrayList<>();
//
//        assertDoesNotThrow(() -> {
//            prescripcionValidationService.validarPrescripcion(paracetamol, prescripcionesExistentes, datosPaciente);
//            // Después de validar la primera, la agregamos a las existentes para validar interacciones
//            prescripcionesExistentes.add(paracetamol);
//            prescripcionValidationService.validarPrescripcion(ibuprofeno, prescripcionesExistentes, datosPaciente);
//        });
//
//        // Actualizar la consulta con las nuevas prescripciones
//        ConsultaExterna consultaExistente = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
//        PlanTratamiento planActualizado = consultaExistente.getPlanTratamiento().toBuilder()
//                .prescripciones(Arrays.asList(paracetamol, ibuprofeno))
//                .build();
//        ConsultaExterna consultaActualizada = consultaExistente.actualizarPlanTratamiento(planActualizado);
//        consultaExternaRepository.save(consultaActualizada);
//
//        // Assert
//        ConsultaExternaDto consultaActualizadaDto = queryHandler.handle(query);
//
//        assertNotNull(consultaActualizadaDto);
//        assertEquals(2, consultaActualizadaDto.getPlanTratamiento().getPrescripciones().size());
//
//        // Verificar que ambas prescripciones se guardaron correctamente
//        boolean tieneParacetamol = consultaActualizadaDto.getPlanTratamiento().getPrescripciones().stream()
//                .anyMatch(p -> "Paracetamol".equals(p.getMedicamento()));
//        boolean tieneIbuprofeno = consultaActualizadaDto.getPlanTratamiento().getPrescripciones().stream()
//                .anyMatch(p -> "Ibuprofeno".equals(p.getMedicamento()));
//
//        assertTrue(tieneParacetamol, "Debe tener prescripción de paracetamol");
//        assertTrue(tieneIbuprofeno, "Debe tener prescripción de ibuprofeno");
//    }
//
//    @Test
//    @DisplayName("Crear prescripciones con interacciones debe fallar")
//    void crearPrescripcionesConInteracciones_DebeFallar() {
//        // Arrange
//        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
//        ConsultaExternaDto consultaDto = queryHandler.handle(query);
//
//        // Crear prescripciones con interacciones conocidas
//        Prescripcion warfarina = Prescripcion.builder()
//                .medicamento("Warfarina")
//                .dosis("5 mg")
//                .frecuencia("cada 24 horas")
//                .viaAdministracion("Oral")
//                .duracionDias(30)
//                .fechaPrescripcion(LocalDate.now())
//                .build();
//
//        Prescripcion aspirina = Prescripcion.builder()
//                .medicamento("Aspirina")
//                .dosis("100 mg")
//                .frecuencia("cada 24 horas")
//                .viaAdministracion("Oral")
//                .duracionDias(30)
//                .fechaPrescripcion(LocalDate.now())
//                .build();
//
//        // Validar la primera prescripción
//        DatosPaciente datosPaciente = consultaDto.getDatosPaciente();
//        List<Prescripcion> prescripcionesExistentes = new ArrayList<>();
//
//        assertDoesNotThrow(() -> {
//            prescripcionValidationService.validarPrescripcion(warfarina, prescripcionesExistentes, datosPaciente);
//        });
//
//        // Agregar la primera prescripción a la consulta
//        ConsultaExterna consultaExistente = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
//        PlanTratamiento planActualizado = consultaExistente.getPlanTratamiento().toBuilder()
//                .prescripciones(List.of(warfarina))
//                .build();
//        ConsultaExterna consultaActualizada = consultaExistente.actualizarPlanTratamiento(planActualizado);
//        consultaExternaRepository.save(consultaActualizada);
//
//        // Intentar agregar la segunda prescripción que interactúa con la primera
//        ConsultaExternaDto consultaConWarfarina = queryHandler.handle(query);
//        List<Prescripcion> prescripcionesConWarfarina = consultaConWarfarina.getPlanTratamiento().getPrescripciones();
//
//        // Act & Assert
//        PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//            prescripcionValidationService.validarPrescripcion(aspirina, prescripcionesConWarfarina, datosPaciente);
//        });
//
//        assertTrue(exception.getMessage().contains("interacciones medicamentosas"));
//    }
//
//    @Test
//    @DisplayName("Actualizar prescripción debe funcionar correctamente")
//    void actualizarPrescripcion_DebeFuncionarCorrectamente() {
//        // Arrange - Crear consulta con prescripción
//        Prescripcion prescripcion = crearPrescripcionParacetamol();
//        ConsultaExterna consultaExistente = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
//        PlanTratamiento planConPrescripcion = consultaExistente.getPlanTratamiento().toBuilder()
//                .prescripciones(List.of(prescripcion))
//                .build();
//        ConsultaExterna consultaConPrescripcion = consultaExistente.actualizarPlanTratamiento(planConPrescripcion);
//        consultaExternaRepository.save(consultaConPrescripcion);
//
//        // Act - Actualizar la prescripción
//        ConsultaExterna consultaParaActualizar = consultaExternaRepository.findById(consultaExternaId).orElseThrow();
//        List<Prescripcion> prescripcionesActualizadas = new ArrayList<>();
//
//        // Modificar la prescripción (cambiar dosis y duración)
//        for (Prescripcion p : consultaParaActualizar.getPlanTratamiento().getPrescripciones()) {
//            if ("Paracetamol".equals(p.getMedicamento())) {
//                Prescripcion prescripcionActualizada = p.toBuilder()
//                        .dosis("650 mg")
//                        .duracionDias(7)
//                        .build();
//                prescripcionesActualizadas.add(prescripcionActualizada);
//            } else {
//                prescripcionesActualizadas.add(p);
//            }
//        }
//
//        // Crear nuevo plan de tratamiento con prescripciones actualizadas
//        PlanTratamiento planActualizado = consultaParaActualizar.getPlanTratamiento().toBuilder()
//                .prescripciones(prescripcionesActualizadas)
//                .build();
//
//        // Actualizar la consulta con el nuevo plan
//        ConsultaExterna consultaActualizada = consultaParaActualizar.actualizarPlanTratamiento(planActualizado);
//        consultaExternaRepository.save(consultaActualizada);
//
//        // Assert
//        BuscarConsultaExternaPorIdQuery query = new BuscarConsultaExternaPorIdQuery(consultaExternaId);
//        ConsultaExternaDto consultaActualizadaDto = queryHandler.handle(query);
//
//        assertNotNull(consultaActualizadaDto);
//        assertEquals(1, consultaActualizadaDto.getPlanTratamiento().getPrescripciones().size());
//
//        Prescripcion prescripcionActualizada = consultaActualizadaDto.getPlanTratamiento().getPrescripciones().get(0);
//        assertEquals("650 mg", prescripcionActualizada.getDosis());
//        assertEquals(7, prescripcionActualizada.getDuracionDias());
//    }
//
//    // ========== MÉTODOS AUXILIARES ==========
//
//    private String crearConsultaExternaParaPruebas() {
//        CrearConsultaExternaCommand command = CrearConsultaExternaCommand.builder()
//                .usuarioCreador("usuario_test")
//                .datosFormulario(DatosFormulario.builder()
//                        .numeroFormulario("HCU-002")
//                        .establecimiento("Hospital Universitario")
//                        .codigoEstablecimiento("001")
//                        .build())
//                .datosPaciente(DatosPaciente.builder()
//                        .cedula("0987654321")
//                        .numeroHistoriaClinica("HC-12345")
//                        .primerNombre("Juan")
//                        .apellidoPaterno("Pérez")
//                        .fechaNacimiento(LocalDate.now().minusYears(45))
//                        .sexo(com.ug.ec.domain.consultaexterna.enums.Sexo.MASCULINO)
//                        .direccion("Dirección de prueba")
//                        .build())
//                .datosConsulta(DatosConsulta.builder()
//                        .numeroConsulta("CE-123456")
//                        .fechaConsulta(LocalDateTime.now())
//                        .especialidad("Medicina General")
//                        .medicoTratante("Dr. Ejemplo")
//                        .tipoConsulta(TipoConsulta.PRIMERA_VEZ)
//                        .motivoConsulta("Dolor abdominal")
//                        .build())
//                .anamnesis(Anamnesis.builder()
//                        .enfermedadActual("Dolor abdominal de 2 días de evolución")
//                        .build())
//                .examenFisico(ExamenFisico.builder()
//                        .signosVitales(SignosVitales.builder()
//                                .presionArterial("120/80")
//                                .frecuenciaCardiaca(new BigDecimal("75"))
//                                .frecuenciaRespiratoria(new BigDecimal("16"))
//                                .temperatura(new BigDecimal("36.5"))
//                                .saturacionOxigeno(new BigDecimal("98"))
//                                .build())
//                        .build())
//                .diagnosticos(List.of(
//                        Diagnostico.builder()
//                                .codigoCie10("K59.9")
//                                .descripcion("Trastorno funcional intestinal, no especificado")
//                                .tipo(com.ug.ec.domain.consultaexterna.enums.TipoDiagnostico.PRINCIPAL)
//                                .fechaDiagnostico(LocalDateTime.now())
//                                .build()
//                ))
//                .planTratamiento(PlanTratamiento.builder()
//                        .prescripciones(new ArrayList<>())
//                        .indicacionesGenerales(List.of("Reposo relativo", "Dieta blanda"))
//                        .interconsultas(new ArrayList<>())
//                        .build())
//                .build();
//
//        return commandHandler.handle(command);
//    }
//
//    private Prescripcion crearPrescripcionParacetamol() {
//        return Prescripcion.builder()
//                .medicamento("Paracetamol")
//                .dosis("500 mg")
//                .frecuencia("cada 8 horas")
//                .viaAdministracion("Oral")
//                .duracionDias(5)
//                .indicaciones("Tomar con alimentos")
//                .fechaPrescripcion(LocalDate.now())
//                .build();
//    }
//
//    private Prescripcion crearPrescripcionIbuprofeno() {
//        return Prescripcion.builder()
//                .medicamento("Ibuprofeno")
//                .dosis("400 mg")
//                .frecuencia("cada 8 horas")
//                .viaAdministracion("Oral")
//                .duracionDias(5)
//                .indicaciones("Tomar después de las comidas")
//                .fechaPrescripcion(LocalDate.now())
//                .build();
//    }
//}