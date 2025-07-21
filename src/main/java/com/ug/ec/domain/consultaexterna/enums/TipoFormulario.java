package com.ug.ec.domain.consultaexterna.enums;

public enum TipoFormulario {
    CONSULTA_EXTERNA("002", "Consulta Externa"),
    HOSPITALIZACION("003", "Hospitalización"),
    EMERGENCIA("008", "Emergencia");
    
    private final String codigo;
    private final String descripcion;
    
    TipoFormulario(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}