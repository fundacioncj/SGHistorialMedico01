package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Value;
import lombok.Builder;
import lombok.With;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@With
@Jacksonized
public class DatosFormulario {
    
    @NotBlank(message = "El número de formulario es obligatorio")
    String numeroFormulario;
    
    @NotBlank(message = "El establecimiento es obligatorio")
    String establecimiento;
    
    @NotBlank(message = "El código de establecimiento es obligatorio")
    @Pattern(regexp = "^[0-9]{3,6}$", message = "El código debe tener entre 3 y 6 dígitos")
    String codigoEstablecimiento;
    
    String areaSalud;
    String distrito;
    String canton;
    String provincia;
    
    // ========== MÉTODOS DE DOMINIO ==========
    
    /**
     * Crea un formulario estándar para un establecimiento
     */
    public static DatosFormulario crearFormularioEstandar(String establecimiento, String codigoEstablecimiento) {
        return DatosFormulario.builder()
                .numeroFormulario("HCU-" + System.currentTimeMillis())
                .establecimiento(establecimiento)
                .codigoEstablecimiento(codigoEstablecimiento)
                .build();
    }
    
    /**
     * Valida si el formulario tiene datos mínimos
     */
    public boolean esValido() {
        return numeroFormulario != null && !numeroFormulario.trim().isEmpty() &&
               establecimiento != null && !establecimiento.trim().isEmpty() &&
               codigoEstablecimiento != null && !codigoEstablecimiento.trim().isEmpty();
    }
    
    /**
     * Obtiene descripción completa del establecimiento
     */
    public String obtenerDescripcionCompleta() {
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(establecimiento);
        
        if (areaSalud != null && !areaSalud.trim().isEmpty()) {
            descripcion.append(" - ").append(areaSalud);
        }
        
        if (distrito != null && !distrito.trim().isEmpty()) {
            descripcion.append(" - ").append(distrito);
        }
        
        return descripcion.toString();
    }
}