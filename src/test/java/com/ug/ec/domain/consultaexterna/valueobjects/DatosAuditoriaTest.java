package com.ug.ec.domain.consultaexterna.valueobjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DatosAuditoria - Tests de Inmutabilidad")
class DatosAuditoriaTest {

    private DatosAuditoria datosAuditoria;
    private LocalDateTime fechaReferencia;

    @BeforeEach
    void setUp() {
        fechaReferencia = LocalDateTime.now();
        datosAuditoria = DatosAuditoria.builder()
                .fechaCreacion(fechaReferencia.minusDays(1))
                .fechaActualizacion(fechaReferencia.minusHours(2))
                .creadoPor("usuario_creador")
                .modificadoPor("usuario_modificador")
                .build();
    }

    @Nested
    @DisplayName("Tests de Métodos de Fábrica")
    class TestsMetodosFabrica {

        @Test
        @DisplayName("Crear nuevo debe generar instancia con fechas actuales")
        void crearNuevo_DebeGenerarInstanciaConFechasActuales() {
            // Act
            DatosAuditoria resultado = DatosAuditoria.crearNuevo("usuario_test");

            // Assert
            assertNotNull(resultado.getFechaCreacion());
            assertNotNull(resultado.getFechaActualizacion());
            assertEquals("usuario_test", resultado.getCreadoPor());
            assertEquals("usuario_test", resultado.getModificadoPor());
            assertTrue(resultado.esMismoCreadorYModificador());
        }

        @Test
        @DisplayName("Crear nuevo debe generar fechas muy cercanas")
        void crearNuevo_DebeGenerarFechasMuyCercanas() {
            // Act
            DatosAuditoria resultado = DatosAuditoria.crearNuevo("usuario_test");

            // Assert
            long diferenciaEnSegundos = java.time.Duration.between(
                    resultado.getFechaCreacion(), 
                    resultado.getFechaActualizacion()
            ).getSeconds();
            
            assertTrue(diferenciaEnSegundos <= 1);
        }
    }

    @Nested
    @DisplayName("Tests de Inmutabilidad")
    class TestsInmutabilidad {

        @Test
        @DisplayName("Actualizar debe retornar nueva instancia")
        void actualizar_DebeRetornarNuevaInstancia() {
            // Act
            DatosAuditoria resultado = datosAuditoria.actualizar("nuevo_usuario");

            // Assert
            assertNotSame(datosAuditoria, resultado);
            assertEquals(datosAuditoria.getFechaCreacion(), resultado.getFechaCreacion());
            assertEquals(datosAuditoria.getCreadoPor(), resultado.getCreadoPor());
            assertEquals("nuevo_usuario", resultado.getModificadoPor());
            assertNotEquals(datosAuditoria.getFechaActualizacion(), resultado.getFechaActualizacion());
        }

        @Test
        @DisplayName("Cambiar usuario modificación debe retornar nueva instancia")
        void cambiarUsuarioModificacion_DebeRetornarNuevaInstancia() {
            // Act
            DatosAuditoria resultado = datosAuditoria.cambiarUsuarioModificacion("usuario_nuevo");

            // Assert
            assertNotSame(datosAuditoria, resultado);
            assertEquals("usuario_nuevo", resultado.getModificadoPor());
            assertNotEquals(datosAuditoria.getFechaActualizacion(), resultado.getFechaActualizacion());
        }

        @Test
        @DisplayName("Modificar con toBuilder debe mantener inmutabilidad")
        void modificarConToBuilder_DebeMantenerInmutabilidad() {
            // Act
            DatosAuditoria resultado = datosAuditoria.toBuilder()
                    .modificadoPor("usuario_modificado")
                    .build();

            // Assert
            assertNotSame(datosAuditoria, resultado);
            assertEquals("usuario_modificador", datosAuditoria.getModificadoPor());
            assertEquals("usuario_modificado", resultado.getModificadoPor());
        }
    }

    @Nested
    @DisplayName("Tests de Métodos de Consulta")
    class TestsMetodosConsulta {

