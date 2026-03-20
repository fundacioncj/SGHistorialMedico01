package com.ug.ec.infrastructure.pdf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CoreMedicoPdfResponse {
    private String cedula;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private List<String> especialidades;
    private String firma; // Base64 encoded image
}