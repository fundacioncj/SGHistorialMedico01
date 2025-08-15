package com.ug.ec.application.consultaexterna.dto;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import lombok.Value;
import lombok.Builder;
import java.time.LocalDateTime;

@Value
@Builder
public class ConsultaExternaResumenDto {
    String id;
    String numeroConsulta;

    // Datos esenciales del paciente
    String cedulaPaciente;
    String nombreCompletoPaciente;

    // Datos esenciales de consulta
    LocalDateTime fechaConsulta;
    String especialidad;
    String medicoTratante;

    // Estado y auditoría
    EstadoConsulta estado;
    LocalDateTime fechaCreacion;
    String creadoPor;
}