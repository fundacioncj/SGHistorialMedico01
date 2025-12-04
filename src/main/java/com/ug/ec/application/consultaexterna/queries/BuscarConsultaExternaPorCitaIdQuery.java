package com.ug.ec.application.consultaexterna.queries;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuscarConsultaExternaPorCitaIdQuery {
    private String id;
}
