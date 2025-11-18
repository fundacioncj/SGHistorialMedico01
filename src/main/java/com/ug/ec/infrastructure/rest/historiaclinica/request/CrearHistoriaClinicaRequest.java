package com.ug.ec.infrastructure.rest.historiaclinica.request;


import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearHistoriaClinicaRequest {

    @NotBlank(message = "El ID del paciente es obligatorio")
    private String idPaciente;

    @NotBlank(message = "La cédula del paciente es obligatoria")
    private String cedulaPaciente;

    @NotNull(message = "Los datos del formulario son obligatorios")
    @Valid
    private DatosFormulario datosFormulario;

    private List<@Valid AntecedentePatologicoPersonal> antecedentesPatologicosPersonales;

    private List<@Valid AntecedentePatologicoFamiliar> antecedentesPatologicosFamiliares;

    @NotBlank(message = "El usuario de creación es obligatorio")
    private String usuarioCreacion;
}
