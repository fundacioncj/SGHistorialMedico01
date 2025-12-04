package com.ug.ec.application.historiaclinica.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EliminarAntecedenteFamiliarCommand {
    private String historiaClinicaId;
    private Integer indice; // Índice del antecedente a eliminar
    private String usuarioModificacion;
}
