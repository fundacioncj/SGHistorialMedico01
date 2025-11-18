package com.ug.ec.application.historiaclinica.commands;

import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarHistoriaClinicaCommand {

    private String id;
    private String idPaciente;
    private String cedulaPaciente;
    private DatosFormulario datosFormulario;
    private List<AntecedentePatologicoPersonal> antecedentesPatologicosPersonales;
    private List<AntecedentePatologicoFamiliar> antecedentesPatologicosFamiliares;
    private String usuarioModificacion;
}
