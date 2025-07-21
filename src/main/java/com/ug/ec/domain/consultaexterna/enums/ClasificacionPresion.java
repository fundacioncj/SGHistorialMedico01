package com.ug.ec.domain.consultaexterna.enums;

import java.math.BigDecimal;

public enum ClasificacionPresion {
    HIPOTENSION("Hipotensión", new BigDecimal("0"), new BigDecimal("90"), new BigDecimal("0"), new BigDecimal("60")),
    NORMAL("Normal", new BigDecimal("90"), new BigDecimal("120"), new BigDecimal("60"), new BigDecimal("80")),
    ELEVADA("Elevada", new BigDecimal("120"), new BigDecimal("129"), new BigDecimal("60"), new BigDecimal("80")),
    HIPERTENSION_GRADO_1("Hipertensión Grado 1", new BigDecimal("130"), new BigDecimal("139"), new BigDecimal("80"), new BigDecimal("89")),
    HIPERTENSION_GRADO_2("Hipertensión Grado 2", new BigDecimal("140"), new BigDecimal("180"), new BigDecimal("90"), new BigDecimal("120")),
    CRISIS_HIPERTENSIVA("Crisis Hipertensiva", new BigDecimal("180"), new BigDecimal("300"), new BigDecimal("120"), new BigDecimal("200"));
    
    private final String descripcion;
    private final BigDecimal sistolicaMin;
    private final BigDecimal sistolicaMax;
    private final BigDecimal diastolicaMin;
    private final BigDecimal diastolicaMax;
    
    ClasificacionPresion(String descripcion, BigDecimal sistolicaMin, BigDecimal sistolicaMax, 
                        BigDecimal diastolicaMin, BigDecimal diastolicaMax) {
        this.descripcion = descripcion;
        this.sistolicaMin = sistolicaMin;
        this.sistolicaMax = sistolicaMax;
        this.diastolicaMin = diastolicaMin;
        this.diastolicaMax = diastolicaMax;
    }
    
    public String getDescripcion() { return descripcion; }
    
    public static ClasificacionPresion clasificar(BigDecimal sistolica, BigDecimal diastolica) {
        for (ClasificacionPresion clasificacion : values()) {
            if (sistolica.compareTo(clasificacion.sistolicaMin) >= 0 && 
                sistolica.compareTo(clasificacion.sistolicaMax) < 0 &&
                diastolica.compareTo(clasificacion.diastolicaMin) >= 0 && 
                diastolica.compareTo(clasificacion.diastolicaMax) < 0) {
                return clasificacion;
            }
        }
        return NORMAL;
    }
    
    public boolean esRiesgoAlto() {
        return this == HIPERTENSION_GRADO_2 || this == CRISIS_HIPERTENSIVA;
    }
}
