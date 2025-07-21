package com.ug.ec.domain.consultaexterna.enums;

public enum CategoriaPeso {
    BAJO_PESO("Bajo peso", 0.0, 18.4),
    NORMAL("Normal", 18.5, 24.9),
    SOBREPESO("Sobrepeso", 25.0, 29.9),
    OBESIDAD_GRADO_I("Obesidad Grado I", 30.0, 34.9),
    OBESIDAD_GRADO_II("Obesidad Grado II", 35.0, 39.9),
    OBESIDAD_GRADO_III("Obesidad Grado III", 40.0, Double.MAX_VALUE);
    
    private final String descripcion;
    private final double imcMinimo;
    private final double imcMaximo;
    
    CategoriaPeso(String descripcion, double imcMinimo, double imcMaximo) {
        this.descripcion = descripcion;
        this.imcMinimo = imcMinimo;
        this.imcMaximo = imcMaximo;
    }
    
    public String getDescripcion() { return descripcion; }
    public double getImcMinimo() { return imcMinimo; }
    public double getImcMaximo() { return imcMaximo; }
    
    public static CategoriaPeso clasificarPorIMC(double imc) {
        for (CategoriaPeso categoria : values()) {
            if (imc >= categoria.imcMinimo && imc <= categoria.imcMaximo) {
                return categoria;
            }
        }
        return NORMAL; // valor por defecto
    }
    
    public boolean esRiesgoAlto() {
        return this == OBESIDAD_GRADO_II || this == OBESIDAD_GRADO_III || this == BAJO_PESO;
    }
}
