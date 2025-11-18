package com.ug.ec.domain.historiaclinica.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.Builder;
import lombok.With;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@With
@Jacksonized
public class DatosFormulario {

    @NotBlank(message = "El número de formulario es obligatorio")
    String numeroFormulario;

    @NotBlank(message = "El establecimiento es obligatorio")
    String establecimiento;

    @NotBlank(message = "El código de establecimiento es obligatorio")
    @Pattern(regexp = "^[0-9]{3,6}$", message = "El código debe tener entre 3 y 6 dígitos")
    String codigoEstablecimiento;

    String areaSalud;
    String distrito;
    String canton;
    String provincia;

    @JsonCreator
    public DatosFormulario(
            @JsonProperty("numeroFormulario") String numeroFormulario,
            @JsonProperty("establecimiento") String establecimiento,
            @JsonProperty("codigoEstablecimiento") String codigoEstablecimiento,
            @JsonProperty("areaSalud") String areaSalud,
            @JsonProperty("distrito") String distrito,
            @JsonProperty("canton") String canton,
            @JsonProperty("provincia") String provincia) {
        this.numeroFormulario = numeroFormulario;
        this.establecimiento = establecimiento;
        this.codigoEstablecimiento = codigoEstablecimiento;
        this.areaSalud = areaSalud;
        this.distrito = distrito;
        this.canton = canton;
        this.provincia = provincia;
    }

    public void validar() {
        if (numeroFormulario == null || numeroFormulario.isBlank()) {
            throw new IllegalArgumentException("El número de formulario es obligatorio");
        }
        if (establecimiento == null || establecimiento.isBlank()) {
            throw new IllegalArgumentException("El establecimiento es obligatorio");
        }
        if (codigoEstablecimiento == null || codigoEstablecimiento.isBlank()) {
            throw new IllegalArgumentException("El código de establecimiento es obligatorio");
        }
        if (!codigoEstablecimiento.matches("^[0-9]{3,6}$")) {
            throw new IllegalArgumentException("El código debe tener entre 3 y 6 dígitos");
        }
    }
}