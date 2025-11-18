package com.ug.ec.application.signosvitales.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EliminarSignosVitalesCommand {
    private String id;
    private String usuarioEliminacion;
}
