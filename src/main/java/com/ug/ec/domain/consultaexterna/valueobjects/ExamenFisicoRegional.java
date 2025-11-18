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
    private boolean presente = false;

}