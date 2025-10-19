package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class ExamenFisicoSistemico {

    private String sistema;
    private List<String> hallazgos;
    private String observaciones;

    @Builder.Default
    private boolean normal = true;
}
