package com.ug.ec.domain.consultaexterna.enums;

import java.time.Duration;

public enum PrioridadInterconsulta {
    URGENTE(1, "Urgente", Duration.ofHours(2)),
    ALTA(2, "Alta", Duration.ofHours(24)),
    MEDIA(3, "Media", Duration.ofDays(3)),
    BAJA(4, "Baja", Duration.ofDays(7));
    
    private final int nivel;
    private final String descripcion;
    private final Duration tiempoLimite;
    
    PrioridadInterconsulta(int nivel, String descripcion, Duration tiempoLimite) {
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.tiempoLimite = tiempoLimite;
    }
    
    public int getNivel() { return nivel; }
    public String getDescripcion() { return descripcion; }
    public Duration getTiempoLimite() { return tiempoLimite; }
    
    public boolean esMasPrioritaria(PrioridadInterconsulta otra) {
        return this.nivel < otra.nivel;
    }
    
    public boolean esUrgente() {
        return this == URGENTE;
    }
}