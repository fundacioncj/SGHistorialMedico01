package com.ug.ec.application.consultaexterna.mappers;

import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.ActualizarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;


@Component
@RequiredArgsConstructor
@Slf4j
public class ConsultaExternaMapper {
    
    /**
     * Convierte una entidad de dominio a un DTO completo
     */
    public ConsultaExternaDto entityToDto(ConsultaExterna entity) {
        if (entity == null) {
            return null;
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        return ConsultaExternaDto.builder()
                .id(entity.getId())
                .numeroConsulta(entity.getNumeroConsulta())
                .historiaClinicaId(entity.getHistoriaClinicaId())
                .cedulaPaciente(entity.getCedulaPaciente())
                .datosConsulta(entity.getDatosConsulta())
                .anamnesis(entity.getAnamnesis())
                .examenFisico(entity.getExamenFisico())
                .diagnosticos(entity.getDiagnosticos())
                .planTratamiento(entity.getPlanTratamiento())
                .estado(entity.getEstado())
                .auditoria(entity.getAuditoria())
                .camposAdicionales(entity.getCamposAdicionales())
                .build();
    }
    
    /**
     * Convierte un DTO a una entidad de dominio
     */
    public ConsultaExterna dtoToEntity(ConsultaExternaDto dto) {
        if (dto == null) {
            return null;
        }
        
        return ConsultaExterna.builder()
                .id(dto.getId())
                .numeroConsulta(dto.getNumeroConsulta())
                .historiaClinicaId(dto.getHistoriaClinicaId())
               .cedulaPaciente(dto.getCedulaPaciente())
                .datosConsulta(dto.getDatosConsulta())
                .anamnesis(dto.getAnamnesis())
                .examenFisico(dto.getExamenFisico())
                .diagnosticos(dto.getDiagnosticos())
                .planTratamiento(dto.getPlanTratamiento())
                .estado(dto.getEstado())
                .auditoria(dto.getAuditoria())
                .camposAdicionales(dto.getCamposAdicionales())
                .build();
    }

    public ConsultaExterna fromCommand(CrearConsultaExternaCommand command) {
        return ConsultaExterna.builder()
                .numeroConsulta(command.getDatosConsulta().getNumeroConsulta())
                .historiaClinicaId(command.getHistoriaClinicaId())
              .cedulaPaciente(command.getCedulaPaciente())
                .datosConsulta(command.getDatosConsulta())
                .anamnesis(command.getAnamnesis())
                .examenFisico(command.getExamenFisico())
                .diagnosticos(command.getDiagnosticos())
                .planTratamiento(command.getPlanTratamiento())
                .estado(EstadoConsulta.EN_PROCESO)
                .auditoria(DatosAuditoria.crearNuevo("SISTEMA"))
                .build();
    }

    public ConsultaExterna updateFromCommand(ConsultaExterna existente, ActualizarConsultaExternaCommand command) {
        DatosAuditoria auditoriaActualizada = existente.getAuditoria() != null ? 
            existente.getAuditoria().actualizar(command.getUsuarioActualizador() != null ? command.getUsuarioActualizador() : "SISTEMA") : 
            DatosAuditoria.crearNuevo("SISTEMA");

        return existente.toBuilder()
                .datosConsulta(command.getDatosConsulta() != null ? command.getDatosConsulta() : existente.getDatosConsulta())
                .anamnesis(command.getAnamnesis() != null ? command.getAnamnesis() : existente.getAnamnesis())
                .examenFisico(command.getExamenFisico() != null ? command.getExamenFisico() : existente.getExamenFisico())
                .diagnosticos(command.getDiagnosticos() != null ? command.getDiagnosticos() : existente.getDiagnosticos())
                .planTratamiento(command.getPlanTratamiento() != null ? command.getPlanTratamiento() : existente.getPlanTratamiento())
                .auditoria(auditoriaActualizada)
                .build();
    }

    // DTO reducido para listados
    public com.ug.ec.application.consultaexterna.dto.ConsultaExternaResumenDto entityToResumenDto(ConsultaExterna entity) {
        if (entity == null) {
            return null;
        }
        return com.ug.ec.application.consultaexterna.dto.ConsultaExternaResumenDto.builder()
                .id(entity.getId())
                .numeroConsulta(entity.obtenerNumeroConsulta())
                .cedulaPaciente(entity.getCedulaPaciente() != null ? entity.getCedulaPaciente() : null)
                .fechaConsulta(entity.getDatosConsulta() != null ? entity.getDatosConsulta().getFechaConsulta() : null)
                .especialidad(entity.getDatosConsulta() != null ? entity.getDatosConsulta().getEspecialidad() : null)
                .medicoTratante(entity.getDatosConsulta() != null ? entity.getDatosConsulta().getMedicoTratante() : null)
                .estado(entity.getEstado())
                .fechaCreacion(entity.getAuditoria() != null ? entity.getAuditoria().getFechaCreacion() : null)
                .creadoPor(entity.getAuditoria() != null ? entity.getAuditoria().getCreadoPor() : null)
                .build();
    }
}