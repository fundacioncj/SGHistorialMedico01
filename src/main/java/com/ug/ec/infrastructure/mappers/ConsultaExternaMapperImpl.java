package com.ug.ec.infrastructure.mappers;

import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
import com.ug.ec.application.consultaexterna.mappers.ConsultaExternaMapper;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ConsultaExternaMapperImpl extends ConsultaExternaMapper {
    
    @Override
    public ConsultaExternaDto entityToDto(ConsultaExterna entity) {
        if (entity == null) {
            return null;
        }
        
        return ConsultaExternaDto.builder()
                .id(entity.getId())
                .numeroConsulta(entity.getNumeroConsulta())
                .historiaClinicaId(entity.getHistoriaClinicaId())
                .signosVitalesId(entity.getSignosVitalesId())
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
    
    @Override
    public ConsultaExterna dtoToEntity(ConsultaExternaDto dto) {
        if (dto == null) {
            return null;
        }
        
        return ConsultaExterna.builder()
                .id(dto.getId())
                .numeroConsulta(dto.getNumeroConsulta())
//                .datosFormulario(dto.getDatosFormulario())
//                .datosPaciente(dto.getDatosPaciente())
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
}