package com.ug.ec.domain.consultaexterna;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaIncompletaException;
import com.ug.ec.domain.consultaexterna.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultaExterna - Tests de Inmutabilidad y Dominio")
class ConsultaExternaTest {

    private ConsultaExterna consultaBase;
    private DatosAuditoria auditoria;
    private DatosPaciente datosPaciente;
    private DatosConsulta datosConsulta;
    private Anamnesis anamnesis;
    private ExamenFisico examenFisico;
    private List<Diagnostico> diagnosticos;
    private PlanTratamiento planTratamiento;

    @BeforeEach
    void setUp() {
        // Crear datos de auditoría
        auditoria = DatosAuditoria.crearNuevo("usuario_test");
        
        // Crear datos de paciente
        datosPaciente = DatosPaciente.builder()
                .cedula("0987654321")
                .numeroHistoriaClinica("HC-12345")
                .primerNombre("Juan")
                .apellidoPaterno("Pérez")
                .build();
        
        // Crear datos de consulta
        datosConsulta = DatosConsulta.builder()
                .numeroConsulta("CE-123456")
                .fechaConsulta(LocalDateTime.now())
                .especialidad("Medicina General")
                .medicoTratante("Dr. Ejemplo")
                .tipoConsulta(TipoConsulta.PRIMERA_VEZ)
                .motivoConsulta("Dolor abdominal")
                .build();
        
        // Crear anamnesis
        anamnesis = Anamnesis.builder()
                .enfermedadActual("Dolor abdominal de 2 días de evolución")
                .build();
        
        // Crear examen físico con signos vitales
        examenFisico = ExamenFisico.builder()
                .signosVitales(SignosVitales.builder()
                        .presionSistolica(120)
                        .presionDiastolica(80)
                        .frecuenciaCardiaca(75)
                        .frecuenciaRespiratoria(16)
                        .temperatura(36.5)
                        .build())
                .hallazgosRelevantes("Abdomen blando, doloroso en epigastrio")
                .build();
        
        // Crear diagnósticos
        diagnosticos = new ArrayList<>();
        diagnosticos.add(Diagnostico.builder()
                .codigoCie10("K59.9")
                .descripcion("Trastorno funcional intestinal, no especificado")
                .tipo("PRINCIPAL")
                .build());
        
        // Crear plan de tratamiento
        planTratamiento = PlanTratamiento.builder()
                .prescripciones(List.of())
                .indicacionesGenerales(List.of("Reposo relativo", "Dieta blanda"))
                .build();
        
        // Crear consulta base
        consultaBase = ConsultaExterna.builder()
                .id("consulta-123")
                .numeroConsulta("CE-123456")
                .datosFormulario(DatosFormulario.builder()
                        .numeroFormulario("HCU-002")
                        .establecimiento("Hospital Universitario")
                        .codigoEstablecimiento("001")
                        .build())
                .datosPaciente(datosPaciente)
                .datosConsulta(datosConsulta)
                .anamnesis(anamnesis)
                .examenFisico(examenFisico)
                .diagnosticos(diagnosticos)
                .planTratamiento(planTratamiento)
                .estado(EstadoConsulta.INICIADA)
                .auditoria(auditoria)
                .camposAdicionales(new HashMap<>())
                .build();
    }

    @Nested
    @DisplayName("Tests de Inmutabilidad")
    class TestsInmutabilidad {

        @Test
        @DisplayName("Completar consulta debe retornar nueva instancia")
        void completar_DebeRetornarNuevaInstancia() {
            // Act
            ConsultaExterna consultaCompletada = consultaBase.completar();

            // Assert
            assertNotSame(consultaBase, consultaCompletada);
            assertEquals(EstadoConsulta.INICIADA, consultaBase.getEstado());
            assertEquals(EstadoConsulta.COMPLETADA, consultaCompletada.getEstado());
            
            // Verificar que la auditoría fue actualizada
            assertNotEquals(consultaBase.getAuditoria().getFechaActualizacion(), 
                           consultaCompletada.getAuditoria().getFechaActualizacion());
        }

