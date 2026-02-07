package com.ug.ec.infrastructure.pdf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacientePdfDto {
    private PersonaPdfDto persona;
    private Integer edad;
}