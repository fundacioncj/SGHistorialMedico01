package com.ug.ec.application.consultaexterna.queries;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuscarConsultasExternasPorFechaQuery {
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private String especialidad;
    private String medicoTratante;
    private EstadoConsulta estado;
    private int pagina;
    private int tamanio;
}