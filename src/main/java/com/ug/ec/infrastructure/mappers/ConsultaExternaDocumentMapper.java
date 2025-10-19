package com.ug.ec.infrastructure.mappers;

import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.infrastructure.persistence.consultaexterna.ConsultaExternaDocument;
import org.springframework.stereotype.Component;

@Component
public class ConsultaExternaDocumentMapper {
    
    public ConsultaExternaDocument toDocument(ConsultaExterna domain) {
        if (domain == null) {
            return null;
        }
        
        return ConsultaExternaDocument.builder()
                .id(domain.getId())
                .numeroConsulta(domain.getNumeroConsulta())
                .historiaClinicaId(domain.getHistoriaClinicaId())
                .cedulaPaciente(domain.getCedulaPaciente())
                .datosConsulta(domain.getDatosConsulta())
                .anamnesis(domain.getAnamnesis())
                .examenFisico(domain.getExamenFisico())
                .diagnosticos(domain.getDiagnosticos())
                .planTratamiento(domain.getPlanTratamiento())
                .estado(domain.getEstado())
                .auditoria(domain.getAuditoria())
                .eliminada(domain.getEliminada())
                .fechaEliminacion(domain.getFechaEliminacion())
                .usuarioEliminador(domain.getUsuarioEliminador())
                .motivoEliminacion(domain.getMotivoEliminacion())
                .camposAdicionales(domain.getCamposAdicionales())
                .build();
    }
    
    public ConsultaExterna toDomain(ConsultaExternaDocument document) {
        if (document == null) {
            return null;
        }
        
        return ConsultaExterna.builder()
                .id(document.getId())
                .numeroConsulta(document.getNumeroConsulta())
                .historiaClinicaId(document.getHistoriaClinicaId())
                .cedulaPaciente(document.getCedulaPaciente())
                .datosConsulta(document.getDatosConsulta())
                .anamnesis(document.getAnamnesis())
                .examenFisico(document.getExamenFisico())
                .diagnosticos(document.getDiagnosticos())
                .planTratamiento(document.getPlanTratamiento())
                .estado(document.getEstado())
                .auditoria(document.getAuditoria())
                .eliminada(document.getEliminada() != null ? document.getEliminada() : false)
                .fechaEliminacion(document.getFechaEliminacion())
                .usuarioEliminador(document.getUsuarioEliminador())
                .motivoEliminacion(document.getMotivoEliminacion())
                .camposAdicionales(document.getCamposAdicionales())
                .build();
    }
}