        @Test
        @DisplayName("Agregar diagnóstico debe retornar nueva instancia")
        void agregarDiagnostico_DebeRetornarNuevaInstancia() {
            // Arrange
            Diagnostico nuevoDiagnostico = Diagnostico.builder()
                    .codigoCie10("K30")
                    .descripcion("Dispepsia funcional")
                    .tipo("SECUNDARIO")
                    .build();

            // Act
            ConsultaExterna consultaConNuevoDiagnostico = consultaBase.agregarDiagnostico(nuevoDiagnostico);

            // Assert
            assertNotSame(consultaBase, consultaConNuevoDiagnostico);
            assertEquals(1, consultaBase.getDiagnosticos().size());
            assertEquals(2, consultaConNuevoDiagnostico.getDiagnosticos().size());
            
            // Verificar que el diagnóstico original no se modificó
            assertFalse(consultaBase.getDiagnosticos().contains(nuevoDiagnostico));
            assertTrue(consultaConNuevoDiagnostico.getDiagnosticos().contains(nuevoDiagnostico));
        }

        @Test
        @DisplayName("Agregar diagnóstico nulo debe lanzar excepción")
        void agregarDiagnostico_ConDiagnosticoNulo_DebeLanzarExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                consultaBase.agregarDiagnostico(null);
            });
        }

        @Test
        @DisplayName("Actualizar plan de tratamiento debe retornar nueva instancia")
        void actualizarPlanTratamiento_DebeRetornarNuevaInstancia() {
            // Arrange
            PlanTratamiento nuevoPlan = PlanTratamiento.builder()
                    .prescripciones(List.of(
                            Prescripcion.builder()
                                    .medicamento("Omeprazol")
                                    .dosis("20mg")
                                    .frecuencia("Cada 12 horas")
                                    .build()
                    ))
                    .indicacionesGenerales(List.of("Dieta blanda", "Evitar irritantes"))
                    .build();

            // Act
            ConsultaExterna consultaConNuevoPlan = consultaBase.actualizarPlanTratamiento(nuevoPlan);

            // Assert
            assertNotSame(consultaBase, consultaConNuevoPlan);
            assertNotSame(consultaBase.getPlanTratamiento(), consultaConNuevoPlan.getPlanTratamiento());
            assertEquals(nuevoPlan, consultaConNuevoPlan.getPlanTratamiento());
        }

        @Test
        @DisplayName("Actualizar plan con nulo debe lanzar excepción")
        void actualizarPlanTratamiento_ConPlanNulo_DebeLanzarExcepcion() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                consultaBase.actualizarPlanTratamiento(null);
            });
        }

        @Test
        @DisplayName("Agregar interconsultas debe retornar nueva instancia")
        void agregarInterconsultas_DebeRetornarNuevaInstancia() {
            // Arrange
            List<Interconsulta> interconsultas = List.of(
                    Interconsulta.builder()
                            .especialidad("CARDIOLOGIA")
                            .motivo("Evaluación de arritmia")
                            .build()
            );

            // Act
            ConsultaExterna consultaConInterconsultas = consultaBase.agregarInterconsultas(interconsultas);

            // Assert
            assertNotSame(consultaBase, consultaConInterconsultas);
            assertEquals(0, consultaBase.getPlanTratamiento().getInterconsultas().size());
            assertEquals(1, consultaConInterconsultas.getPlanTratamiento().getInterconsultas().size());
        }
    }

    @Nested
    @DisplayName("Tests de Métodos de Consulta")
    class TestsMetodosConsulta {

        @Test
        @DisplayName("Es consulta primera vez debe retornar true")
        void esConsultaPrimeraVez_ConTipoPrimeraVez_DebeRetornarTrue() {
            // Act
            boolean resultado = consultaBase.esConsultaPrimeraVez();

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Es consulta primera vez debe retornar false para subsecuente")
        void esConsultaPrimeraVez_ConTipoSubsecuente_DebeRetornarFalse() {
            // Arrange
            ConsultaExterna consultaSubsecuente = consultaBase.toBuilder()
                    .datosConsulta(datosConsulta.toBuilder()
                            .tipoConsulta(TipoConsulta.SUBSECUENTE)
                            .build())
                    .build();

            // Act
            boolean resultado = consultaSubsecuente.esConsultaPrimeraVez();

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Está completada debe retornar false para consulta iniciada")
        void estaCompletada_ConEstadoIniciada_DebeRetornarFalse() {
            // Act
            boolean resultado = consultaBase.estaCompletada();

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Está completada debe retornar true para consulta completada")
        void estaCompletada_ConEstadoCompletada_DebeRetornarTrue() {
            // Arrange
            ConsultaExterna consultaCompletada = consultaBase.completar();

            // Act
            boolean resultado = consultaCompletada.estaCompletada();

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Cantidad diagnósticos debe retornar número correcto")
        void cantidadDiagnosticos_DebeRetornarNumeroCorrecto() {
            // Act
            int cantidad = consultaBase.cantidadDiagnosticos();

            // Assert
            assertEquals(1, cantidad);
        }

        @Test
        @DisplayName("Tiene diagnósticos principales debe retornar true")
        void tieneDiagnosticosPrincipales_ConDiagnosticoPrincipal_DebeRetornarTrue() {
            // Act
            boolean resultado = consultaBase.tieneDiagnosticosPrincipales();

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Obtener cédula paciente debe retornar cédula correcta")
        void obtenerCedulaPaciente_DebeRetornarCedulaCorrecta() {
            // Act
            String cedula = consultaBase.obtenerCedulaPaciente();

            // Assert
            assertEquals("0987654321", cedula);
        }

        @Test
        @DisplayName("Obtener número consulta debe retornar número correcto")
        void obtenerNumeroConsulta_DebeRetornarNumeroCorrecta() {
            // Act
            String numero = consultaBase.obtenerNumeroConsulta();

            // Assert
            assertEquals("CE-123456", numero);
        }
    }

    @Nested
    @DisplayName("Tests de Validaciones")
    class TestsValidaciones {

        @Test
        @DisplayName("Validar completitud debe pasar con datos completos")
        void validarCompletitud_ConDatosCompletos_DebeNoLanzarExcepcion() {
            // Act & Assert
            assertDoesNotThrow(() -> {
                consultaBase.validarCompletitud();
            });
        }

        @Test
        @DisplayName("Validar completitud debe fallar sin diagnósticos")
        void validarCompletitud_SinDiagnosticos_DebeLanzarExcepcion() {
            // Arrange
            ConsultaExterna consultaSinDiagnosticos = consultaBase.toBuilder()
                    .diagnosticos(null)
                    .build();

            // Act & Assert
            ConsultaExternaIncompletaException exception = assertThrows(
                    ConsultaExternaIncompletaException.class,
                    () -> consultaSinDiagnosticos.validarCompletitud()
            );

            assertEquals("La consulta debe tener al menos un diagnóstico", exception.getMessage());
        }

        @Test
        @DisplayName("Validar completitud debe fallar sin anamnesis")
        void validarCompletitud_SinAnamnesis_DebeLanzarExcepcion() {
            // Arrange
            ConsultaExterna consultaSinAnamnesis = consultaBase.toBuilder()
                    .anamnesis(null)
                    .build();

            // Act & Assert
            ConsultaExternaIncompletaException exception = assertThrows(
                    ConsultaExternaIncompletaException.class,
                    () -> consultaSinAnamnesis.validarCompletitud()
            );

            assertEquals("La anamnesis es obligatoria", exception.getMessage());
        }

        @Test
        @DisplayName("Validar completitud debe fallar sin examen físico")
        void validarCompletitud_SinExamenFisico_DebeLanzarExcepcion() {
            // Arrange
            ConsultaExterna consultaSinExamenFisico = consultaBase.toBuilder()
                    .examenFisico(null)
                    .build();

            // Act & Assert
            ConsultaExternaIncompletaException exception = assertThrows(
                    ConsultaExternaIncompletaException.class,
                    () -> consultaSinExamenFisico.validarCompletitud()
            );

            assertEquals("El examen físico es obligatorio", exception.getMessage());
        }

        @Test
        @DisplayName("Validar completitud debe fallar sin datos de paciente")
        void validarCompletitud_SinDatosPaciente_DebeLanzarExcepcion() {
            // Arrange
            ConsultaExterna consultaSinDatosPaciente = consultaBase.toBuilder()
                    .datosPaciente(null)
                    .build();

            // Act & Assert
            ConsultaExternaIncompletaException exception = assertThrows(
                    ConsultaExternaIncompletaException.class,
                    () -> consultaSinDatosPaciente.validarCompletitud()
            );

            assertEquals("Los datos del paciente son obligatorios", exception.getMessage());
        }

        @Test
        @DisplayName("Puede completarse debe retornar true con datos completos")
        void puedeCompletarse_ConDatosCompletos_DebeRetornarTrue() {
            // Act
            boolean resultado = consultaBase.puedeCompletarse();

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Puede completarse debe retornar false sin datos completos")
        void puedeCompletarse_SinDatosCompletos_DebeRetornarFalse() {
            // Arrange
            ConsultaExterna consultaIncompleta = consultaBase.toBuilder()
                    .diagnosticos(null)
                    .build();

            // Act
            boolean resultado = consultaIncompleta.puedeCompletarse();

            // Assert
            assertFalse(resultado);
        }
    }

    @Nested
    @DisplayName("Tests de Casos Edge")
    class TestsCasosEdge {

        @Test
        @DisplayName("Obtener cédula paciente con datos nulos debe retornar DESCONOCIDO")
        void obtenerCedulaPaciente_ConDatosNulos_DebeRetornarDesconocido() {
            // Arrange
            ConsultaExterna consultaSinDatos = consultaBase.toBuilder()
                    .datosPaciente(null)
                    .build();

            // Act
            String cedula = consultaSinDatos.obtenerCedulaPaciente();

            // Assert
            assertEquals("DESCONOCIDO", cedula);
        }

        @Test
        @DisplayName("Obtener número consulta con datos nulos debe retornar SIN_NUMERO")
        void obtenerNumeroConsulta_ConDatosNulos_DebeRetornarSinNumero() {
            // Arrange
            ConsultaExterna consultaSinNumero = consultaBase.toBuilder()
                    .numeroConsulta(null)
                    .datosConsulta(null)
                    .build();

            // Act
            String numero = consultaSinNumero.obtenerNumeroConsulta();

            // Assert
            assertEquals("SIN_NUMERO", numero);
        }

        @Test
        @DisplayName("Agregar interconsultas vacías debe retornar misma instancia")
        void agregarInterconsultas_ConListaVacia_DebeRetornarMismaInstancia() {
            // Arrange
            List<Interconsulta> interconsultasVacias = List.of();

            // Act
            ConsultaExterna resultado = consultaBase.agregarInterconsultas(interconsultasVacias);

            // Assert
            assertSame(consultaBase, resultado);
        }

        @Test
        @DisplayName("Agregar interconsultas nulas debe retornar misma instancia")
        void agregarInterconsultas_ConListaNula_DebeRetornarMismaInstancia() {
            // Act
            ConsultaExterna resultado = consultaBase.agregarInterconsultas(null);

            // Assert
            assertSame(consultaBase, resultado);
        }

        @Test
        @DisplayName("Cantidad diagnósticos con lista nula debe retornar 0")
        void cantidadDiagnosticos_ConListaNula_DebeRetornarCero() {
            // Arrange
            ConsultaExterna consultaSinDiagnosticos = consultaBase.toBuilder()
                    .diagnosticos(null)
                    .build();

            // Act
            int cantidad = consultaSinDiagnosticos.cantidadDiagnosticos();

            // Assert
            assertEquals(0, cantidad);
        }
    }
}