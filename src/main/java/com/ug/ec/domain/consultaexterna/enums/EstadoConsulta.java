package com.ug.ec.domain.consultaexterna.enums;

public enum EstadoConsulta {
    INICIADA("Iniciada"),
    EN_PROCESO("En Proceso"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada"),
    SUSPENDIDA("Suspendida");
    
    private final String descripcion;
    
    EstadoConsulta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}