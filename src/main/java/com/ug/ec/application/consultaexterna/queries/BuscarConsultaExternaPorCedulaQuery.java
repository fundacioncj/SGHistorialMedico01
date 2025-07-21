package com.ug.ec.application.consultaexterna.queries;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuscarConsultaExternaPorCedulaQuery {
    private String cedula;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private int pagina;
    private int tamanio;
}