package com.ug.ec.domain.historiaclinica;

import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@With
public class HistorialClinico {

    String id;
    String idPaciente;
    String cedulaPaciente;

    DatosFormulario datosFormulario;

    AntecedentePatologicoPersonal antecedentesPatologicosPersonales;
    String antecedentesPatologicosFamiliares;



    LocalDateTime fechaUltimaActualizacion;
    DatosAuditoria auditoria;
}
