package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.enums.EstadoInterconsulta;
import com.ug.ec.domain.consultaexterna.enums.PrioridadInterconsulta;
import com.ug.ec.domain.consultaexterna.exceptions.InterconsultaInvalidaException;
import com.ug.ec.domain.consultaexterna.valueobjects.Interconsulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterconsultaValidationService - Tests de Validación")
class InterconsultaValidationServiceTest {

    @Mock
    private DiagnosticoInterconsultaRulesService diagnosticoRulesService;

    @InjectMocks
    private InterconsultaValidationService validationService;

    private Interconsulta interconsultaValida;
    private String codigoDiagnostico;

    @BeforeEach
    void setUp() {
        interconsultaValida = crearInterconsultaValida();
        codigoDiagnostico = "I10"; // Hipertensión esencial (primaria)
    }

    @Nested
    @DisplayName("Tests de Validación de Especialidad")
    class TestsValidacionEspecialidad {

        @Test
        @DisplayName("Validar interconsulta con especialidad adecuada debe ser exitoso")
        void validarInterconsulta_ConEspecialidadAdecuada_DebeSerExitoso() {
            // Arrange
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoDiagnostico, "CARDIOLOGÍA"))
                    .thenReturn(true);
            when(diagnosticoRulesService.calcularPrioridadRecomendada(codigoDiagnostico))
                    .thenReturn(PrioridadInterconsulta.MEDIA);

