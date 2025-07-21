package com.ug.ec.domain.consultaexterna.enums;

public enum TipoConsulta {
    PRIMERA_VEZ("Primera Vez"),
    SUBSECUENTE("Subsecuente"),
    CONTROL("Control"),
    EMERGENCIA("Emergencia");
    
    private final String descripcion;
    
    TipoConsulta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}