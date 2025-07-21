package com.ug.ec.application.consultaexterna.handlers;

import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.ActualizarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.EliminarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaNotFoundException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsultaExternaCommandHandler {

    private final ConsultaExternaRepository repository;
    
    public ConsultaExternaCommandHandler(@Qualifier("consultaExternaRepositoryImpl") ConsultaExternaRepository repository) {
        this.repository = repository;
    }

    public String handle(CrearConsultaExternaCommand command) {
        log.info("Iniciando creación de consulta externa para paciente: {}", 
                command.getDatosPaciente().getCedula());
        
        try {
            ConsultaExterna consulta = ConsultaExterna.builder()
                    .numeroConsulta(command.getDatosConsulta().getNumeroConsulta())
                    .datosFormulario(command.getDatosFormulario())
                    .datosPaciente(command.getDatosPaciente())
                    .datosConsulta(command.getDatosConsulta())
                    .anamnesis(command.getAnamnesis())
                    .examenFisico(command.getExamenFisico())
                    .diagnosticos(command.getDiagnosticos())
                    .planTratamiento(command.getPlanTratamiento())
                    .estado(EstadoConsulta.EN_PROCESO)
                    .auditoria(DatosAuditoria.crearNuevo("SISTEMA"))
                    .build();
            
            ConsultaExterna consultaGuardada = repository.save(consulta);
            
            log.info("Consulta externa creada exitosamente con ID: {}", consultaGuardada.getId());
            return consultaGuardada.getId();
            
        } catch (Exception e) {
            log.error("Error al crear consulta externa: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void handle(ActualizarConsultaExternaCommand command) {
        log.info("Iniciando actualización de consulta externa ID: {}", command.getId());
        
        ConsultaExterna consultaExistente = repository.findById(command.getId())
                .orElseThrow(() -> new ConsultaExternaNotFoundException("No se encontró la consulta con ID: " + command.getId()));
        
        ConsultaExterna consultaActualizada = consultaExistente.toBuilder()
                .datosFormulario(command.getDatosFormulario() != null ? command.getDatosFormulario() : consultaExistente.getDatosFormulario())
                .datosPaciente(command.getDatosPaciente() != null ? command.getDatosPaciente() : consultaExistente.getDatosPaciente())
                .datosConsulta(command.getDatosConsulta() != null ? command.getDatosConsulta() : consultaExistente.getDatosConsulta())
                .anamnesis(command.getAnamnesis() != null ? command.getAnamnesis() : consultaExistente.getAnamnesis())
                .examenFisico(command.getExamenFisico() != null ? command.getExamenFisico() : consultaExistente.getExamenFisico())
                .diagnosticos(command.getDiagnosticos() != null ? command.getDiagnosticos() : consultaExistente.getDiagnosticos())
                .planTratamiento(command.getPlanTratamiento() != null ? command.getPlanTratamiento() : consultaExistente.getPlanTratamiento())
                .auditoria(consultaExistente.getAuditoria() != null ? 
                    consultaExistente.getAuditoria().actualizar("SISTEMA") : 
                    DatosAuditoria.crearNuevo("SISTEMA"))
                .build();
        
        repository.save(consultaActualizada);
        
        log.info("Consulta externa actualizada exitosamente ID: {}", command.getId());
    }

    public void handle(EliminarConsultaExternaCommand command) {
        log.info("Iniciando eliminación de consulta externa ID: {}", command.getId());
        
        ConsultaExterna consultaExistente = repository.findById(command.getId())
                .orElseThrow(() -> new ConsultaExternaNotFoundException("No se encontró la consulta con ID: " + command.getId()));
        
        // Verificar si se puede eliminar según el estado
        if (EstadoConsulta.COMPLETADA.equals(consultaExistente.getEstado())) {
            throw new IllegalStateException("No se puede eliminar una consulta completada");
        }
        
        repository.deleteById(command.getId());
        
        log.info("Consulta externa eliminada exitosamente. Número: {}, Paciente: {}", 
                consultaExistente.getNumeroConsulta(),
                consultaExistente.getDatosPaciente().getCedula());
    }
}