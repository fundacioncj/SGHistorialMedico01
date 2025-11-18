package com.ug.ec.infrastructure.rest.historiaclinica.response;

import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
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
public class HistoriaClinicaResponse {
    private String id;
    private String idPaciente;
    private String cedulaPaciente;
    private DatosFormulario datosFormulario;
    private List<AntecedentePatologicoPersonal> antecedentesPatologicosPersonales;
    private List<AntecedentePatologicoFamiliar> antecedentesPatologicosFamiliares;
    private LocalDateTime fechaUltimaActualizacion;
    private DatosAuditoria auditoria;
    private Boolean activo;
    private String resumen;
    private Boolean estaCompleta;
}
