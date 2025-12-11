package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import com.ug.ec.domain.consultaexterna.enums.TipoDiagnostico;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Diagnostico {
    
//    @NotBlank(message = "El código CIE-10 es obligatorio")
//    @Pattern(regexp = "^[A-Z]\\d{2}(\\.\\d{1,2})?$", message = "El código CIE-10 debe tener formato válido")
    private String codigoCie10;
    
//    @NotBlank(message = "La descripción del diagnóstico es obligatoria")
    private String descripcion;
    
//    @NotNull(message = "El tipo de diagnóstico es obligatorio")
    private TipoDiagnostico tipo;
    
    private String observaciones;
    
//    @NotNull(message = "La fecha del diagnóstico es obligatoria")
    private LocalDateTime fechaDiagnostico;
    
    // Nuevos campos
    private String severidad; // LEVE, MODERADO, SEVERO
    private String estadioEnfermedad;
    private Boolean requiereSeguimiento;
    private Integer tiempoSeguimientoMeses;
    private String planSeguimiento;
    private List<String> manifestacionesClinicas;
    private List<String> factoresRiesgo;


    
    // Métodos de dominio
    public boolean esDiagnosticoPrincipal() {
        return this.tipo == TipoDiagnostico.PRINCIPAL;
    }
    
    public boolean esDiagnosticoSecundario() {
        return this.tipo == TipoDiagnostico.SECUNDARIO;
    }
    
    public boolean esDiagnosticoPresuntivo() {
        return this.tipo == TipoDiagnostico.PRESUNTIVO;
    }
    
    public boolean esDiagnosticoConfirmado() {
        return this.tipo == TipoDiagnostico.CONFIRMADO;
    }
    
    // Nuevos métodos de dominio
    public boolean esGrave() {
        return "SEVERO".equals(severidad) || 
               (manifestacionesClinicas != null && 
                manifestacionesClinicas.stream().anyMatch(m -> 
                    m.toLowerCase().contains("grave") || 
                    m.toLowerCase().contains("severo")));
    }

    

}