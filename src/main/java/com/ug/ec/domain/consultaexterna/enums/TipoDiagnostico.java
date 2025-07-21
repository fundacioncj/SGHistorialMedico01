package com.ug.ec.domain.consultaexterna.enums;

public enum TipoDiagnostico {
    PRINCIPAL("Principal"),
    SECUNDARIO("Secundario"),
    DIFERENCIAL("Diferencial"),
    PRESUNTIVO("Presuntivo"),
    CONFIRMADO("Confirmado");
    
    private final String descripcion;
    
    TipoDiagnostico(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}