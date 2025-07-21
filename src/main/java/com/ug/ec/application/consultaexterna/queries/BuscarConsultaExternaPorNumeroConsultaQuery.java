package com.ug.ec.application.consultaexterna.queries;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuscarConsultaExternaPorNumeroConsultaQuery {
    private String numeroConsulta;
}