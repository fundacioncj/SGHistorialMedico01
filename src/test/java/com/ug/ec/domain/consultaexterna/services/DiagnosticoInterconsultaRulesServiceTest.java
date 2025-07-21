package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.enums.PrioridadInterconsulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiagnosticoInterconsultaRulesService - Tests de Reglas de Negocio")
class DiagnosticoInterconsultaRulesServiceTest {

    private DiagnosticoInterconsultaRulesService rulesService;

    @BeforeEach
    void setUp() {
        rulesService = new DiagnosticoInterconsultaRulesService();
    }

    @Nested
    @DisplayName("Tests de Especialidades Recomendadas")
    class TestsEspecialidadesRecomendadas {

        @Test
        @DisplayName("Diagnóstico cardiovascular debe recomendar cardiología")
        void esEspecialidadAdecuada_DiagnosticoCardiovascular_DebeRecomendarCardiologia() {
            // Act & Assert
            assertTrue(rulesService.esEspecialidadAdecuada("I10", "CARDIOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("I20", "CARDIOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("I21", "CARDIOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("I50", "CARDIOLOGÍA"));
        }

        @Test
        @DisplayName("Diagnóstico neurológico debe recomendar neurología")
        void esEspecialidadAdecuada_DiagnosticoNeurologico_DebeRecomendarNeurologia() {
            // Act & Assert
            assertTrue(rulesService.esEspecialidadAdecuada("G40", "NEUROLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("G43", "NEUROLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("G45", "NEUROLOGÍA"));
        }

        @Test
        @DisplayName("Diagnóstico psiquiátrico debe recomendar psiquiatría")
        void esEspecialidadAdecuada_DiagnosticoPsiquiatrico_DebeRecomendarPsiquiatria() {
            // Act & Assert
            assertTrue(rulesService.esEspecialidadAdecuada("F20", "PSIQUIATRÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("F32", "PSIQUIATRÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("F41", "PSIQUIATRÍA"));
        }

        @Test
        @DisplayName("Diagnóstico oncológico debe recomendar oncología")
        void esEspecialidadAdecuada_DiagnosticoOncologico_DebeRecomendarOncologia() {
            // Act & Assert
            assertTrue(rulesService.esEspecialidadAdecuada("C50", "ONCOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("C61", "ONCOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("C18", "ONCOLOGÍA"));
        }

        @Test
        @DisplayName("Diagnóstico endocrinológico debe recomendar endocrinología")
        void esEspecialidadAdecuada_DiagnosticoEndocrinologico_DebeRecomendarEndocrinologia() {
            // Act & Assert
            assertTrue(rulesService.esEspecialidadAdecuada("E10", "ENDOCRINOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("E11", "ENDOCRINOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("E05", "ENDOCRINOLOGÍA"));
        }

        @Test
        @DisplayName("Especialidad incorrecta para diagnóstico debe retornar falso")
        void esEspecialidadAdecuada_EspecialidadIncorrecta_DebeRetornarFalso() {
            // Act & Assert
            assertFalse(rulesService.esEspecialidadAdecuada("I10", "NEUROLOGÍA"));
            assertFalse(rulesService.esEspecialidadAdecuada("G40", "CARDIOLOGÍA"));
            assertFalse(rulesService.esEspecialidadAdecuada("F20", "ONCOLOGÍA"));
            assertFalse(rulesService.esEspecialidadAdecuada("C50", "PSIQUIATRÍA"));
            assertFalse(rulesService.esEspecialidadAdecuada("E10", "CARDIOLOGÍA"));
        }

        @Test
        @DisplayName("Diagnóstico sin reglas específicas debe aceptar cualquier especialidad")
        void esEspecialidadAdecuada_DiagnosticoSinReglas_DebeAceptarCualquierEspecialidad() {
            // Act & Assert
            assertTrue(rulesService.esEspecialidadAdecuada("Z00", "MEDICINA GENERAL"));
            assertTrue(rulesService.esEspecialidadAdecuada("Z00", "CARDIOLOGÍA"));
            assertTrue(rulesService.esEspecialidadAdecuada("Z00", "NEUROLOGÍA"));
        }

        @ParameterizedTest
        @CsvSource({
            "I10, MEDICINA INTERNA, true",
            "I21, CUIDADOS INTENSIVOS, true",
            "G45, MEDICINA INTERNA, true",
            "F32, PSICOLOGÍA, true",
            "C61, UROLOGÍA, true",
            "C18, GASTROENTEROLOGÍA, true"
        })
        @DisplayName("Diagnósticos con múltiples especialidades válidas")
        void esEspecialidadAdecuada_DiagnosticosConMultiplesEspecialidades(
                String codigoCie10, String especialidad, boolean esperado) {
            // Act & Assert
            assertEquals(esperado, rulesService.esEspecialidadAdecuada(codigoCie10, especialidad));
        }
    }

    @Nested
    @DisplayName("Tests de Prioridades Recomendadas")
    class TestsPrioridadesRecomendadas {

        @Test
        @DisplayName("Diagnósticos que requieren atención urgente")
        void calcularPrioridadRecomendada_DiagnosticosUrgentes_DebeRetornarUrgente() {
            // Act & Assert
            assertEquals(PrioridadInterconsulta.URGENTE, rulesService.calcularPrioridadRecomendada("I21"));
            assertEquals(PrioridadInterconsulta.URGENTE, rulesService.calcularPrioridadRecomendada("I60"));
            assertEquals(PrioridadInterconsulta.URGENTE, rulesService.calcularPrioridadRecomendada("I61"));
            assertEquals(PrioridadInterconsulta.URGENTE, rulesService.calcularPrioridadRecomendada("G45"));
            assertEquals(PrioridadInterconsulta.URGENTE, rulesService.calcularPrioridadRecomendada("K92"));
            assertEquals(PrioridadInterconsulta.URGENTE, rulesService.calcularPrioridadRecomendada("C50"));
        }

        @Test
        @DisplayName("Diagnósticos que requieren atención alta")
        void calcularPrioridadRecomendada_DiagnosticosAlta_DebeRetornarAlta() {
            // Act & Assert
            assertEquals(PrioridadInterconsulta.ALTA, rulesService.calcularPrioridadRecomendada("I20"));
            assertEquals(PrioridadInterconsulta.ALTA, rulesService.calcularPrioridadRecomendada("I50"));
            assertEquals(PrioridadInterconsulta.ALTA, rulesService.calcularPrioridadRecomendada("E10"));
            assertEquals(PrioridadInterconsulta.ALTA, rulesService.calcularPrioridadRecomendada("G40"));
            assertEquals(PrioridadInterconsulta.ALTA, rulesService.calcularPrioridadRecomendada("F20"));
        }

        @Test
        @DisplayName("Diagnósticos que requieren atención media")
        void calcularPrioridadRecomendada_DiagnosticosMedios_DebeRetornarMedia() {
            // Act & Assert
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada("I10"));
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada("E78"));
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada("F32"));
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada("F41"));
        }

        @Test
        @DisplayName("Diagnósticos que requieren atención baja")
        void calcularPrioridadRecomendada_DiagnosticosBajos_DebeRetornarBaja() {
            // Act & Assert
            assertEquals(PrioridadInterconsulta.BAJA, rulesService.calcularPrioridadRecomendada("M54"));
            assertEquals(PrioridadInterconsulta.BAJA, rulesService.calcularPrioridadRecomendada("J30"));
            assertEquals(PrioridadInterconsulta.BAJA, rulesService.calcularPrioridadRecomendada("L20"));
        }

        @Test
        @DisplayName("Diagnóstico sin prioridad específica debe retornar prioridad media por defecto")
        void calcularPrioridadRecomendada_DiagnosticoSinPrioridad_DebeRetornarMedia() {
            // Act & Assert
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada("Z00"));
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada("A00"));
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada("B01"));
        }
    }

