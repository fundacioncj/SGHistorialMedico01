package com.ug.ec.application.consultaexterna.handlers;

import com.ug.ec.application.consultaexterna.commands.ActualizarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.EliminarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
import com.ug.ec.domain.consultaexterna.exceptions.*;
import com.ug.ec.domain.consultaexterna.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultaExternaCommandHandler - Tests de Commands")
class ConsultaExternaCommandHandlerTest {

    @Mock
    private ConsultaExternaRepository consultaExternaRepository;

    @InjectMocks
    private ConsultaExternaCommandHandler commandHandler;

    private CrearConsultaExternaCommand crearCommand;
    private ActualizarConsultaExternaCommand actualizarCommand;
    private EliminarConsultaExternaCommand eliminarCommand;
    private ConsultaExterna consultaExistente;

    @BeforeEach
    void setUp() {
        crearCommand = CrearConsultaExternaCommand.builder()
                .usuarioCreador("usuario_test")
                .datosFormulario(DatosFormulario.builder()
                        .numeroFormulario("HCU-002")
                        .establecimiento("Hospital Test")
                        .build())
                .datosPaciente(DatosPaciente.builder()
                        .cedula("0987654321")
                        .numeroHistoriaClinica("HC-12345")
                        .primerNombre("Juan")
                        .apellidoPaterno("Pérez")
                        .build())
                .datosConsulta(DatosConsulta.builder()
                        .fechaConsulta(LocalDateTime.now())
                        .especialidad("Medicina General")
                        .medicoTratante("Dr. Test")
                        .tipoConsulta(TipoConsulta.PRIMERA_VEZ)
                        .motivoConsulta("Consulta de rutina")
                        .build())
                .anamnesis(Anamnesis.builder()
                        .enfermedadActual("Consulta de rutina")
                        .build())
                .examenFisico(ExamenFisico.builder()
                        .signosVitales(SignosVitales.builder()
                                .presionSistolica(120)
                                .presionDiastolica(80)
                                .build())
                        .build())
                .diagnosticos(List.of(
                        Diagnostico.builder()
                                .codigoCie10("Z00.0")
                                .descripcion("Examen médico general")
                                .build()
                ))
                .planTratamiento(PlanTratamiento.builder()
                        .indicacionesGenerales(List.of("Mantener hábitos saludables"))
                        .build())
                .build();

        consultaExistente = ConsultaExterna.builder()
                .id("consulta-123")
                .numeroConsulta("CE-123456")
                .datosPaciente(crearCommand.getDatosPaciente())
                .datosConsulta(crearCommand.getDatosConsulta())
                .anamnesis(crearCommand.getAnamnesis())
                .examenFisico(crearCommand.getExamenFisico())
                .diagnosticos(crearCommand.getDiagnosticos())
                .planTratamiento(crearCommand.getPlanTratamiento())
                .estado(EstadoConsulta.INICIADA)
                .auditoria(DatosAuditoria.crearNuevo("usuario_test"))
                .camposAdicionales(new HashMap<>())
                .build();

        actualizarCommand = ActualizarConsultaExternaCommand.builder()
                .id("consulta-123")
                .usuarioActualizador("usuario_actualizador")
                .diagnosticos(List.of(
                        Diagnostico.builder()
                                .codigoCie10("K59.9")
                                .descripcion("Diagnóstico actualizado")
                                .build()
                ))
                .build();

        eliminarCommand = EliminarConsultaExternaCommand.builder()
                .id("consulta-123")
                .usuarioEliminador("usuario_eliminador")
                .build();
    }

    @Nested
    @DisplayName("Tests de Creación de Consultas")
    class TestsCreacionConsultas {

        @Test
        @DisplayName("Crear consulta exitosa debe retornar ID")
        void handle_CrearConsultaExitosa_DebeRetornarId() {
            // Arrange
            when(consultaExternaRepository.existsByNumeroConsulta(anyString())).thenReturn(false);
            when(consultaExternaRepository.save(any(ConsultaExterna.class))).thenReturn(consultaExistente);

            // Act
            String resultado = commandHandler.handle(crearCommand);

            // Assert
            assertNotNull(resultado);
            assertEquals("consulta-123", resultado);
            verify(consultaExternaRepository).existsByNumeroConsulta(anyString());
            verify(consultaExternaRepository).save(any(ConsultaExterna.class));
        }

