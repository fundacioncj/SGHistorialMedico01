package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Value;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.ArrayList;

@Value
@Builder(toBuilder = true)
@With
@Jacksonized
public class PlanTratamiento {
    
    @Builder.Default
    List<Prescripcion> prescripciones = List.of();
    

    String indicacionesGenerales ;

    
    @Builder.Default
    List<String> recomendaciones = List.of();
    
    String planEducacional;
    
    // ========== MÉTODOS DE DOMINIO INMUTABLES ==========
    
    /**
     * Agrega interconsultas retornando nueva instancia
     */

    
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
    

    

    

    
    public boolean tienePrescripcionesControladasEspeciales() {
        return prescripciones != null && 
               prescripciones.stream().anyMatch(Prescripcion::esControlada);
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

    

    


}