//package com.ug.ec.domain.consultaexterna.services;
//
//import com.ug.ec.domain.consultaexterna.valueobjects.Prescripcion;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("InteraccionesMedicamentosService - Tests de Detección de Interacciones")
//class InteraccionesMedicamentosServiceTest {
//
//    private InteraccionesMedicamentosService interaccionesService;
//
//    @BeforeEach
//    void setUp() {
//        interaccionesService = new InteraccionesMedicamentosService();
//    }
//
//    @Nested
//    @DisplayName("Tests de Interacciones con Warfarina")
//    class TestsInteraccionesWarfarina {
//
//        @Test
//        @DisplayName("Warfarina con AINE debe detectar interacción")
//        void verificarInteracciones_WarfarinaConAINE_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion warfarina = crearPrescripcion("Warfarina", "5 mg", "cada 24 horas");
//            Prescripcion ibuprofeno = crearPrescripcion("Ibuprofeno", "400 mg", "cada 8 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(warfarina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(ibuprofeno, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("warfarina") && i.contains("ibuprofeno")));
//        }
//
//        @Test
//        @DisplayName("Warfarina con Paracetamol no debe detectar interacción")
//        void verificarInteracciones_WarfarinaConParacetamol_NoDebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion warfarina = crearPrescripcion("Warfarina", "5 mg", "cada 24 horas");
//            Prescripcion paracetamol = crearPrescripcion("Paracetamol", "500 mg", "cada 8 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(warfarina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(paracetamol, prescripcionesExistentes);
//
//            // Assert
//            assertTrue(interacciones.isEmpty());
//        }
//
//        @Test
//        @DisplayName("Warfarina con Fluconazol debe detectar interacción")
//        void verificarInteracciones_WarfarinaConFluconazol_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion warfarina = crearPrescripcion("Warfarina", "5 mg", "cada 24 horas");
//            Prescripcion fluconazol = crearPrescripcion("Fluconazol", "150 mg", "cada 24 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(warfarina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(fluconazol, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("warfarina") && i.contains("fluconazol")));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Interacciones con Inhibidores de la MAO")
//    class TestsInteraccionesInhibidoresMAO {
//
//        @Test
//        @DisplayName("Selegilina con ISRS debe detectar interacción")
//        void verificarInteracciones_SelegilinaConISRS_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion selegilina = crearPrescripcion("Selegilina", "5 mg", "cada 12 horas");
//            Prescripcion fluoxetina = crearPrescripcion("Fluoxetina", "20 mg", "cada 24 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(selegilina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(fluoxetina, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("selegilina") && i.contains("fluoxetina")));
//        }
//
//        @Test
//        @DisplayName("Tranilcipromina con Tramadol debe detectar interacción")
//        void verificarInteracciones_TranilciprominaConTramadol_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion tranilcipromina = crearPrescripcion("Tranilcipromina", "10 mg", "cada 12 horas");
//            Prescripcion tramadol = crearPrescripcion("Tramadol", "50 mg", "cada 8 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(tranilcipromina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(tramadol, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("tramadol")));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Interacciones con Macrólidos")
//    class TestsInteraccionesMacrolidos {
//
//        @Test
//        @DisplayName("Eritromicina con Simvastatina debe detectar interacción")
//        void verificarInteracciones_EritromicinConSimvastatina_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion eritromicina = crearPrescripcion("Eritromicina", "500 mg", "cada 8 horas");
//            Prescripcion simvastatina = crearPrescripcion("Simvastatina", "20 mg", "cada 24 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(eritromicina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(simvastatina, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("simvastatina") && i.contains("eritromicina")));
//        }
//
//        @Test
//        @DisplayName("Claritromicina con Digoxina debe detectar interacción")
//        void verificarInteracciones_ClaritromicinConDigoxina_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion claritromicina = crearPrescripcion("Claritromicina", "500 mg", "cada 12 horas");
//            Prescripcion digoxina = crearPrescripcion("Digoxina", "0.25 mg", "cada 24 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(claritromicina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(digoxina, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("digoxina")));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Interacciones con Benzodiacepinas")
//    class TestsInteraccionesBenzodiacepinas {
//
//        @Test
//        @DisplayName("Diazepam con Alcohol debe detectar interacción")
//        void verificarInteracciones_DiazepamConAlcohol_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion diazepam = crearPrescripcion("Diazepam", "5 mg", "cada 12 horas");
//            Prescripcion alcohol = crearPrescripcion("Alcohol", "dosis social", "ocasional");
//            List<Prescripcion> prescripcionesExistentes = List.of(diazepam);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(alcohol, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("diazepam") && i.contains("alcohol")));
//        }
//
//        @Test
//        @DisplayName("Alprazolam con Omeprazol debe detectar interacción")
//        void verificarInteracciones_AlprazolamConOmeprazol_DebeDetectarInteraccion() {
//            // Arrange
//            Prescripcion alprazolam = crearPrescripcion("Alprazolam", "0.5 mg", "cada 12 horas");
//            Prescripcion omeprazol = crearPrescripcion("Omeprazol", "20 mg", "cada 24 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(alprazolam);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(omeprazol, prescripcionesExistentes);
//
//            // Assert
//            assertFalse(interacciones.isEmpty());
//            assertTrue(interacciones.stream().anyMatch(i -> i.contains("alprazolam") && i.contains("omeprazol")));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Normalización de Nombres de Medicamentos")
//    class TestsNormalizacionNombresMedicamentos {
//
//        @ParameterizedTest
//        @CsvSource({
//            "Warfarina 5 mg, Aspirina, true",
//            "WARFARINA, Aspirina, true",
//            "warfarina (5 mg), Aspirina, true",
//            "Warfarina Sódica, Aspirina, true"
//        })
//        @DisplayName("Normalización de nombres de medicamentos debe funcionar correctamente")
//        void verificarInteracciones_ConDiferentesFormatosDeNombre_DebeDetectarInteraccion(
//                String nombreWarfarina, String nombreAspirina, boolean debeDetectar) {
//            // Arrange
//            Prescripcion warfarina = crearPrescripcion(nombreWarfarina, "5 mg", "cada 24 horas");
//            Prescripcion aspirina = crearPrescripcion(nombreAspirina, "100 mg", "cada 24 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(warfarina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(aspirina, prescripcionesExistentes);
//
//            // Assert
//            assertEquals(debeDetectar, !interacciones.isEmpty());
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Casos Borde")
//    class TestsCasosBorde {
//
//        @Test
//        @DisplayName("Lista de prescripciones existentes vacía debe manejar correctamente")
//        void verificarInteracciones_ListaVacia_DebeRetornarListaVacia() {
//            // Arrange
//            Prescripcion prescripcion = crearPrescripcion("Paracetamol", "500 mg", "cada 8 horas");
//            List<Prescripcion> prescripcionesExistentes = new ArrayList<>();
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(prescripcion, prescripcionesExistentes);
//
//            // Assert
//            assertTrue(interacciones.isEmpty());
//        }
//
//        @Test
//        @DisplayName("Prescripción nula debe manejar correctamente")
//        void verificarInteracciones_PrescripcionNula_DebeRetornarListaVacia() {
//            // Arrange
//            Prescripcion warfarina = crearPrescripcion("Warfarina", "5 mg", "cada 24 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(warfarina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(null, prescripcionesExistentes);
//
//            // Assert
//            assertTrue(interacciones.isEmpty());
//        }
//
//        @Test
//        @DisplayName("Lista de prescripciones existentes nula debe manejar correctamente")
//        void verificarInteracciones_ListaNula_DebeRetornarListaVacia() {
//            // Arrange
//            Prescripcion prescripcion = crearPrescripcion("Paracetamol", "500 mg", "cada 8 horas");
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(prescripcion, null);
//
//            // Assert
//            assertTrue(interacciones.isEmpty());
//        }
//
//        @Test
//        @DisplayName("Medicamento sin nombre debe manejar correctamente")
//        void verificarInteracciones_MedicamentoSinNombre_DebeRetornarListaVacia() {
//            // Arrange
//            Prescripcion warfarina = crearPrescripcion("Warfarina", "5 mg", "cada 24 horas");
//            Prescripcion sinNombre = crearPrescripcion(null, "500 mg", "cada 8 horas");
//            List<Prescripcion> prescripcionesExistentes = List.of(warfarina);
//
//            // Act
//            List<String> interacciones = interaccionesService.verificarInteracciones(sinNombre, prescripcionesExistentes);
//
//            // Assert
//            assertTrue(interacciones.isEmpty());
//        }
//    }
//
//    // ========== MÉTODOS AUXILIARES ==========
//
//    private Prescripcion crearPrescripcion(String medicamento, String dosis, String frecuencia) {
//        return Prescripcion.builder()
//                .medicamento(medicamento)
//                .dosis(dosis)
//                .frecuencia(frecuencia)
//                .viaAdministracion("Oral")
//                .duracionDias(5)
//                .fechaPrescripcion(LocalDate.now())
//                .build();
//    }
//}