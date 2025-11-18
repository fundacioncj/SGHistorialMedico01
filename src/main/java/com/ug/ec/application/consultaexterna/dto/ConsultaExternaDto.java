package com.ug.ec.application.consultaexterna.dto;

import com.ug.ec.domain.consultaexterna.valueobjects.*;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaExternaDto {
    private String id;
    private String numeroConsulta;
    private String historiaClinicaId;
    private String signosVitalesId;
    private String cedulaPaciente;
    private DatosConsulta datosConsulta;
    private Anamnesis anamnesis;
    private ExamenFisico examenFisico;
    private List<Diagnostico> diagnosticos;
    private PlanTratamiento planTratamiento;
    private EstadoConsulta estado;
    private DatosAuditoria auditoria;
    private Map<String, Object> camposAdicionales;
}