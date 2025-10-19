package com.ug.ec.application.consultaexterna.commands;

import com.ug.ec.domain.consultaexterna.valueobjects.SignosVitales;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearTriajeCommand {

    @NotNull(message = "Es requerida la Hstoria Clinica")
    @Valid
    String historiaClinicaId;

    @NotNull(message = "La cedual del paciente es requerida")
    @Valid
    String cedulaPaciente;

    @NotNull
    private SignosVitales signosVitales;

    private String usuarioCreador;
}
