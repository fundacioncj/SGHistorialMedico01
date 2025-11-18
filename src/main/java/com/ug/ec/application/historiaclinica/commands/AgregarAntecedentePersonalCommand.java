package com.ug.ec.application.historiaclinica.commands;

import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarAntecedentePersonalCommand {
    private String historiaClinicaId;
    private AntecedentePatologicoPersonal antecedente;
    private String usuarioModificacion;
}