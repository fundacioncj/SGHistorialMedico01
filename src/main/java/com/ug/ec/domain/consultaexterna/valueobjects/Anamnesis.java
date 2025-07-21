package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Anamnesis {
    
    @NotBlank(message = "La enfermedad actual es obligatoria")
    private String enfermedadActual;
    
    private String antecedentesPatologicosPersonales;
    private String antecedentesPatologicosFamiliares;
    private String antecedentesQuirurgicos;
    private String antecedentesGinecoObstetricos;
    
    private HabitosPersonales habitos;
    private List<String> medicamentosActuales;
    private List<String> alergias;
    
    private String revisionSistemas;
    private String observaciones;
    
    // Métodos de dominio
    public boolean tieneAntecedentesPatologicos() {
        return (antecedentesPatologicosPersonales != null && 
                !antecedentesPatologicosPersonales.trim().isEmpty()) ||
               (antecedentesPatologicosFamiliares != null && 
                !antecedentesPatologicosFamiliares.trim().isEmpty());
    }
    
    public boolean tieneAlergias() {
        return alergias != null && !alergias.isEmpty();
    }
}