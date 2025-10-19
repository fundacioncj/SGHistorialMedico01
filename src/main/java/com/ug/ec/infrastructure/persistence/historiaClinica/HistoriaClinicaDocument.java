package com.ug.ec.infrastructure.persistence.historiaClinica;

import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.consultaexterna.valueobjects.DatosFormulario;
import com.ug.ec.domain.consultaexterna.valueobjects.HabitosPersonales;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("historias_clinicas")
public class HistoriaClinicaDocument {
    @Id
    private String id;
    private String idPaciente;
    private String cedulaPaciente;

    private DatosFormulario datosFormulario;


    private String antecedentesPatologicosPersonales;
    private String antecedentesPatologicosFamiliares;
    private String antecedentesQuirurgicos;
    private String antecedentesGinecoObstetricos;

    private HabitosPersonales habitos;
    private List<String> alergias;
    private List<String> medicamentosCronicos;


    private LocalDateTime fechaUltimaActualizacion;
    private DatosAuditoria auditoria;
}