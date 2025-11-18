package com.ug.ec.infrastructure.persistence.historiaClinica;

import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("historias_clinicas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinicaDocument {
    @Id
    private String id;
    private String idPaciente;
    private String cedulaPaciente;

    private DatosFormulario datosFormulario;

    private AntecedentePatologicoPersonal antecedentesPatologicosPersonales;
    private AntecedentePatologicoFamiliar antecedentesPatologicosFamiliares;



    private LocalDateTime fechaUltimaActualizacion;
    private DatosAuditoria auditoria;
}