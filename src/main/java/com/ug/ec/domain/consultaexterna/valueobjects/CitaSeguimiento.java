package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaSeguimiento {
    
    @NotNull(message = "La fecha de seguimiento es obligatoria")
    @Future(message = "La fecha de seguimiento debe ser futura")
    private LocalDateTime fechaSeguimiento;
    
    private String especialidad;
    private String motivo;
    private String observaciones;
    private String medicoAsignado;
    
    @Builder.Default
    private boolean activa = true;
    
    public boolean esPendiente() {
        return activa && fechaSeguimiento.isAfter(LocalDateTime.now());
    }
    
    public boolean esVencida() {
        return activa && fechaSeguimiento.isBefore(LocalDateTime.now());
    }
    
    /**
     * Determina si la cita es urgente (programada dentro de las próximas 72 horas)
     * @return true si la cita es urgente, false en caso contrario
     */
    public boolean esUrgente() {
        if (!activa || fechaSeguimiento == null) return false;
        
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(72); // 3 días
        
        return fechaSeguimiento.isAfter(ahora) && 
               fechaSeguimiento.isBefore(limite);
    }
    
    /**
     * Determina si la cita está programada para los próximos días
     * @return true si la cita está programada para los próximos 7 días
     */
    public boolean esCitaProxima() {
        if (!activa || fechaSeguimiento == null) return false;
        
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(7); // 7 días
        
        return fechaSeguimiento.isAfter(ahora) && 
               fechaSeguimiento.isBefore(limite);
    }
    
    public String obtenerDescripcion() {
        StringBuilder desc = new StringBuilder();
        if (especialidad != null) {
            desc.append("Seguimiento en ").append(especialidad);
        } else {
            desc.append("Seguimiento médico");
        }
        
        if (motivo != null && !motivo.trim().isEmpty()) {
            desc.append(" - ").append(motivo);
        }
        
        return desc.toString();
    }
    
    public static CitaSeguimiento programarEn(int dias, String motivo) {
        return CitaSeguimiento.builder()
                .fechaSeguimiento(LocalDateTime.now().plusDays(dias))
                .motivo(motivo)
                .activa(true)
                .build();
    }
    
    public static CitaSeguimiento programarEspecialidad(int dias, String especialidad, String motivo) {
        return CitaSeguimiento.builder()
                .fechaSeguimiento(LocalDateTime.now().plusDays(dias))
                .especialidad(especialidad)
                .motivo(motivo)
                .activa(true)
                .build();
    }
}