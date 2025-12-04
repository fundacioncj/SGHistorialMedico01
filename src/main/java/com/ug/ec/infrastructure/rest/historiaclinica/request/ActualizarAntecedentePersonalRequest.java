package com.ug.ec.infrastructure.rest.historiaclinica.request;

import com.ug.ec.domain.historiaclinica.enums.TipoAntecedente;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarAntecedentePersonalRequest {

    @NotNull(message = "El tipo de antecedente original es obligatorio")
    private TipoAntecedente tipoAntecedenteOriginal;

    @NotNull(message = "El antecedente actualizado es obligatorio")
    @Valid
    private AntecedentePatologicoPersonal antecedenteActualizado;

    private String usuarioModificacion;
}