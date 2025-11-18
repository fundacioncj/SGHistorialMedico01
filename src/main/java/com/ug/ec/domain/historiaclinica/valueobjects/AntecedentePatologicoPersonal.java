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
@JsonDeserialize(builder = AntecedentePatologicoPersonal.AntecedentePatologicoPersonalBuilder.class)
public class AntecedentePatologicoPersonal {

    @NotNull(message = "El tipo de antecedente es obligatorio")
    private TipoAntecedente tipo;

    private String descripcion;
    private boolean presente;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AntecedentePatologicoPersonalBuilder {
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
