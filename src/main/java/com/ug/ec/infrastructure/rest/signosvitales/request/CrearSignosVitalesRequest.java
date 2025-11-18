package com.ug.ec.infrastructure.rest.signosvitales.request;

import com.ug.ec.domain.signosvitales.valueobjects.TomaSignos;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearSignosVitalesRequest {

    @NotBlank(message = "El ID de la cita es obligatorio")
    private String citaId;

    @NotBlank(message = "El ID del paciente es obligatorio")
    private String pacienteId;

    @NotBlank(message = "El usuario de registro es obligatorio")
    private String usuarioRegistro;

    private LocalDateTime fechaRegistro;

    private List<TomaSignos> tomas;
}