            // Act & Assert
            assertDoesNotThrow(() -> {
                validationService.validarInterconsulta(interconsultaValida, codigoDiagnostico);
            });
        }

        @Test
        @DisplayName("Validar interconsulta con especialidad inadecuada debe lanzar excepción")
        void validarInterconsulta_ConEspecialidadInadecuada_DebeLanzarExcepcion() {
            // Arrange
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoDiagnostico, "CARDIOLOGÍA"))
                    .thenReturn(false);

            // Act & Assert
            InterconsultaInvalidaException exception = assertThrows(InterconsultaInvalidaException.class, () -> {
                validationService.validarInterconsulta(interconsultaValida, codigoDiagnostico);
            });

            assertTrue(exception.getMessage().contains("no es adecuada para el diagnóstico"));
        }
    }

    @Nested
    @DisplayName("Tests de Validación de Prioridad")
    class TestsValidacionPrioridad {

        @Test
        @DisplayName("Validar interconsulta con prioridad adecuada debe ser exitoso")
        void validarInterconsulta_ConPrioridadAdecuada_DebeSerExitoso() {
            // Arrange
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoDiagnostico, "CARDIOLOGÍA"))
                    .thenReturn(true);
            when(diagnosticoRulesService.calcularPrioridadRecomendada(codigoDiagnostico))
                    .thenReturn(PrioridadInterconsulta.MEDIA);

            // Act & Assert
            assertDoesNotThrow(() -> {
                validationService.validarInterconsulta(interconsultaValida, codigoDiagnostico);
            });
        }

        @Test
        @DisplayName("Validar interconsulta con prioridad menor a la recomendada debe lanzar excepción")
        void validarInterconsulta_ConPrioridadMenorARecomendada_DebeLanzarExcepcion() {
            // Arrange
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoDiagnostico, "CARDIOLOGÍA"))
                    .thenReturn(true);
            when(diagnosticoRulesService.calcularPrioridadRecomendada(codigoDiagnostico))
                    .thenReturn(PrioridadInterconsulta.URGENTE);

            // Act & Assert
            InterconsultaInvalidaException exception = assertThrows(InterconsultaInvalidaException.class, () -> {
                validationService.validarInterconsulta(interconsultaValida, codigoDiagnostico);
            });

            assertTrue(exception.getMessage().contains("requiere una interconsulta URGENTE"));
        }
    }

    @Nested
    @DisplayName("Tests de Validación de Campos Obligatorios")
    class TestsValidacionCamposObligatorios {

        @Test
        @DisplayName("Validar interconsulta para cardiología sin exámenes realizados debe lanzar excepción")
        void validarInterconsulta_ParaCardiologiaSinExamenesRealizados_DebeLanzarExcepcion() {
            // Arrange
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoDiagnostico, "CARDIOLOGÍA"))
                    .thenReturn(true);
            when(diagnosticoRulesService.calcularPrioridadRecomendada(codigoDiagnostico))
                    .thenReturn(PrioridadInterconsulta.MEDIA);

            Interconsulta interconsultaSinExamenes = interconsultaValida.toBuilder()
                    .examenesRealizados(null)
                    .build();

            // Act & Assert
            InterconsultaInvalidaException exception = assertThrows(InterconsultaInvalidaException.class, () -> {
                validationService.validarInterconsulta(interconsultaSinExamenes, codigoDiagnostico);
            });

            assertTrue(exception.getMessage().contains("Faltan campos obligatorios"));
            assertTrue(exception.getMessage().contains("examenesRealizados"));
        }

        @Test
        @DisplayName("Validar interconsulta para neurología sin hallazgos relevantes debe lanzar excepción")
        void validarInterconsulta_ParaNeurologiaSinHallazgosRelevantes_DebeLanzarExcepcion() {
            // Arrange
            String codigoNeurologico = "G40"; // Epilepsia
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoNeurologico, "NEUROLOGÍA"))
                    .thenReturn(true);
            when(diagnosticoRulesService.calcularPrioridadRecomendada(codigoNeurologico))
                    .thenReturn(PrioridadInterconsulta.MEDIA);

            Interconsulta interconsultaNeurologia = interconsultaValida.toBuilder()
                    .especialidad("NEUROLOGÍA")
                    .hallazgosRelevantes(null)
                    .build();

            // Act & Assert
            InterconsultaInvalidaException exception = assertThrows(InterconsultaInvalidaException.class, () -> {
                validationService.validarInterconsulta(interconsultaNeurologia, codigoNeurologico);
            });

            assertTrue(exception.getMessage().contains("Faltan campos obligatorios"));
            assertTrue(exception.getMessage().contains("hallazgosRelevantes"));
        }
    }

    @Nested
    @DisplayName("Tests de Casos Completos")
    class TestsCasosCompletos {

        @Test
        @DisplayName("Validar interconsulta completa y válida debe ser exitoso")
        void validarInterconsulta_CompletaYValida_DebeSerExitoso() {
            // Arrange
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoDiagnostico, "CARDIOLOGÍA"))
                    .thenReturn(true);
            when(diagnosticoRulesService.calcularPrioridadRecomendada(codigoDiagnostico))
                    .thenReturn(PrioridadInterconsulta.MEDIA);

            // Act & Assert
            assertDoesNotThrow(() -> {
                validationService.validarInterconsulta(interconsultaValida, codigoDiagnostico);
            });

            // Verify
            verify(diagnosticoRulesService).esEspecialidadAdecuada(codigoDiagnostico, "CARDIOLOGÍA");
            verify(diagnosticoRulesService).calcularPrioridadRecomendada(codigoDiagnostico);
        }

        @Test
        @DisplayName("Validar interconsulta oncológica completa debe ser exitoso")
        void validarInterconsulta_OncologicaCompleta_DebeSerExitoso() {
            // Arrange
            String codigoOncologico = "C50"; // Cáncer de mama
            when(diagnosticoRulesService.esEspecialidadAdecuada(codigoOncologico, "ONCOLOGÍA"))
                    .thenReturn(true);
            when(diagnosticoRulesService.calcularPrioridadRecomendada(codigoOncologico))
                    .thenReturn(PrioridadInterconsulta.URGENTE);

            Interconsulta interconsultaOncologia = interconsultaValida.toBuilder()
                    .especialidad("ONCOLOGÍA")
                    .prioridad(PrioridadInterconsulta.URGENTE)
                    .examenesRealizados(Arrays.asList("Mamografía", "Biopsia"))
                    .resultadosExamenes("Hallazgos compatibles con neoplasia maligna")
                    .hallazgosRelevantes("Nódulo palpable en cuadrante superior externo de mama derecha")
                    .build();

            // Act & Assert
            assertDoesNotThrow(() -> {
                validationService.validarInterconsulta(interconsultaOncologia, codigoOncologico);
            });

            // Verify
            verify(diagnosticoRulesService).esEspecialidadAdecuada(codigoOncologico, "ONCOLOGÍA");
            verify(diagnosticoRulesService).calcularPrioridadRecomendada(codigoOncologico);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Interconsulta crearInterconsultaValida() {
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
}