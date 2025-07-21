package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.enums.TipoDiagnostico;
import com.ug.ec.domain.consultaexterna.exceptions.DiagnosticoInvalidoException;
import com.ug.ec.domain.consultaexterna.valueobjects.Diagnostico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiagnosticoEspecificoService - Tests de Validación")
class DiagnosticoEspecificoServiceTest {

    @Mock
    private CIE10ValidationService cie10Service;

    @InjectMocks
    private DiagnosticoEspecificoService diagnosticoService;

    private Diagnostico diagnosticoValido;

    @BeforeEach
    void setUp() {
        diagnosticoValido = crearDiagnosticoValido();
    }

    @Nested
    @DisplayName("Tests de Validación de Código CIE-10")
    class TestsValidacionCodigoCIE10 {

        @Test
        @DisplayName("Validar diagnóstico con código CIE-10 válido debe ser exitoso")
        void validarDiagnosticoEspecifico_CodigoCIE10Valido_DebeSerExitoso() {
            // Arrange
            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            assertDoesNotThrow(() -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoValido);
            });

            verify(cie10Service).esCodigoValido(diagnosticoValido.getCodigoCie10());
        }

        @Test
        @DisplayName("Validar diagnóstico con código CIE-10 inválido debe fallar")
        void validarDiagnosticoEspecifico_CodigoCIE10Invalido_DebeFallar() {
            // Arrange
            when(cie10Service.esCodigoValido(anyString())).thenReturn(false);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoValido);
            });

            assertTrue(exception.getMessage().contains("no es válido"));
            verify(cie10Service).esCodigoValido(diagnosticoValido.getCodigoCie10());
        }
    }

    @Nested
    @DisplayName("Tests de Validación de Campos Obligatorios")
    class TestsValidacionCamposObligatorios {

        @Test
        @DisplayName("Validar diagnóstico principal sin severidad debe fallar")
        void validarDiagnosticoEspecifico_PrincipalSinSeveridad_DebeFallar() {
            // Arrange
            Diagnostico diagnosticoPrincipalSinSeveridad = diagnosticoValido.toBuilder()
                    .tipo(TipoDiagnostico.PRINCIPAL)
                    .severidad(null)
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoPrincipalSinSeveridad);
            });

            assertTrue(exception.getMessage().contains("Faltan campos obligatorios"));
            assertTrue(exception.getMessage().contains("severidad"));
        }

        @Test
        @DisplayName("Validar diagnóstico crónico sin seguimiento debe fallar")
        void validarDiagnosticoEspecifico_CronicoSinSeguimiento_DebeFallar() {
            // Arrange
            Diagnostico diagnosticoCronicoSinSeguimiento = diagnosticoValido.toBuilder()
                    .esCronico(true)
                    .requiereSeguimiento(null)
                    .tiempoSeguimientoMeses(null)
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoCronicoSinSeguimiento);
            });

            assertTrue(exception.getMessage().contains("Faltan campos obligatorios"));
            assertTrue(exception.getMessage().contains("requiereSeguimiento"));
            assertTrue(exception.getMessage().contains("tiempoSeguimientoMeses"));
        }

        @Test
        @DisplayName("Validar diagnóstico con interconsulta sin especialidad debe fallar")
        void validarDiagnosticoEspecifico_ConInterconsultaSinEspecialidad_DebeFallar() {
            // Arrange
            Diagnostico diagnosticoConInterconsultaSinEspecialidad = diagnosticoValido.toBuilder()
                    .requiereInterconsulta(true)
                    .especialidadRecomendada(null)
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoConInterconsultaSinEspecialidad);
            });

            assertTrue(exception.getMessage().contains("Faltan campos obligatorios"));
            assertTrue(exception.getMessage().contains("especialidadRecomendada"));
        }
    }

    @Nested
    @DisplayName("Tests de Coherencia entre Campos")
    class TestsCoherenciaCampos {

        @Test
        @DisplayName("Validar diagnóstico con severidad SEVERO sin manifestaciones clínicas debe fallar")
        void validarDiagnosticoEspecifico_SeveroSinManifestaciones_DebeFallar() {
            // Arrange
            Diagnostico diagnosticoSeveroSinManifestaciones = diagnosticoValido.toBuilder()
                    .severidad("SEVERO")
                    .manifestacionesClinicas(null)
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoSeveroSinManifestaciones);
            });

            assertTrue(exception.getMessage().contains("debe incluir manifestaciones clínicas"));
        }

        @Test
        @DisplayName("Validar diagnóstico crónico sin requerir seguimiento debe fallar")
        void validarDiagnosticoEspecifico_CronicoSinRequerirSeguimiento_DebeFallar() {
            // Arrange
            Diagnostico diagnosticoCronicoSinRequerirSeguimiento = diagnosticoValido.toBuilder()
                    .esCronico(true)
                    .requiereSeguimiento(false)
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoCronicoSinRequerirSeguimiento);
            });

            assertTrue(exception.getMessage().contains("debe requerir seguimiento"));
        }
    }

    @Nested
    @DisplayName("Tests de Reglas Específicas por Código CIE-10")
    class TestsReglasEspecificasPorCodigo {

        @ParameterizedTest
        @ValueSource(strings = {"I20", "I21", "I25", "I50"})
        @DisplayName("Diagnósticos cardiovasculares deben requerir interconsulta con cardiología")
        void validarDiagnosticoEspecifico_Cardiovascular_DebeRequerirInterconsultaCardiologia(String codigoCie10) {
            // Arrange
            Diagnostico diagnosticoCardiovascular = diagnosticoValido.toBuilder()
                    .codigoCie10(codigoCie10)
                    .requiereInterconsulta(false)  // No tiene interconsulta, pero debería
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoCardiovascular);
            });

            assertTrue(exception.getMessage().contains("requiere interconsulta obligatoria"));
        }

        @ParameterizedTest
        @CsvSource({
            "I10, 3",
            "I20, 1",
            "I21, 1",
            "I50, 1",
            "E10, 3",
            "E11, 3",
            "G20, 3",
            "G35, 3",
            "G40, 3",
            "F20, 1",
            "F31, 1",
            "F32, 1"
        })
        @DisplayName("Diagnósticos crónicos deben tener tiempo de seguimiento mínimo adecuado")
        void validarDiagnosticoEspecifico_Cronico_DebeTenerTiempoSeguimientoMinimo(String codigoCie10, int mesesMinimos) {
            // Arrange
            Diagnostico diagnosticoCronico = diagnosticoValido.toBuilder()
                    .codigoCie10(codigoCie10)
                    .esCronico(true)
                    .requiereSeguimiento(true)
                    .tiempoSeguimientoMeses(mesesMinimos - 1)  // Menos del mínimo requerido
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            DiagnosticoInvalidoException exception = assertThrows(DiagnosticoInvalidoException.class, () -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoCronico);
            });

            assertTrue(exception.getMessage().contains("requiere un seguimiento mínimo"));
            assertTrue(exception.getMessage().contains(String.valueOf(mesesMinimos)));
        }
    }

    @Nested
    @DisplayName("Tests de Casos Completos")
    class TestsCasosCompletos {

        @Test
        @DisplayName("Validar diagnóstico completo y válido debe ser exitoso")
        void validarDiagnosticoEspecifico_CompletoYValido_DebeSerExitoso() {
            // Arrange
            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            assertDoesNotThrow(() -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoValido);
            });

            verify(cie10Service).esCodigoValido(diagnosticoValido.getCodigoCie10());
        }

        @Test
        @DisplayName("Validar diagnóstico de diabetes completo debe ser exitoso")
        void validarDiagnosticoEspecifico_DiabetesCompleto_DebeSerExitoso() {
            // Arrange
            Diagnostico diagnosticoDiabetes = Diagnostico.builder()
                    .codigoCie10("E11")
                    .descripcion("Diabetes mellitus tipo 2")
                    .tipo(TipoDiagnostico.PRINCIPAL)
                    .fechaDiagnostico(LocalDateTime.now())
                    .severidad("MODERADO")
                    .esCronico(true)
                    .requiereSeguimiento(true)
                    .tiempoSeguimientoMeses(6)
                    .planSeguimiento("Control cada 3 meses")
                    .manifestacionesClinicas(Arrays.asList("Polidipsia", "Poliuria", "Pérdida de peso"))
                    .factoresRiesgo(Arrays.asList("Obesidad", "Antecedentes familiares"))
                    .requiereInterconsulta(true)
                    .especialidadRecomendada("ENDOCRINOLOGÍA")
                    .build();

            when(cie10Service.esCodigoValido(anyString())).thenReturn(true);

            // Act & Assert
            assertDoesNotThrow(() -> {
                diagnosticoService.validarDiagnosticoEspecifico(diagnosticoDiabetes);
            });

            verify(cie10Service).esCodigoValido(diagnosticoDiabetes.getCodigoCie10());
        }
    }

    @Nested
    @DisplayName("Tests de Métodos Auxiliares")
    class TestsMetodosAuxiliares {

        @Test
        @DisplayName("Método requiereInterconsultaObligatoria debe funcionar correctamente")
        void requiereInterconsultaObligatoria_DebeFuncionarCorrectamente() {
            // Act & Assert
            assertTrue(diagnosticoService.requiereInterconsultaObligatoria("I20"));
            assertTrue(diagnosticoService.requiereInterconsultaObligatoria("I21"));
            assertTrue(diagnosticoService.requiereInterconsultaObligatoria("F20"));
            assertFalse(diagnosticoService.requiereInterconsultaObligatoria("Z00.0"));
            assertFalse(diagnosticoService.requiereInterconsultaObligatoria(null));
        }

        @Test
        @DisplayName("Método determinarEspecialidadRecomendada debe funcionar correctamente")
        void determinarEspecialidadRecomendada_DebeFuncionarCorrectamente() {
            // Act & Assert
            assertEquals("CARDIOLOGÍA", diagnosticoService.determinarEspecialidadRecomendada("I20"));
            assertEquals("CARDIOLOGÍA", diagnosticoService.determinarEspecialidadRecomendada("I21"));
            assertEquals("PSIQUIATRÍA", diagnosticoService.determinarEspecialidadRecomendada("F20"));
            assertNull(diagnosticoService.determinarEspecialidadRecomendada("Z00.0"));
            assertNull(diagnosticoService.determinarEspecialidadRecomendada(null));
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Diagnostico crearDiagnosticoValido() {
        return Diagnostico.builder()
                .codigoCie10("J45.0")
                .descripcion("Asma predominantemente alérgica")
                .tipo(TipoDiagnostico.PRINCIPAL)
                .fechaDiagnostico(LocalDateTime.now())
                .severidad("MODERADO")
                .estadioEnfermedad("CRÓNICO")
                .requiereSeguimiento(true)
                .tiempoSeguimientoMeses(6)
                .planSeguimiento("Control cada 3 meses")
                .manifestacionesClinicas(Arrays.asList("Disnea", "Sibilancias", "Tos"))
                .factoresRiesgo(Arrays.asList("Alergia", "Antecedentes familiares"))
                .pronostico("Favorable con tratamiento adecuado")
                .esCronico(true)
                .build();
    }
}