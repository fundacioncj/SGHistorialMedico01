package com.ug.ec.domain.consultaexterna.valueobjects;

import com.ug.ec.domain.consultaexterna.enums.EstadoInterconsulta;
import com.ug.ec.domain.consultaexterna.enums.PrioridadInterconsulta;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Interconsulta {
    
    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;
    
    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
    
    private String observaciones;
    
    private String hallazgosRelevantes;
    
    @NotNull(message = "La fecha de solicitud es obligatoria")
    private LocalDateTime fechaSolicitud;
    
    private LocalDateTime fechaProgramada;
    
    @NotNull(message = "El estado es obligatorio")
    @Builder.Default
    private EstadoInterconsulta estado = EstadoInterconsulta.SOLICITADA;
    
    @NotNull(message = "La prioridad es obligatoria")
    @Builder.Default
    private PrioridadInterconsulta prioridad = PrioridadInterconsulta.MEDIA;
    
    private String medicoSolicitante;
    private String medicoEspecialista;
    
    // Nuevos campos
    private String codigoDiagnosticoRelacionado;
    private String descripcionDiagnosticoRelacionado;
    private String resultadosEsperados;
    private String preguntaEspecifica;
    private List<String> examenesRealizados;
    private String resultadosExamenes;
    private LocalDateTime fechaLimiteAtencion;
    private String notasAdicionales;
    private String documentosAdjuntos;
    
    // Métodos de dominio
    public boolean esPendiente() {
        return EstadoInterconsulta.SOLICITADA.equals(estado);
    }
    
    public boolean esUrgente() {
        return PrioridadInterconsulta.URGENTE.equals(prioridad);
    }
    
    public Interconsulta programar(LocalDateTime fechaProgramada) {
        return this.toBuilder()
                .fechaProgramada(fechaProgramada)
                .estado(EstadoInterconsulta.AGENDADA)
                .build();
    }
    
    public Interconsulta completar(String medicoEspecialista) {
        return this.toBuilder()
                .medicoEspecialista(medicoEspecialista)
                .estado(EstadoInterconsulta.COMPLETADA)
                .build();
    }
    
    // Nuevos métodos de dominio
    public boolean requiereAtencionInmediata() {
        return PrioridadInterconsulta.URGENTE.equals(prioridad) && 
               (fechaLimiteAtencion == null || 
                fechaLimiteAtencion.isBefore(LocalDateTime.now().plusDays(1)));
    }
    
    public boolean estaVencida() {
        return EstadoInterconsulta.SOLICITADA.equals(estado) && 
               fechaLimiteAtencion != null && 
               fechaLimiteAtencion.isBefore(LocalDateTime.now());
    }
    
    public Interconsulta rechazar(String motivo) {
        return this.toBuilder()
                .estado(EstadoInterconsulta.RECHAZADA)
                .observaciones(motivo)
                .build();
    }
}