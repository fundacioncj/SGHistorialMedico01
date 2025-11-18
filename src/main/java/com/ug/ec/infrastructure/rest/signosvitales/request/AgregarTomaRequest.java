package com.ug.ec.infrastructure.rest.signosvitales.request;

import com.ug.ec.domain.signosvitales.valueobjects.TomaSignos;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarTomaRequest {

    @NotNull(message = "Los datos de la toma son obligatorios")
    private TomaSignos toma;
}
