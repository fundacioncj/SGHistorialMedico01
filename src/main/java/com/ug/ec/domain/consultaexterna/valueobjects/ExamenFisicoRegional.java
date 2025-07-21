package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamenFisicoRegional {
    
    private String region;
    private String hallazgos;
    private String observaciones;
    
    @Builder.Default
    private boolean normal = true;
    
    public String obtenerDescripcionFormateada() {
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(region != null ? region : "Región no especificada");
        descripcion.append(": ");
        
        if (normal) {
            descripcion.append("Normal");
        } else {
            descripcion.append(hallazgos != null ? hallazgos : "Sin hallazgos específicos");
        }
        
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            descripcion.append(" - ").append(observaciones);
        }
        
        return descripcion.toString();
    }
    
    public boolean tieneHallazgosAnormales() {
        return !normal || (hallazgos != null && !hallazgos.trim().isEmpty());
    }
    
    // Método corregido para la referencia en ExamenFisico
    public boolean isNormal() {
        return normal;
    }
}