    @Nested
    @DisplayName("Tests de Normalización de Códigos")
    class TestsNormalizacionCodigos {

        @ParameterizedTest
        @CsvSource({
            "I10, I10",
            "I10.1, I10",
            "I10.9, I10",
            "G40.1, G40",
            "C50.9, C50"
        })
        @DisplayName("Normalización de códigos CIE-10 debe extraer parte principal")
        void normalizarCodigoCie10_DebeExtraerPartePrincipal(String codigoCompleto, String codigoBase) {
            // Este test verifica indirectamente el método privado normalizarCodigoCie10
            // a través de los métodos públicos que lo utilizan
            
            // Arrange
            String especialidadEsperada = obtenerEspecialidadParaCodigo(codigoBase);
            
            // Act & Assert
            assertTrue(rulesService.esEspecialidadAdecuada(codigoCompleto, especialidadEsperada),
                    "El código " + codigoCompleto + " debería normalizarse a " + codigoBase);
        }
        
        private String obtenerEspecialidadParaCodigo(String codigo) {
            if (codigo.startsWith("I")) return "CARDIOLOGÍA";
            if (codigo.startsWith("G")) return "NEUROLOGÍA";
            if (codigo.startsWith("F")) return "PSIQUIATRÍA";
            if (codigo.startsWith("C")) return "ONCOLOGÍA";
            if (codigo.startsWith("E")) return "ENDOCRINOLOGÍA";
            return "MEDICINA GENERAL";
        }
    }

    @Nested
    @DisplayName("Tests de Casos Borde")
    class TestsCasosBorde {

        @Test
        @DisplayName("Código CIE-10 nulo debe manejar correctamente")
        void esEspecialidadAdecuada_CodigoNulo_DebeRetornarFalso() {
            // Act & Assert
            assertFalse(rulesService.esEspecialidadAdecuada(null, "CARDIOLOGÍA"));
        }

        @Test
        @DisplayName("Especialidad nula debe manejar correctamente")
        void esEspecialidadAdecuada_EspecialidadNula_DebeRetornarFalso() {
            // Act & Assert
            assertFalse(rulesService.esEspecialidadAdecuada("I10", null));
        }

        @Test
        @DisplayName("Código CIE-10 nulo en cálculo de prioridad debe retornar prioridad media")
        void calcularPrioridadRecomendada_CodigoNulo_DebeRetornarMedia() {
            // Act & Assert
            assertEquals(PrioridadInterconsulta.MEDIA, rulesService.calcularPrioridadRecomendada(null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Código CIE-10 vacío debe manejar correctamente")
        void esEspecialidadAdecuada_CodigoVacio_DebeRetornarFalso(String codigo) {
            // Act & Assert
            assertFalse(rulesService.esEspecialidadAdecuada(codigo, "CARDIOLOGÍA"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Especialidad vacía debe manejar correctamente")
        void esEspecialidadAdecuada_EspecialidadVacia_DebeRetornarFalso(String especialidad) {
            // Act & Assert
            assertFalse(rulesService.esEspecialidadAdecuada("I10", especialidad));
        }
    }
}