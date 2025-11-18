package com.ug.ec.infrastructure.rest.historiaclinica.request;

import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarHistoriaClinicaRequest {

    private String idPaciente;
    private String cedulaPaciente;

    @Valid
    private DatosFormulario datosFormulario;

    private List<@Valid AntecedentePatologicoPersonal> antecedentesPatologicosPersonales;

    private List<@Valid AntecedentePatologicoFamiliar> antecedentesPatologicosFamiliares;

    private String usuarioModificacion;
}
