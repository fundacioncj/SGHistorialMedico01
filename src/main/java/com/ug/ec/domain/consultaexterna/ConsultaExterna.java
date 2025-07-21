package com.ug.ec.domain.consultaexterna;

import lombok.Value;
import lombok.Builder;
import lombok.With;

import com.ug.ec.domain.consultaexterna.valueobjects.*;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaIncompletaException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Value
@Builder(toBuilder = true)
@With
public class ConsultaExterna {
    
    String id;
    
    String numeroConsulta;
    
    DatosFormulario datosFormulario;
    DatosPaciente datosPaciente;
    DatosConsulta datosConsulta;
    Anamnesis anamnesis;
    ExamenFisico examenFisico;
    List<Diagnostico> diagnosticos;
    PlanTratamiento planTratamiento;
    EstadoConsulta estado;
    DatosAuditoria auditoria;
    
    @Builder.Default
    Map<String, Object> camposAdicionales = new HashMap<>();
    
    // ========== MÉTODOS DE DOMINIO INMUTABLES ==========
    
    /**
     * Marca la consulta como completada retornando nueva instancia
     */
    public ConsultaExterna completar() {
        DatosAuditoria auditoriaActualizada = auditoria != null ? 
            auditoria.actualizar(auditoria.getModificadoPor()) : 
            DatosAuditoria.crearNuevo("SISTEMA");
            
        return this.toBuilder()
            .estado(EstadoConsulta.COMPLETADA)
            .auditoria(auditoriaActualizada)
            .build();
    }
    
    /**
     * Agrega un diagnóstico retornando nueva instancia
     */
    public ConsultaExterna agregarDiagnostico(Diagnostico diagnostico) {
        if (diagnostico == null) {
            throw new IllegalArgumentException("El diagnóstico no puede ser nulo");
        }
        
        List<Diagnostico> diagnosticosActualizados = diagnosticos != null ? 
            List.copyOf(diagnosticos) : 
            List.of();
            
        diagnosticosActualizados.add(diagnostico);
        
        DatosAuditoria auditoriaActualizada = auditoria != null ? 
            auditoria.actualizar(auditoria.getModificadoPor()) : 
            DatosAuditoria.crearNuevo("SISTEMA");
            
        return this.toBuilder()
            .diagnosticos(diagnosticosActualizados)
            .auditoria(auditoriaActualizada)
            .build();
    }
    
    /**
     * Actualiza el plan de tratamiento retornando nueva instancia
     */
    public ConsultaExterna actualizarPlanTratamiento(PlanTratamiento nuevoPlan) {
        if (nuevoPlan == null) {
            throw new IllegalArgumentException("El plan de tratamiento no puede ser nulo");
        }
        
        DatosAuditoria auditoriaActualizada = auditoria != null ? 
            auditoria.actualizar(auditoria.getModificadoPor()) : 
            DatosAuditoria.crearNuevo("SISTEMA");
            
        return this.toBuilder()
            .planTratamiento(nuevoPlan)
            .auditoria(auditoriaActualizada)
            .build();
    }
    
    /**
     * Agrega interconsultas al plan existente
     */
    public ConsultaExterna agregarInterconsultas(List<Interconsulta> nuevasInterconsultas) {
        if (nuevasInterconsultas == null || nuevasInterconsultas.isEmpty()) {
            return this;
        }
        
        PlanTratamiento planActualizado = planTratamiento != null ? 
            planTratamiento.agregarInterconsultas(nuevasInterconsultas) :
            PlanTratamiento.builder()
                .interconsultas(nuevasInterconsultas)
                .prescripciones(List.of())
                .indicacionesGenerales(List.of())
                .citasSeguimiento(List.of())
                .recomendaciones(List.of())
                .build();
                
        return actualizarPlanTratamiento(planActualizado);
    }
    
    // ========== MÉTODOS DE CONSULTA ==========
    
    public boolean esConsultaPrimeraVez() {
        return datosConsulta != null && 
               datosConsulta.getTipoConsulta() == TipoConsulta.PRIMERA_VEZ;
    }
    
    public boolean requiereInterconsulta() {
        return planTratamiento != null && 
               planTratamiento.getInterconsultas() != null && 
               !planTratamiento.getInterconsultas().isEmpty();
    }
    
    public boolean estaCompletada() {
        return EstadoConsulta.COMPLETADA.equals(estado);
    }
    
    public boolean esUrgente() {
        return examenFisico != null && 
               examenFisico.getSignosVitales() != null &&
               examenFisico.getSignosVitales().requiereAtencionUrgente();
    }
    
    public int cantidadDiagnosticos() {
        return diagnosticos != null ? diagnosticos.size() : 0;
    }
    
    public boolean tieneDiagnosticosPrincipales() {
        return diagnosticos != null && 
               diagnosticos.stream()
                   .anyMatch(diagnostico -> diagnostico.getTipo() != null && 
                            diagnostico.getTipo().equals("PRINCIPAL"));
    }
    
    // ========== VALIDACIONES DE DOMINIO ==========
    
    /**
     * Valida la completitud de la consulta
     */
    public void validarCompletitud() {
        if (diagnosticos == null || diagnosticos.isEmpty()) {
            throw new ConsultaExternaIncompletaException("La consulta debe tener al menos un diagnóstico");
        }
        
        if (anamnesis == null || anamnesis.getEnfermedadActual() == null) {
            throw new ConsultaExternaIncompletaException("La anamnesis es obligatoria");
        }
        
        if (examenFisico == null) {
            throw new ConsultaExternaIncompletaException("El examen físico es obligatorio");
        }
        
        if (datosPaciente == null || datosPaciente.getCedula() == null) {
            throw new ConsultaExternaIncompletaException("Los datos del paciente son obligatorios");
        }
    }
    
    /**
     * Valida si la consulta puede ser completada
     */
    public boolean puedeCompletarse() {
        try {
            validarCompletitud();
            return true;
        } catch (ConsultaExternaIncompletaException e) {
            return false;
        }
    }
    
    /**
     * Obtiene la cédula del paciente de forma segura
     */
    public String obtenerCedulaPaciente() {
        return datosPaciente != null ? datosPaciente.getCedula() : "DESCONOCIDO";
    }
    
    /**
     * Obtiene el número de consulta de forma segura
     */
    public String obtenerNumeroConsulta() {
        if (datosConsulta != null && datosConsulta.getNumeroConsulta() != null) {
            return datosConsulta.getNumeroConsulta();
        }
        return numeroConsulta != null ? numeroConsulta : "SIN_NUMERO";
    }
}