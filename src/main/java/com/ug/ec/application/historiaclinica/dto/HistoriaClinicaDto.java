package com.ug.ec.application.historiaclinica.dto;


import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinicaDto {

    String id;
    String idPaciente;
    String cedulaPaciente;

    DatosFormulario datosFormulario;

    AntecedentePatologicoPersonal antecedentesPatologicosPersonales;
    String antecedentesPatologicosFamiliares;

    LocalDateTime fechaUltimaActualizacion;
    DatosAuditoria auditoria;
}
