package com.ug.ec.application.signosvitales.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListarSignosVitalesPorPacienteQuery {
    private String pacienteId;
}