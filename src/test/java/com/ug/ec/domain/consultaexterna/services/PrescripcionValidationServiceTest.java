//package com.ug.ec.domain.consultaexterna.services;
//
//import com.ug.ec.domain.consultaexterna.exceptions.PrescripcionInvalidaException;
//import com.ug.ec.domain.consultaexterna.valueobjects.DatosPaciente;
//import com.ug.ec.domain.consultaexterna.valueobjects.Prescripcion;
//import com.ug.ec.domain.consultaexterna.enums.Sexo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("PrescripcionValidationService - Tests de Validación")
//class PrescripcionValidationServiceTest {
//
//    @Mock
//    private InteraccionesMedicamentosService interaccionesService;
//
//    @InjectMocks
//    private PrescripcionValidationService validationService;
//
//    private Prescripcion prescripcionValida;
//    private DatosPaciente datosPacienteAdulto;
//    private DatosPaciente datosPacienteNino;
//    private List<Prescripcion> prescripcionesExistentes;
//
//    @BeforeEach
//    void setUp() {
//        prescripcionValida = crearPrescripcionValida();
//        datosPacienteAdulto = crearDatosPacienteAdulto();
//        datosPacienteNino = crearDatosPacienteNino();
//        prescripcionesExistentes = new ArrayList<>();
//    }
//
//    @Nested
//    @DisplayName("Tests de Validación de Dosis por Peso y Edad")
//    class TestsValidacionDosisPorPesoYEdad {
//
//        @Test
//        @DisplayName("Validar prescripción de paracetamol con dosis adecuada para adulto debe ser exitoso")
//        void validarPrescripcion_ParacetamolDosisAdecuadaAdulto_DebeSerExitoso() {
//            // Arrange
//            Prescripcion paracetamol = Prescripcion.builder()
//                    .medicamento("Paracetamol")
//                    .dosis("500 mg")
//                    .frecuencia("cada 8 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(5)
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            assertDoesNotThrow(() -> {
//                validationService.validarPrescripcion(paracetamol, prescripcionesExistentes, datosPacienteAdulto);
//            });
//        }
//
//        @Test
//        @DisplayName("Validar prescripción de paracetamol con dosis excesiva para adulto debe fallar")
//        void validarPrescripcion_ParacetamolDosisExcesivaAdulto_DebeFallar() {
//            // Arrange
//            Prescripcion paracetamolExcesivo = Prescripcion.builder()
//                    .medicamento("Paracetamol")
//                    .dosis("2000 mg")
//                    .frecuencia("cada 4 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(5)
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(paracetamolExcesivo, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            assertTrue(exception.getMessage().contains("excede el máximo recomendado"));
//        }
//
//        @Test
//        @DisplayName("Validar prescripción de ibuprofeno para niño menor de 6 años debe fallar")
//        void validarPrescripcion_IbuprofenoParaNinoMenorDe6_DebeFallar() {
//            // Arrange
//            Prescripcion ibuprofeno = Prescripcion.builder()
//                    .medicamento("Ibuprofeno")
//                    .dosis("100 mg")
//                    .frecuencia("cada 8 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(3)
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            DatosPaciente ninoMenorDe6 = DatosPaciente.builder()
//                    .cedula("0987654321")
//                    .numeroHistoriaClinica("HC-12345")
//                    .primerNombre("Juan")
//                    .apellidoPaterno("Pérez")
//                    .fechaNacimiento(LocalDate.now().minusYears(5))
//                    .sexo(Sexo.MASCULINO)
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(ibuprofeno, prescripcionesExistentes, ninoMenorDe6);
//            });
//
//            assertTrue(exception.getMessage().contains("no está recomendado para niños menores de 6 años"));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Validación de Interacciones Medicamentosas")
//    class TestsValidacionInteraccionesMedicamentosas {
//
//        @Test
//        @DisplayName("Validar prescripción sin interacciones debe ser exitoso")
//        void validarPrescripcion_SinInteracciones_DebeSerExitoso() {
//            // Arrange
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            assertDoesNotThrow(() -> {
//                validationService.validarPrescripcion(prescripcionValida, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            verify(interaccionesService).verificarInteracciones(eq(prescripcionValida), eq(prescripcionesExistentes));
//        }
//
//        @Test
//        @DisplayName("Validar prescripción con interacciones debe fallar")
//        void validarPrescripcion_ConInteracciones_DebeFallar() {
//            // Arrange
//            List<String> interacciones = Arrays.asList(
//                    "La warfarina aumenta el riesgo de sangrado cuando se usa con aspirina"
//            );
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(interacciones);
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(prescripcionValida, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            assertTrue(exception.getMessage().contains("interacciones medicamentosas"));
//            verify(interaccionesService).verificarInteracciones(eq(prescripcionValida), eq(prescripcionesExistentes));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Validación de Contraindicaciones")
//    class TestsValidacionContraindicaciones {
//
//        @Test
//        @DisplayName("Validar prescripción de aspirina para niño debe fallar")
//        void validarPrescripcion_AspirinaParaNino_DebeFallar() {
//            // Arrange
//            Prescripcion aspirina = Prescripcion.builder()
//                    .medicamento("Aspirina")
//                    .dosis("100 mg")
//                    .frecuencia("cada 12 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(5)
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(aspirina, prescripcionesExistentes, datosPacienteNino);
//            });
//
//            assertTrue(exception.getMessage().contains("contraindicado"));
//            assertTrue(exception.getMessage().contains("síndrome de Reye"));
//        }
//
//        @Test
//        @DisplayName("Validar prescripción de AINE para mujer en edad fértil debe mostrar advertencia")
//        void validarPrescripcion_AINEParaMujerEdadFertil_DebeMostrarAdvertencia() {
//            // Arrange
//            Prescripcion ibuprofeno = Prescripcion.builder()
//                    .medicamento("Ibuprofeno")
//                    .dosis("400 mg")
//                    .frecuencia("cada 8 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(5)
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            DatosPaciente mujerEdadFertil = DatosPaciente.builder()
//                    .cedula("0987654321")
//                    .numeroHistoriaClinica("HC-12345")
//                    .primerNombre("María")
//                    .apellidoPaterno("López")
//                    .fechaNacimiento(LocalDate.now().minusYears(25))
//                    .sexo(Sexo.FEMENINO)
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(ibuprofeno, prescripcionesExistentes, mujerEdadFertil);
//            });
//
//            assertTrue(exception.getMessage().contains("pueden estar contraindicados en mujeres embarazadas"));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Validación de Duración del Tratamiento")
//    class TestsValidacionDuracionTratamiento {
//
//        @Test
//        @DisplayName("Validar prescripción con duración negativa debe fallar")
//        void validarPrescripcion_DuracionNegativa_DebeFallar() {
//            // Arrange
//            Prescripcion prescripcionDuracionInvalida = prescripcionValida.toBuilder()
//                    .duracionDias(-1)
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(prescripcionDuracionInvalida, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            assertTrue(exception.getMessage().contains("debe ser mayor a cero días"));
//        }
//
//        @Test
//        @DisplayName("Validar prescripción de antibiótico con duración muy corta debe fallar")
//        void validarPrescripcion_AntibioticoDuracionCorta_DebeFallar() {
//            // Arrange
//            Prescripcion antibiotico = Prescripcion.builder()
//                    .medicamento("Amoxicilina")
//                    .dosis("500 mg")
//                    .frecuencia("cada 8 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(2) // Duración muy corta
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(antibiotico, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            assertTrue(exception.getMessage().contains("duración mínima"));
//        }
//
//        @Test
//        @DisplayName("Validar prescripción de medicamento controlado con duración excesiva debe fallar")
//        void validarPrescripcion_ControladorDuracionExcesiva_DebeFallar() {
//            // Arrange
//            Prescripcion controlado = Prescripcion.builder()
//                    .medicamento("Alprazolam")
//                    .dosis("0.5 mg")
//                    .frecuencia("cada 12 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(45) // Duración excesiva
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(controlado, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            assertTrue(exception.getMessage().contains("duración máxima"));
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests de Validación de Campos Obligatorios")
//    class TestsValidacionCamposObligatorios {
//
//        @Test
//        @DisplayName("Validar prescripción de medicamento controlado sin justificación clínica debe fallar")
//        void validarPrescripcion_ControladoSinJustificacion_DebeFallar() {
//            // Arrange
//            Prescripcion controlado = Prescripcion.builder()
//                    .medicamento("Alprazolam")
//                    .dosis("0.5 mg")
//                    .frecuencia("cada 12 horas")
//                    .viaAdministracion("Oral")
//                    .duracionDias(15)
//                    .fechaPrescripcion(LocalDate.now())
//                    .requiereRecetaEspecial(true)
//                    // Sin justificación clínica
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(controlado, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            assertTrue(exception.getMessage().contains("justificación clínica es obligatoria"));
//        }
//
//        @Test
//        @DisplayName("Validar prescripción de antibiótico sin vía de administración debe fallar")
//        void validarPrescripcion_AntibioticoSinViaAdministracion_DebeFallar() {
//            // Arrange
//            Prescripcion antibiotico = Prescripcion.builder()
//                    .medicamento("Amoxicilina")
//                    .dosis("500 mg")
//                    .frecuencia("cada 8 horas")
//                    // Sin vía de administración
//                    .duracionDias(7)
//                    .fechaPrescripcion(LocalDate.now())
//                    .build();
//
//            when(interaccionesService.verificarInteracciones(any(), any())).thenReturn(new ArrayList<>());
//
//            // Act & Assert
//            PrescripcionInvalidaException exception = assertThrows(PrescripcionInvalidaException.class, () -> {
//                validationService.validarPrescripcion(antibiotico, prescripcionesExistentes, datosPacienteAdulto);
//            });
//
//            assertTrue(exception.getMessage().contains("vía de administración es obligatoria"));
//        }
//    }
//
//    // ========== MÉTODOS AUXILIARES ==========
//
//    private Prescripcion crearPrescripcionValida() {
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
//    private DatosPaciente crearDatosPacienteAdulto() {
//        return DatosPaciente.builder()
//                .cedula("0987654321")
//                .numeroHistoriaClinica("HC-12345")
//                .primerNombre("Juan")
//                .apellidoPaterno("Pérez")
//                .fechaNacimiento(LocalDate.now().minusYears(45))
//                .sexo(Sexo.MASCULINO)
//                .build();
//    }
//
//    private DatosPaciente crearDatosPacienteNino() {
//        return DatosPaciente.builder()
//                .cedula("0987654322")
//                .numeroHistoriaClinica("HC-12346")
//                .primerNombre("Pedro")
//                .apellidoPaterno("Gómez")
//                .fechaNacimiento(LocalDate.now().minusYears(10))
//                .sexo(Sexo.MASCULINO)
//                .build();
//    }
//}