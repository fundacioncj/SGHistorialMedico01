package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class ExamenFisicoRegional {
    
    private String region;
    private List<String> hallazgos;
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
            if (hallazgos != null && !hallazgos.isEmpty()) {
                descripcion.append(String.join(", ", hallazgos));
            } else {
                descripcion.append("Sin hallazgos específicos");
            }
        }
        
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            descripcion.append(" - ").append(observaciones);
        }
        
        return descripcion.toString();
    }
    
    public boolean tieneHallazgosAnormales() {
        return !normal || (hallazgos != null && !hallazgos.isEmpty());
    }
    
    // Método corregido para la referencia en ExamenFisico
    public boolean isNormal() {
        return normal;
    }
}