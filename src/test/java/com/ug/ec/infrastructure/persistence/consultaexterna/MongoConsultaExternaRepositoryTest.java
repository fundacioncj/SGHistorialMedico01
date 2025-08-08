//package com.ug.ec.infrastructure.persistence.consultaexterna;
//
//import com.ug.ec.domain.consultaexterna.ConsultaExterna;
//import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
//import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
//import com.ug.ec.domain.consultaexterna.valueobjects.*;
//import com.ug.ec.infrastructure.persistence.consultaexterna.MongoConsultaExternaRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataMongoTest
//@ActiveProfiles("test")
//class MongoConsultaExternaRepositoryTest {
//
//    @Autowired
//    private MongoConsultaExternaRepository repository;
//
//    private ConsultaExterna consultaExterna;
//
//    @BeforeEach
//    void setUp() {
//        // Limpiar la base de datos antes de cada test
//        repository.deleteAll();
//
//        // Crear una consulta externa de prueba
//        consultaExterna = crearConsultaExternaPrueba();
//    }
//
//    @Test
//    void save_DebeGuardarConsultaExterna() {
//        // Act
//        ConsultaExterna consultaGuardada = null;
//
//        // Assert
//        assertNotNull(consultaGuardada);
//        assertNotNull(consultaGuardada.getId());
//        assertEquals("CE-123456", consultaGuardada.getNumeroConsulta());
//        assertEquals("0987654321", consultaGuardada.getDatosPaciente().getCedula());
//    }
//
//    @Test
//    void findById_DebeRetornarConsulta_CuandoExiste() {
//        // Arrange
////        ConsultaExterna consultaGuardada = repository.save(consultaExterna);
////
//        // Act
//        Optional<ConsultaExternaDocument> consultaEncontrada = repository.findById(consultaGuardada.getId());
//
//        // Assert
//        assertTrue(consultaEncontrada.isPresent());
//        assertEquals(consultaGuardada.getId(), consultaEncontrada.get().getId());
//        assertEquals("CE-123456", consultaEncontrada.get().getNumeroConsulta());
//    }
//
//    @Test
//    void findById_DebeRetornarEmpty_CuandoNoExiste() {
//        // Act
//        Optional<ConsultaExternaDocument> consultaEncontrada = repository.findById("id-inexistente");
//
//        // Assert
//        assertFalse(consultaEncontrada.isPresent());
//    }
//
//    @Test
//    void findByNumeroConsulta_DebeRetornarConsulta_CuandoExiste() {
//        // Arrange
////        repository.save(consultaExterna);
//
//        // Act
//        Optional<ConsultaExternaDocument> consultaEncontrada = repository.findByNumeroConsulta("CE-123456");
//
//        // Assert
//        assertTrue(consultaEncontrada.isPresent());
//        assertEquals("CE-123456", consultaEncontrada.get().getNumeroConsulta());
//    }
//
//    @Test
//    void existsByNumeroConsulta_DebeRetornarTrue_CuandoExiste() {
//        // Arrange
//        repository.save(consultaExterna);
//
//        // Act
//        boolean existe = repository.existsByNumeroConsulta("CE-123456");
//
//        // Assert
//        assertTrue(existe);
//    }
//
//    @Test
//    void findByDatosPacienteCedula_DebeRetornarConsultas() {
//        // Arrange
//        repository.save(consultaExterna);
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // Act
//        Page<ConsultaExternaDocument> consultasPage = repository.findByDatosPacienteCedula("0987654321", pageable);
//
//        // Assert
//        assertNotNull(consultasPage);
//        assertEquals(1, consultasPage.getTotalElements());
//        assertEquals("0987654321", consultasPage.getContent().get(0).getDatosPaciente().getCedula());
//    }
//
//    @Test
//    void deleteById_DebeEliminarConsulta() {
//        // Arrange
//        ConsultaExterna consultaGuardada = repository.save(consultaExterna);
//        String id = consultaGuardada.getId();
//
//        // Act
//        repository.deleteById(id);
//        Optional<ConsultaExternaDocument> consultaRecuperada = repository.findById(id);
//
//        // Assert
//        assertFalse(consultaRecuperada.isPresent());
//    }
//
//    @Test
//    void findByDatosConsultaFechaConsultaBetween_DebeRetornarConsultasEnRango() {
//        // Arrange
////        repository.save(consultaExterna);
//        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(1);
//        LocalDateTime fechaFin = LocalDateTime.now().plusDays(1);
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // Act
//        Page<ConsultaExternaDocument> consultasPage = repository.findByDatosConsultaFechaConsultaBetween(
//                fechaInicio, fechaFin, pageable);
//
//        // Assert
//        assertNotNull(consultasPage);
//        assertEquals(1, consultasPage.getTotalElements());
//    }
//
//    private ConsultaExterna crearConsultaExternaPrueba() {
//        DatosFormulario datosFormulario = DatosFormulario.builder()
//                .numeroFormulario("HCU-002")
//                .establecimiento("Hospital Universitario")
//                .codigoEstablecimiento("001")
//                .build();
//
//        DatosPaciente datosPaciente = DatosPaciente.builder()
//                .cedula("0987654321")
//                .numeroHistoriaClinica("HC-12345")
//                .primerNombre("Juan")
//                .apellidoPaterno("Pérez")
//                .build();
//
//        DatosConsulta datosConsulta = DatosConsulta.builder()
//                .numeroConsulta("CE-123456")
//                .fechaConsulta(LocalDateTime.now())
//                .especialidad("Medicina General")
//                .medicoTratante("Dr. Ejemplo")
//                .tipoConsulta(TipoConsulta.PRIMERA_VEZ)
//                .motivoConsulta("Dolor abdominal")
//                .build();
//
//        Anamnesis anamnesis = Anamnesis.builder()
//                .enfermedadActual("Dolor abdominal de 2 días de evolución")
//                .build();
//
//        ExamenFisico examenFisico = ExamenFisico.builder()
//                .signosVitales(SignosVitales.builder()
//
//                        .frecuenciaCardiaca(BigDecimal.valueOf(75))
//                        .frecuenciaRespiratoria(BigDecimal.valueOf(16))
//                        .temperatura(BigDecimal.valueOf(36.5))
//                        .build())
//                .build();
//
//        List<Diagnostico> diagnosticos = new ArrayList<>();
//        diagnosticos.add(Diagnostico.builder()
//                .codigoCie10("K59.9")
//                .descripcion("Trastorno funcional intestinal, no especificado")
//                .build());
//
//        PlanTratamiento planTratamiento = PlanTratamiento.builder()
//                .prescripciones(new ArrayList<>())
//                .indicacionesGenerales(List.of("Reposo relativo", "Dieta blanda"))
//                .build();
//
//        DatosAuditoria auditoria = DatosAuditoria.builder()
//                .fechaCreacion(LocalDateTime.now())
//                .creadoPor("usuario_test")
//                .modificadoPor("usuario_test")
//                .build();
//
//        return ConsultaExterna.builder()
//                .numeroConsulta("CE-123456")
//                .datosFormulario(datosFormulario)
//                .datosPaciente(datosPaciente)
//                .datosConsulta(datosConsulta)
//                .anamnesis(anamnesis)
//                .examenFisico(examenFisico)
//                .diagnosticos(diagnosticos)
//                .planTratamiento(planTratamiento)
//                .estado(EstadoConsulta.INICIADA)
//                .auditoria(auditoria)
//                .camposAdicionales(new HashMap<>())
//                .build();
//    }
//}