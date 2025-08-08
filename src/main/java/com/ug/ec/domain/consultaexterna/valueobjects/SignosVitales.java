package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class SignosVitales {
    
    @NotNull(message = "La presión arterial es obligatoria")
    private String presionArterial; // Formato "120/80"
    
    @NotNull(message = "La frecuencia cardíaca es obligatoria")
    @DecimalMin(value = "30", message = "La frecuencia cardíaca debe ser mayor a 30")
    @DecimalMax(value = "200", message = "La frecuencia cardíaca debe ser menor a 200")
    private BigDecimal frecuenciaCardiaca;
    
    @NotNull(message = "La frecuencia respiratoria es obligatoria")
    @DecimalMin(value = "8", message = "La frecuencia respiratoria debe ser mayor a 8")
    @DecimalMax(value = "40", message = "La frecuencia respiratoria debe ser menor a 40")
    private BigDecimal frecuenciaRespiratoria;
    
    @NotNull(message = "La temperatura es obligatoria")
    @DecimalMin(value = "30.0", message = "La temperatura debe ser mayor a 30°C")
    @DecimalMax(value = "45.0", message = "La temperatura debe ser menor a 45°C")
    private BigDecimal temperatura;
    
    @DecimalMin(value = "70", message = "La saturación de oxígeno debe ser mayor a 70%")
    @DecimalMax(value = "100", message = "La saturación de oxígeno debe ser menor o igual a 100%")
    private BigDecimal saturacionOxigeno;

    // Métodos de conversión para compatibilidad
    public Integer getFrecuenciaCardiacaAsInt() {
        return frecuenciaCardiaca != null ? frecuenciaCardiaca.intValue() : null;
    }

    public Integer getFrecuenciaRespiratoriaAsInt() {
        return frecuenciaRespiratoria != null ? frecuenciaRespiratoria.intValue() : null;
    }

    public Double getTemperaturaAsDouble() {
        return temperatura != null ? temperatura.doubleValue() : null;
    }

    public Integer getSaturacionOxigenoAsInt() {
        return saturacionOxigeno != null ? saturacionOxigeno.intValue() : null;
    }

    // Métodos de clasificación y validación
    public String clasificarPresion() {
        if (presionArterial == null) return "NO_REGISTRADA";
        
        String[] valores = presionArterial.split("/");
        if (valores.length != 2) return "FORMATO_INVALIDO";
        
        try {
            int sistolica = Integer.parseInt(valores[0].trim());
            int diastolica = Integer.parseInt(valores[1].trim());
            
            if (sistolica < 90 || diastolica < 60) return "HIPOTENSION";
            if (sistolica < 120 && diastolica < 80) return "NORMAL";
            if (sistolica < 130 && diastolica < 80) return "ELEVADA";
            if (sistolica < 140 || diastolica < 90) return "HIPERTENSION_GRADO_1";
            if (sistolica < 180 || diastolica < 120) return "HIPERTENSION_GRADO_2";
            return "CRISIS_HIPERTENSIVA";
        } catch (NumberFormatException e) {
            return "FORMATO_INVALIDO";
        }
    }

    public boolean tieneHipoxemiaGrave() {
        return saturacionOxigeno != null && saturacionOxigeno.compareTo(new BigDecimal("90")) < 0;
    }

    public boolean tieneHipotensionSevera() {
        if (presionArterial == null) return false;
        String[] valores = presionArterial.split("/");
        if (valores.length != 2) return false;
        
        try {
            int sistolica = Integer.parseInt(valores[0].trim());
            return sistolica < 90;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean tieneHipotermia() {
        return temperatura != null && temperatura.compareTo(new BigDecimal("35.0")) < 0;
    }

    public boolean estanEnRangoNormal() {
        return "NORMAL".equals(clasificarPresion()) &&
               frecuenciaCardiaca != null && frecuenciaCardiaca.intValue() >= 60 && frecuenciaCardiaca.intValue() <= 100 &&
               frecuenciaRespiratoria != null && frecuenciaRespiratoria.intValue() >= 12 && frecuenciaRespiratoria.intValue() <= 20 &&
               temperatura != null && temperatura.doubleValue() >= 36.0 && temperatura.doubleValue() <= 37.5 &&
               (saturacionOxigeno == null || saturacionOxigeno.intValue() >= 95);
    }

    public boolean requiereAtencionUrgente() {
        return "CRISIS_HIPERTENSIVA".equals(clasificarPresion()) ||
               tieneHipoxemiaGrave() ||
               tieneHipotensionSevera() ||
               tieneHipotermia() ||
               (temperatura != null && temperatura.compareTo(new BigDecimal("40.0")) > 0);
    }
    
    // Métodos de validación específicos para signos vitales
    public boolean tieneHipertension() {
        String clasificacion = clasificarPresion();
        return "HIPERTENSION_GRADO_1".equals(clasificacion) || 
               "HIPERTENSION_GRADO_2".equals(clasificacion) || 
               "CRISIS_HIPERTENSIVA".equals(clasificacion);
    }
    
    public boolean tieneHipotension() {
        return "HIPOTENSION".equals(clasificarPresion());
    }
    
    public boolean tieneFiebre() {
        return temperatura != null && temperatura.compareTo(new BigDecimal("37.5")) > 0;
    }
    
    public boolean tieneTaquicardia() {
        return frecuenciaCardiaca != null && frecuenciaCardiaca.intValue() > 100;
    }
    
    public boolean tieneBradicardia() {
        return frecuenciaCardiaca != null && frecuenciaCardiaca.intValue() < 60;
    }
    
    public boolean tieneHipoxemia() {
        return saturacionOxigeno != null && saturacionOxigeno.compareTo(new BigDecimal("95")) < 0;
    }
    
    /**
     * Obtiene la presión sistólica (el primer número antes del "/")
     * @return La presión sistólica como BigDecimal, o null si no se puede extraer
     */
    public BigDecimal getPresionSistolica() {
        if (presionArterial == null) return null;
        
        try {
            String[] valores = presionArterial.split("/");
            if (valores.length < 1) return null;
            
            return new BigDecimal(valores[0].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Obtiene la presión diastólica (el segundo número después del "/")
     * @return La presión diastólica como BigDecimal, o null si no se puede extraer
     */
    public BigDecimal getPresionDiastolica() {
        if (presionArterial == null) return null;
        
        try {
            String[] valores = presionArterial.split("/");
            if (valores.length < 2) return null;
            
            return new BigDecimal(valores[1].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<String> obtenerAlertas() {
        List<String> alertas = new ArrayList<>();
        
        String clasificacionPresion = clasificarPresion();
        if ("CRISIS_HIPERTENSIVA".equals(clasificacionPresion)) {
            alertas.add("CRISIS HIPERTENSIVA - ATENCIÓN URGENTE");
        } else if ("HIPOTENSION".equals(clasificacionPresion)) {
            alertas.add("HIPOTENSIÓN DETECTADA");
        }
        
        if (tieneHipoxemiaGrave()) {
            alertas.add("HIPOXEMIA GRAVE - OXÍGENO REQUERIDO");
        }
        
        if (tieneHipotermia()) {
            alertas.add("HIPOTERMIA DETECTADA");
        } else if (temperatura != null && temperatura.compareTo(new BigDecimal("40.0")) > 0) {
            alertas.add("FIEBRE ALTA - MONITOREO REQUERIDO");
        }
        
        if (frecuenciaCardiaca != null) {
            if (frecuenciaCardiaca.intValue() > 120) {
                alertas.add("TAQUICARDIA DETECTADA");
            } else if (frecuenciaCardiaca.intValue() < 50) {
                alertas.add("BRADICARDIA DETECTADA");
            }
        }
        
        return alertas;
    }
}