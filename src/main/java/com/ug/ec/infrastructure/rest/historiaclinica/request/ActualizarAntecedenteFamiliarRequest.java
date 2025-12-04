package com.ug.ec.infrastructure.rest.historiaclinica.request;

import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarAntecedenteFamiliarRequest {

    @NotNull(message = "El índice es obligatorio")
    @Min(value = 0, message = "El índice debe ser mayor o igual a 0")
    private Integer indice;

    @NotNull(message = "El antecedente actualizado es obligatorio")
    @Valid
    private AntecedentePatologicoFamiliar antecedenteActualizado;

    private String usuarioModificacion;
}
