package com.ug.ec.application.historiaclinica.commands;

import com.ug.ec.domain.historiaclinica.enums.TipoAntecedente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EliminarAntecedentePersonalCommand {
    private String historiaClinicaId;
    private TipoAntecedente tipoAntecedente;
    private String usuarioModificacion;
}