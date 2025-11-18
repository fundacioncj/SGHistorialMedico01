package com.ug.ec.infrastructure.rest.historiaclinica.request;

import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
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
public class ActualizarDatosFormularioRequest {

    @NotNull(message = "Los datos del formulario son obligatorios")
    @Valid
    private DatosFormulario datosFormulario;

    private String usuarioModificacion;
}