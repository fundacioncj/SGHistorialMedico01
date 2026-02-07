package com.ug.ec.infrastructure.pdf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicoPdfDto {
    private PersonaPdfDto persona;
    private List<EspecialidadPdfDto> especialidades;
    private String firma;
}