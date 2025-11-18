package com.ug.ec.domain.consultaexterna.valueobjects;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class ExamenFisico {
    

    
    private String examenGeneral;
    
    @Builder.Default
    private List<ExamenFisicoRegional> examenesRegionales = new ArrayList<>();

    @Builder.Default
    private List<ExamenFisicoSistemico> examenesSistemicos = new ArrayList<>();
    

    


    

}