package com.ug.ec.application.signosvitales.commands;

import com.ug.ec.domain.signosvitales.valueobjects.TomaSignos;
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
public class CrearSignosVitalesCommand {
    private String citaId;
    private String pacienteId;
    private String usuarioRegistro;
    private LocalDateTime fechaRegistro;
    private List<TomaSignos> tomas;
}
