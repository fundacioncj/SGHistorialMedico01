package com.ug.ec.application.historiaclinica.commands;

import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarDatosFormularioCommand {
    private String historiaClinicaId;
    private DatosFormulario datosFormulario;
    private String usuarioModificacion;
}