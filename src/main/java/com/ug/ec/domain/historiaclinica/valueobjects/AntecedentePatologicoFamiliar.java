package com.ug.ec.domain.historiaclinica.valueobjects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.ug.ec.domain.historiaclinica.enums.TipoAntecedente;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

@JsonDeserialize(builder = AntecedentePatologicoFamiliar.AntecedentePatologicoFamiliarBuilder.class)
public class AntecedentePatologicoFamiliar {

    @NotNull(message = "El tipo de antecedente es obligatorio")
    private TipoAntecedente tipo;

    private String descripcion;
    private boolean presente;
    private String parentesco; // padre, madre, hermano, abuelo, etc.

    @JsonPOJOBuilder(withPrefix = "")
    public static class AntecedentePatologicoFamiliarBuilder {
    }

    public void validar() {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de antecedente es obligatorio");
        }
        if (presente && (descripcion == null || descripcion.isBlank())) {
            throw new IllegalArgumentException(
                    "La descripción es obligatoria cuando el antecedente está presente");
        }
    }
}
