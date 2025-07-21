package com.ug.ec.domain.consultaexterna.enums;

public enum Sexo {
    MASCULINO("M", "Masculino"),
    FEMENINO("F", "Femenino"),
    INDEFINIDO("I", "Indefinido");
    
    private final String codigo;
    private final String descripcion;
    
    Sexo(String codigo, String descripcion) {
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