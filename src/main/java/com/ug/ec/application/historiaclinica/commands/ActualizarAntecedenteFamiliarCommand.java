package com.ug.ec.application.historiaclinica.commands;

import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarAntecedenteFamiliarCommand {
    private String historiaClinicaId;
    private Integer indice; // Índice del antecedente a actualizar
    private AntecedentePatologicoFamiliar antecedenteActualizado;
    private String usuarioModificacion;
}