        @Test
        @DisplayName("Es reciente debe retornar true para fecha reciente")
        void esReciente_ConFechaReciente_DebeRetornarTrue() {
            // Arrange
            DatosAuditoria datosRecientes = DatosAuditoria.crearNuevo("usuario_test");

            // Act
            boolean resultado = datosRecientes.esReciente();

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Es reciente debe retornar false para fecha antigua")
        void esReciente_ConFechaAntigua_DebeRetornarFalse() {
            // Arrange
            DatosAuditoria datosAntiguos = DatosAuditoria.builder()
                    .fechaCreacion(LocalDateTime.now().minusDays(10))
                    .fechaActualizacion(LocalDateTime.now().minusDays(10))
                    .creadoPor("usuario_test")
                    .modificadoPor("usuario_test")
                    .build();

            // Act
            boolean resultado = datosAntiguos.esReciente();

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Fue modificado recientemente debe retornar true")
        void fueModificadoRecientemente_ConModificacionReciente_DebeRetornarTrue() {
            // Arrange
            DatosAuditoria datosModificados = datosAuditoria.actualizar("usuario_test");

            // Act
            boolean resultado = datosModificados.fueModificadoRecientemente();

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Fue modificado por debe retornar true con usuario correcto")
        void fueModificadoPor_ConUsuarioCorrecto_DebeRetornarTrue() {
            // Act
            boolean resultado = datosAuditoria.fueModificadoPor("usuario_modificador");

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Fue modificado por debe retornar false con usuario incorrecto")
        void fueModificadoPor_ConUsuarioIncorrecto_DebeRetornarFalse() {
            // Act
            boolean resultado = datosAuditoria.fueModificadoPor("usuario_diferente");

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Es mismo creador y modificador debe retornar false")
        void esMismoCreadorYModificador_ConUsuariosDiferentes_DebeRetornarFalse() {
            // Act
            boolean resultado = datosAuditoria.esMismoCreadorYModificador();

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Es mismo creador y modificador debe retornar true")
        void esMismoCreadorYModificador_ConMismoUsuario_DebeRetornarTrue() {
            // Arrange
            DatosAuditoria datosMismoUsuario = DatosAuditoria.crearNuevo("usuario_test");

            // Act
            boolean resultado = datosMismoUsuario.esMismoCreadorYModificador();

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Días desde creación debe calcular correctamente")
        void diasDesdeCreacion_DebeCalcularCorrectamente() {
            // Arrange
            DatosAuditoria datosConFechaEspecifica = DatosAuditoria.builder()
                    .fechaCreacion(LocalDateTime.now().minusDays(5))
                    .fechaActualizacion(LocalDateTime.now())
                    .creadoPor("usuario_test")
                    .modificadoPor("usuario_test")
                    .build();

            // Act
            long dias = datosConFechaEspecifica.diasDesdeCreacion();

            // Assert
            assertEquals(5, dias);
        }

        @Test
        @DisplayName("Horas desde última modificación debe calcular correctamente")
        void horasDesdeUltimaModificacion_DebeCalcularCorrectamente() {
            // Arrange
            DatosAuditoria datosConFechaEspecifica = DatosAuditoria.builder()
                    .fechaCreacion(LocalDateTime.now().minusDays(1))
                    .fechaActualizacion(LocalDateTime.now().minusHours(3))
                    .creadoPor("usuario_test")
                    .modificadoPor("usuario_test")
                    .build();

            // Act
            long horas = datosConFechaEspecifica.horasDesdeUltimaModificacion();

            // Assert
            assertEquals(3, horas);
        }
    }

    @Nested
    @DisplayName("Tests de Casos Edge")
    class TestsCasosEdge {

        @Test
        @DisplayName("Días desde creación con fecha nula debe retornar 0")
        void diasDesdeCreacion_ConFechaNula_DebeRetornarCero() {
            // Arrange
            DatosAuditoria datosConFechaNula = DatosAuditoria.builder()
                    .fechaCreacion(null)
                    .fechaActualizacion(LocalDateTime.now())
                    .creadoPor("usuario_test")
                    .modificadoPor("usuario_test")
                    .build();

            // Act
            long dias = datosConFechaNula.diasDesdeCreacion();

            // Assert
            assertEquals(0, dias);
        }

        @Test
        @DisplayName("Horas desde última modificación con fecha nula debe retornar 0")
        void horasDesdeUltimaModificacion_ConFechaNula_DebeRetornarCero() {
            // Arrange
            DatosAuditoria datosConFechaNula = DatosAuditoria.builder()
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(null)
                    .creadoPor("usuario_test")
                    .modificadoPor("usuario_test")
                    .build();

            // Act
            long horas = datosConFechaNula.horasDesdeUltimaModificacion();

            // Assert
            assertEquals(0, horas);
        }

        @Test
        @DisplayName("Fue modificado por con usuario nulo debe retornar false")
        void fueModificadoPor_ConUsuarioNulo_DebeRetornarFalse() {
            // Act
            boolean resultado = datosAuditoria.fueModificadoPor(null);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Es mismo creador y modificador con nulos debe retornar false")
        void esMismoCreadorYModificador_ConUsuariosNulos_DebeRetornarFalse() {
            // Arrange
            DatosAuditoria datosConNulos = DatosAuditoria.builder()
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(LocalDateTime.now())
                    .creadoPor(null)
                    .modificadoPor(null)
                    .build();

            // Act
            boolean resultado = datosConNulos.esMismoCreadorYModificador();

            // Assert
            assertFalse(resultado);
        }
    }
}
