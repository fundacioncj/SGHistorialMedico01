package com.ug.ec.application.historiaclinica.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObtenerHistoriaClinicaPorPacienteQuery {
    private String pacienteId;
    private String cedulaPaciente;
}
