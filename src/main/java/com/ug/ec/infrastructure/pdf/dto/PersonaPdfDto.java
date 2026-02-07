package com.ug.ec.infrastructure.pdf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaPdfDto {
    private String cedula;
    private String primerApellido;
    private String segundoApellido;
    private String primerNombre;
    private String segundoNombre;
}