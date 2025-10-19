package com.ug.ec.infrastructure.persistence.consultaexterna;

import com.ug.ec.domain.consultaexterna.valueobjects.*;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Documento MongoDB para la colección de consultas externas.
 * Se han añadido índices para optimizar las consultas más frecuentes.
 */
@Document(collection = "consultas-externas")
@CompoundIndexes({
    @CompoundIndex(name = "cedula_fecha_idx", def = "{'cedulaPaciente': 1, 'datosConsulta.fechaConsulta': -1}"),
    @CompoundIndex(name = "medico_fecha_idx", def = "{'datosConsulta.medicoTratante': 1, 'datosConsulta.fechaConsulta': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaExternaDocument {
    
    @Id
    private String id;

    private String historiaClinicaId;;

    @Indexed(unique = true)
    private String numeroConsulta;

    private String cedulaPaciente;
    private DatosConsulta datosConsulta;
    
    private Anamnesis anamnesis;
    
    private ExamenFisico examenFisico;
    
    private List<Diagnostico> diagnosticos;
    
    private PlanTratamiento planTratamiento;
    
    @Indexed
    private EstadoConsulta estado;
    
    private DatosAuditoria auditoria;
    
    @Indexed
    private Boolean eliminada;
    private java.time.LocalDateTime fechaEliminacion;
    private String usuarioEliminador;
    private String motivoEliminacion;
    
    private Map<String, Object> camposAdicionales;
}