        @Test
        @DisplayName("Crear consulta con número existente debe lanzar excepción")
        void handle_CrearConsultaConNumeroExistente_DebeLanzarExcepcion() {
            // Arrange
            when(consultaExternaRepository.existsByNumeroConsulta(anyString())).thenReturn(true);

            // Act & Assert
            assertThrows(ConsultaExternaCreacionException.class, () -> {
                commandHandler.handle(crearCommand);
            });
            
            verify(consultaExternaRepository).existsByNumeroConsulta(anyString());
            verify(consultaExternaRepository, never()).save(any(ConsultaExterna.class));
        }

        @Test
        @DisplayName("Crear consulta debe usar immutabilidad")
        void handle_CrearConsulta_DebeUsarInmutabilidad() {
            // Arrange
            when(consultaExternaRepository.existsByNumeroConsulta(anyString())).thenReturn(false);
            when(consultaExternaRepository.save(any(ConsultaExterna.class))).thenReturn(consultaExistente);

            // Act
            commandHandler.handle(crearCommand);

            // Assert
            verify(consultaExternaRepository).save(argThat(consulta -> 
                consulta.getEstado() == EstadoConsulta.INICIADA &&
                consulta.getAuditoria() != null &&
                consulta.getAuditoria().getCreadoPor().equals("usuario_test")
            ));
        }

        @Test
        @DisplayName("Crear consulta incompleta debe lanzar excepción")
        void handle_CrearConsultaIncompleta_DebeLanzarExcepcion() {
            // Arrange
            CrearConsultaExternaCommand commandIncompleto = crearCommand.toBuilder()
                    .diagnosticos(null)
                    .build();
            
            when(consultaExternaRepository.existsByNumeroConsulta(anyString())).thenReturn(false);

            // Act & Assert
            assertThrows(ConsultaExternaCreacionException.class, () -> {
                commandHandler.handle(commandIncompleto);
            });
        }
    }

    @Nested
    @DisplayName("Tests de Actualización de Consultas")
    class TestsActualizacionConsultas {

        @Test
        @DisplayName("Actualizar consulta exitosa debe mantener inmutabilidad")
        void handle_ActualizarConsultaExitosa_DebeMantenerInmutabilidad() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.of(consultaExistente));
            when(consultaExternaRepository.save(any(ConsultaExterna.class))).thenReturn(consultaExistente);

            // Act
            commandHandler.handle(actualizarCommand);

