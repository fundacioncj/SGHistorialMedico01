package com.ug.ec.application.historiaclinica.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EliminarHistoriaClinicaCommand {
    private String id;
    private String usuarioEliminacion;
}