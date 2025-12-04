package com.ug.ec.application.historiaclinica.commands;

import com.ug.ec.domain.historiaclinica.enums.TipoAntecedente;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarAntecedentePersonalCommand {
    private String historiaClinicaId;
    private TipoAntecedente tipoAntecedenteOriginal; // Para identificar cuál actualizar
    private AntecedentePatologicoPersonal antecedenteActualizado;
    private String usuarioModificacion;
}