            // Assert
            verify(consultaExternaRepository).save(argThat(consulta -> 
                !consulta.equals(consultaExistente) && // Nueva instancia
                consulta.getId().equals("consulta-123") &&
                consulta.getAuditoria().getModificadoPor().equals("usuario_actualizador")
            ));
        }

        @Test
        @DisplayName("Actualizar consulta inexistente debe lanzar excepción")
        void handle_ActualizarConsultaInexistente_DebeLanzarExcepcion() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ConsultaExternaNotFoundException.class, () -> {
                commandHandler.handle(actualizarCommand);
            });
            
            verify(consultaExternaRepository, never()).save(any(ConsultaExterna.class));
        }

        @Test
        @DisplayName("Actualizar consulta debe preservar campos no modificados")
        void handle_ActualizarConsulta_DebePreservarCamposNoModificados() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.of(consultaExistente));
            when(consultaExternaRepository.save(any(ConsultaExterna.class))).thenReturn(consultaExistente);

            // Act
            commandHandler.handle(actualizarCommand);

            // Assert
            verify(consultaExternaRepository).save(argThat(consulta -> 
                consulta.getDatosPaciente().equals(consultaExistente.getDatosPaciente()) &&
                consulta.getDatosConsulta().equals(consultaExistente.getDatosConsulta()) &&
                consulta.getAnamnesis().equals(consultaExistente.getAnamnesis())
            ));
        }

        @Test
        @DisplayName("Actualizar consulta con datos parciales debe funcionar")
        void handle_ActualizarConsultaConDatosParciales_DebeFuncionar() {
            // Arrange
            ActualizarConsultaExternaCommand comandoParcial = ActualizarConsultaExternaCommand.builder()
                    .id("consulta-123")
                    .usuarioActualizador("usuario_actualizador")
                    .diagnosticos(List.of(
                            Diagnostico.builder()
                                    .codigoCie10("K30")
                                    .descripcion("Nuevo diagnóstico")
                                    .build()
                    ))
                    .build();

            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.of(consultaExistente));
            when(consultaExternaRepository.save(any(ConsultaExterna.class))).thenReturn(consultaExistente);

            // Act
            commandHandler.handle(comandoParcial);

            // Assert
            verify(consultaExternaRepository).save(argThat(consulta -> 
                consulta.getDiagnosticos().size() == 1 &&
                consulta.getDiagnosticos().get(0).getCodigoCie10().equals("K30")
            ));
        }
    }

    @Nested
    @DisplayName("Tests de Eliminación de Consultas")
    class TestsEliminacionConsultas {

        @Test
        @DisplayName("Eliminar consulta exitosa debe ejecutar delete")
        void handle_EliminarConsultaExitosa_DebeEjecutarDelete() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.of(consultaExistente));

            // Act
            commandHandler.handle(eliminarCommand);

            // Assert
            verify(consultaExternaRepository).deleteById("consulta-123");
        }

        @Test
        @DisplayName("Eliminar consulta inexistente debe lanzar excepción")
        void handle_EliminarConsultaInexistente_DebeLanzarExcepcion() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ConsultaExternaNotFoundException.class, () -> {
                commandHandler.handle(eliminarCommand);
            });
            
            verify(consultaExternaRepository, never()).deleteById(anyString());
        }

        @Test
        @DisplayName("Eliminar consulta debe validar existencia antes de eliminar")
        void handle_EliminarConsulta_DebeValidarExistenciaAntesDeEliminar() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.of(consultaExistente));

            // Act
            commandHandler.handle(eliminarCommand);

            // Assert
            verify(consultaExternaRepository).findById("consulta-123");
            verify(consultaExternaRepository).deleteById("consulta-123");
        }
    }

    @Nested
    @DisplayName("Tests de Manejo de Errores")
    class TestsManejoErrores {

        @Test
        @DisplayName("Error en repositorio durante creación debe lanzar excepción envolvente")
        void handle_ErrorEnRepositorioDuranteCreacion_DebeLanzarExcepcionEnvolvente() {
            // Arrange
            when(consultaExternaRepository.existsByNumeroConsulta(anyString())).thenReturn(false);
            when(consultaExternaRepository.save(any(ConsultaExterna.class))).thenThrow(new RuntimeException("Error DB"));

            // Act & Assert
            ConsultaExternaCreacionException exception = assertThrows(
                    ConsultaExternaCreacionException.class,
                    () -> commandHandler.handle(crearCommand)
            );

            assertTrue(exception.getMessage().contains("Error al crear la consulta externa"));
            assertNotNull(exception.getCause());
        }

        @Test
        @DisplayName("Error en repositorio durante actualización debe lanzar excepción envolvente")
        void handle_ErrorEnRepositorioDuranteActualizacion_DebeLanzarExcepcionEnvolvente() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.of(consultaExistente));
            when(consultaExternaRepository.save(any(ConsultaExterna.class))).thenThrow(new RuntimeException("Error DB"));

            // Act & Assert
            ConsultaExternaActualizacionException exception = assertThrows(
                    ConsultaExternaActualizacionException.class,
                    () -> commandHandler.handle(actualizarCommand)
            );

            assertTrue(exception.getMessage().contains("Error al actualizar la consulta externa"));
            assertNotNull(exception.getCause());
        }

        @Test
        @DisplayName("Error en repositorio durante eliminación debe lanzar excepción envolvente")
        void handle_ErrorEnRepositorioDuranteEliminacion_DebeLanzarExcepcionEnvolvente() {
            // Arrange
            when(consultaExternaRepository.findById("consulta-123")).thenReturn(Optional.of(consultaExistente));
            doThrow(new RuntimeException("Error DB")).when(consultaExternaRepository).deleteById("consulta-123");

            // Act & Assert
            ConsultaExternaEliminacionException exception = assertThrows(
                    ConsultaExternaEliminacionException.class,
                    () -> commandHandler.handle(eliminarCommand)
            );

            assertTrue(exception.getMessage().contains("Error al eliminar la consulta externa"));
            assertNotNull(exception.getCause());
        }
    }
}