package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Anamnesis {
    
    @NotBlank(message = "La enfermedad actual es obligatoria")
    private String enfermedadActual;


    private List<String> medicamentosActuales;
    
    private String revisionSistemas;
    private String observaciones;
    

}