package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Value;
import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.ArrayList;

@Value
@Builder(toBuilder = true)
@With
public class PlanTratamiento {
    
    @Builder.Default
    List<Prescripcion> prescripciones = List.of();
    
    @Builder.Default
    List<String> indicacionesGenerales = List.of();
    
    @Builder.Default
    List<CitaSeguimiento> citasSeguimiento = List.of();
    
    @Builder.Default
    List<Interconsulta> interconsultas = List.of();
    
    @Builder.Default
    List<String> recomendaciones = List.of();
    
    String planEducacional;
    
    // ========== MÉTODOS DE DOMINIO INMUTABLES ==========
    
    /**
     * Agrega interconsultas retornando nueva instancia
     */
    public PlanTratamiento agregarInterconsultas(List<Interconsulta> nuevasInterconsultas) {
        if (nuevasInterconsultas == null || nuevasInterconsultas.isEmpty()) {
            return this;
        }
        
        List<Interconsulta> interconsultasActualizadas = new ArrayList<>(interconsultas);
        interconsultasActualizadas.addAll(nuevasInterconsultas);
        
        return this.toBuilder()
            .interconsultas(interconsultasActualizadas)
            .build();
    }
    
    /**
     * Agrega prescripciones retornando nueva instancia
     */
    public PlanTratamiento agregarPrescripciones(List<Prescripcion> nuevasPrescripciones) {
        if (nuevasPrescripciones == null || nuevasPrescripciones.isEmpty()) {
            return this;
        }
        
        List<Prescripcion> prescripcionesActualizadas = new ArrayList<>(prescripciones);
        prescripcionesActualizadas.addAll(nuevasPrescripciones);
        
        return this.toBuilder()
            .prescripciones(prescripcionesActualizadas)
            .build();
    }
    
    /**
     * Agrega citas de seguimiento retornando nueva instancia
     */
    public PlanTratamiento agregarCitasSeguimiento(List<CitaSeguimiento> nuevasCitas) {
        if (nuevasCitas == null || nuevasCitas.isEmpty()) {
            return this;
        }
        
        List<CitaSeguimiento> citasActualizadas = new ArrayList<>(citasSeguimiento);
        citasActualizadas.addAll(nuevasCitas);
        
        return this.toBuilder()
            .citasSeguimiento(citasActualizadas)
            .build();
    }
    
    /**
     * Agrega recomendaciones retornando nueva instancia
     */
    public PlanTratamiento agregarRecomendaciones(List<String> nuevasRecomendaciones) {
        if (nuevasRecomendaciones == null || nuevasRecomendaciones.isEmpty()) {
            return this;
        }
        
        List<String> recomendacionesActualizadas = new ArrayList<>(recomendaciones);
        recomendacionesActualizadas.addAll(nuevasRecomendaciones);
        
        return this.toBuilder()
            .recomendaciones(recomendacionesActualizadas)
            .build();
    }
    
    /**
     * Actualiza plan educacional retornando nueva instancia
     */
    public PlanTratamiento actualizarPlanEducacional(String nuevoPlan) {
        return this.toBuilder()
            .planEducacional(nuevoPlan)
            .build();
    }
    
    // ========== MÉTODOS DE CONSULTA ==========
    
    public boolean tienePrescripciones() {
        return prescripciones != null && !prescripciones.isEmpty();
    }
    
    public boolean requiereSeguimiento() {
        return citasSeguimiento != null && !citasSeguimiento.isEmpty();
    }
    
    public int cantidadInterconsultas() {
        return interconsultas != null ? interconsultas.size() : 0;
    }
    
    public boolean tieneInterconsultasUrgentes() {
        return interconsultas != null && 
               interconsultas.stream().anyMatch(Interconsulta::esUrgente);
    }
    
    public boolean tienePrescripcionesControladasEspeciales() {
        return prescripciones != null && 
               prescripciones.stream().anyMatch(Prescripcion::esControlada);
    }
    
    public boolean requiereSegimientoInmediato() {
        return citasSeguimiento != null && 
               citasSeguimiento.stream().anyMatch(CitaSeguimiento::esUrgente);
    }
    
    public List<Prescripcion> obtenerAntibioticos() {
        if (prescripciones == null || prescripciones.isEmpty()) {
            return List.of();
        }
        
        return prescripciones.stream()
                .filter(Prescripcion::esAntibiotico)
                .toList();
    }
    
    public List<Prescripcion> obtenerAnalgesicos() {
        if (prescripciones == null || prescripciones.isEmpty()) {
            return List.of();
        }
        
        return prescripciones.stream()
                .filter(Prescripcion::esAnalgesico)
                .toList();
    }
    
    public List<CitaSeguimiento> obtenerCitasProximas() {
        if (citasSeguimiento == null || citasSeguimiento.isEmpty()) {
            return List.of();
        }
        
        return citasSeguimiento.stream()
                .filter(CitaSeguimiento::esCitaProxima)
                .toList();
    }
    
    public List<Interconsulta> obtenerInterconsultasUrgentes() {
        if (interconsultas == null || interconsultas.isEmpty()) {
            return List.of();
        }
        
        return interconsultas.stream()
                .filter(Interconsulta::esUrgente)
                .toList();
    }
    
    public List<Interconsulta> obtenerInterconsultasPorEspecialidad(String especialidad) {
        if (interconsultas == null || interconsultas.isEmpty()) {
            return List.of();
        }
        
        return interconsultas.stream()
                .filter(interconsulta -> especialidad.equals(interconsulta.getEspecialidad()))
                .toList();
    }
    
    public boolean esPlanCompleto() {
        return tienePrescripciones() && 
               (requiereSeguimiento() || tieneInterconsultasUrgentes()) &&
               planEducacional != null && !planEducacional.trim().isEmpty();
    }
}