package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitosPersonales {
    
    @Builder.Default
    private boolean fuma = false;
    
    @Builder.Default
    private boolean bebeAlcohol = false;
    
    @Builder.Default
    private boolean usaDrogas = false;
    
    private String detalleTabaquismo;
    private String detalleAlcoholismo;
    private String detalleDrogas;
    
    private String ejercicio;
    private String dieta;
    private String sueno;
    
    private String observaciones;
    
    public boolean tieneHabitosRiesgo() {
        return fuma || bebeAlcohol || usaDrogas;
    }
    
    /**
     * Alias para tieneHabitosRiesgo() para mantener compatibilidad con el código existente
     * @return true si tiene hábitos nocivos (tabaquismo, alcoholismo, drogas)
     */
    public boolean tieneHabitosNocivos() {
        return tieneHabitosRiesgo();
    }
    
    /**
     * @return Boolean que indica si la persona fuma (null si no se ha especificado)
     */
    public Boolean getTabaquismo() {
        return fuma ? Boolean.TRUE : Boolean.FALSE;
    }
    
    /**
     * @return Boolean que indica si la persona consume alcohol (null si no se ha especificado)
     */
    public Boolean getAlcoholismo() {
        return bebeAlcohol ? Boolean.TRUE : Boolean.FALSE;
    }
    
    /**
     * @return Boolean que indica si la persona es sedentaria (null si no se ha especificado)
     */
    public Boolean getSedentarismo() {
        // Por defecto, si no hay información de ejercicio, se considera sedentario
        return ejercicio == null || ejercicio.isEmpty() || 
               ejercicio.toLowerCase().contains("no") || 
               ejercicio.toLowerCase().contains("sedentario") ? 
               Boolean.TRUE : Boolean.FALSE;
    }
    
    public String obtenerResumenRiesgos() {
        if (!tieneHabitosRiesgo()) {
            return "Sin hábitos de riesgo reportados";
        }
        
        StringBuilder resumen = new StringBuilder("Hábitos de riesgo: ");
        
        if (fuma) {
            resumen.append("Tabaquismo");
            if (detalleTabaquismo != null) {
                resumen.append(" (").append(detalleTabaquismo).append(")");
            }
        }
        
        if (bebeAlcohol) {
            if (fuma) resumen.append(", ");
            resumen.append("Alcoholismo");
            if (detalleAlcoholismo != null) {
                resumen.append(" (").append(detalleAlcoholismo).append(")");
            }
        }
        
        if (usaDrogas) {
            if (fuma || bebeAlcohol) resumen.append(", ");
            resumen.append("Uso de drogas");
            if (detalleDrogas != null) {
                resumen.append(" (").append(detalleDrogas).append(")");
            }
        }
        
        return resumen.toString();
    }
    
    public static HabitosPersonales sinRiesgos() {
        return HabitosPersonales.builder()
                .fuma(false)
                .bebeAlcohol(false)
                .usaDrogas(false)
                .build();
    }
}