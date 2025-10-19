package com.ug.ec.application.consultaexterna.handlers;

import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.ActualizarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.EliminarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.application.consultaexterna.mappers.ConsultaExternaMapper;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaNotFoundException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsultaExternaCommandHandler {

    private final ConsultaExternaRepository repository;
    private final ConsultaExternaMapper mapper;
    
    public ConsultaExternaCommandHandler(@Qualifier("consultaExternaRepositoryImpl") ConsultaExternaRepository repository,
                                         ConsultaExternaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public String handle(CrearConsultaExternaCommand command) {

        
        try {
            ConsultaExterna consulta = mapper.fromCommand(command);
            
            // Asegurar auditoría con usuario creador del comando
            ConsultaExterna consultaConAuditoria = consulta.toBuilder()
                    .auditoria(DatosAuditoria.crearNuevo(command.getUsuarioCreador()))
                    .build();
            
            ConsultaExterna consultaGuardada = repository.save(consultaConAuditoria);
            
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
        
        ConsultaExterna consultaActualizada = mapper.updateFromCommand(consultaExistente, command);
        
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
        
        // Borrado lógico: marcamos el registro como eliminado y registramos auditoría de eliminación
        ConsultaExterna consultaEliminada = consultaExistente.toBuilder()
                .eliminada(true)
                .fechaEliminacion(java.time.LocalDateTime.now())
                .usuarioEliminador(command.getUsuarioEliminador())
                .motivoEliminacion(command.getMotivoEliminacion())
                .build();
        
        repository.save(consultaEliminada);
        
        log.info("Consulta externa eliminada lógicamente. Número: {}, Paciente: {}", 
                consultaExistente.getNumeroConsulta(),
                consultaExistente.getCedulaPaciente());
    }
}