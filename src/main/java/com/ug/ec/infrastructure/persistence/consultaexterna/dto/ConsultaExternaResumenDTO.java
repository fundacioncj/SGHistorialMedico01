package com.ug.ec.infrastructure.persistence.consultaexterna.dto;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para proyecciones de consultas externas que no requieren el documento completo.
 * Contiene solo los campos más utilizados para listados y búsquedas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaExternaResumenDTO {
    
    private String id;
    private String numeroConsulta;
    
    // Datos básicos del paciente
    private String cedulaPaciente;
    private String nombrePaciente;
    private String numeroHistoriaClinica;
    
    // Datos básicos de la consulta
    private LocalDateTime fechaConsulta;
    private String medicoTratante;
    private String especialidad;
    private TipoConsulta tipoConsulta;
    
    // Estado
    private EstadoConsulta estado;
    
    // Diagnóstico principal (si existe)
    private String diagnosticoPrincipal;
    
    // Metadata
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}