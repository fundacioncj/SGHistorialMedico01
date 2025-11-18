package com.ug.ec.infrastructure.rest.historiaclinica.request;

import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
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
public class AgregarAntecedenteFamiliarRequest {

    @NotNull(message = "El antecedente es obligatorio")
    @Valid
    private AntecedentePatologicoFamiliar antecedente;

    private String usuarioModificacion;
}