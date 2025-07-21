package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.valueobjects.*;
import com.ug.ec.domain.consultaexterna.enums.CategoriaPeso;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidacionesClinicasService {
    
    public List<String> validarSignosVitalesPorEdad(SignosVitales signos, LocalDate fechaNacimiento) {
        List<String> alertas = new ArrayList<>();
        
        if (fechaNacimiento == null) {
            alertas.add("Fecha de nacimiento requerida para validación de signos vitales");
            return alertas;
        }
        
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        
        // Validaciones por grupo etario
        if (edad < 1) {
            alertas.addAll(validarSignosVitalesLactante(signos));
        } else if (edad < 12) {
            alertas.addAll(validarSignosVitalesPediatrico(signos, edad));
        } else if (edad < 65) {
            alertas.addAll(validarSignosVitalesAdulto(signos));
        } else {
            alertas.addAll(validarSignosVitalesAdultoMayor(signos));
        }
        
        return alertas;
    }
    
    private List<String> validarSignosVitalesLactante(SignosVitales signos) {
        List<String> alertas = new ArrayList<>();
        
        if (signos.getFrecuenciaCardiaca() != null) {
            if (signos.getFrecuenciaCardiaca().compareTo(new BigDecimal("100")) < 0 ||
                signos.getFrecuenciaCardiaca().compareTo(new BigDecimal("160")) > 0) {
                alertas.add("Frecuencia cardíaca fuera del rango normal para lactante (100-160 lpm)");
            }
        }
        
        if (signos.getFrecuenciaRespiratoria() != null) {
            if (signos.getFrecuenciaRespiratoria().compareTo(new BigDecimal("30")) < 0 ||
                signos.getFrecuenciaRespiratoria().compareTo(new BigDecimal("60")) > 0) {
                alertas.add("Frecuencia respiratoria fuera del rango normal para lactante (30-60 rpm)");
            }
        }
        
        return alertas;
    }
    
    private List<String> validarSignosVitalesPediatrico(SignosVitales signos, int edad) {
        List<String> alertas = new ArrayList<>();
        
        // Rangos aproximados para niños 1-12 años
        BigDecimal fcMin = new BigDecimal(90 - (edad * 2));
        BigDecimal fcMax = new BigDecimal(120 - edad);
        
        if (signos.getFrecuenciaCardiaca() != null) {
            if (signos.getFrecuenciaCardiaca().compareTo(fcMin) < 0 ||
                signos.getFrecuenciaCardiaca().compareTo(fcMax) > 0) {
                alertas.add(String.format("Frecuencia cardíaca fuera del rango esperado para edad %d años", edad));
            }
        }
        
        return alertas;
    }
    
    private List<String> validarSignosVitalesAdulto(SignosVitales signos) {
        List<String> alertas = new ArrayList<>();
        
        if (signos.tieneHipertension()) {
            alertas.add("Hipertensión arterial detectada");
        }
        if (signos.tieneHipotension()) {
            alertas.add("Hipotensión arterial detectada");
        }
        if (signos.tieneFiebre()) {
            alertas.add("Fiebre detectada");
        }
        if (signos.tieneHipotermia()) {
            alertas.add("Hipotermia detectada");
        }
        if (signos.tieneTaquicardia()) {
            alertas.add("Taquicardia detectada");
        }
        if (signos.tieneBradicardia()) {
            alertas.add("Bradicardia detectada");
        }
        if (signos.tieneHipoxemia()) {
            alertas.add("Hipoxemia detectada - Requiere atención inmediata");
        }
        
        return alertas;
    }
    
    private List<String> validarSignosVitalesAdultoMayor(SignosVitales signos) {
        List<String> alertas = new ArrayList<>();
        
        // Los adultos mayores pueden tener rangos ligeramente diferentes
        if (signos.getPresionSistolica() != null && 
            signos.getPresionSistolica().compareTo(new BigDecimal("150")) > 0) {
            alertas.add("Hipertensión en adulto mayor - Monitoreo especial requerido");
        }
        
        if (signos.getFrecuenciaCardiaca() != null && 
            signos.getFrecuenciaCardiaca().compareTo(new BigDecimal("50")) < 0) {
            alertas.add("Bradicardia severa en adulto mayor - Evaluar medicación");
        }
        
        return alertas;
    }
    
    public List<String> evaluarRiesgoCardiovascular(MedidasAntropometricas medidas, 
                                                   HabitosPersonales habitos, 
                                                   boolean esHombre) {
        List<String> riesgos = new ArrayList<>();
        
        // Evaluación antropométrica
        CategoriaPeso categoria = medidas.calcularCategoriaPeso();
        if (categoria.esRiesgoAlto()) {
            riesgos.add("Riesgo cardiovascular por categoría de peso: " + categoria.getDescripcion());
        }
        
        if (medidas.tieneObesidadAbdominal(esHombre)) {
            riesgos.add("Obesidad abdominal - Factor de riesgo cardiovascular");
        }
        
        // Evaluación de hábitos
        if (habitos.tieneHabitosNocivos()) {
            if (Boolean.TRUE.equals(habitos.getTabaquismo())) {
                riesgos.add("Tabaquismo - Factor de riesgo cardiovascular mayor");
            }
            if (Boolean.TRUE.equals(habitos.getAlcoholismo())) {
                riesgos.add("Alcoholismo - Factor de riesgo cardiovascular");
            }
        }
        
        if (Boolean.TRUE.equals(habitos.getSedentarismo())) {
            riesgos.add("Sedentarismo - Factor de riesgo cardiovascular");
        }
        
        // Evaluación global
        int factoresRiesgo = riesgos.size();
        if (factoresRiesgo >= 3) {
            riesgos.add("ALTO RIESGO CARDIOVASCULAR - Requiere intervención inmediata");
        } else if (factoresRiesgo >= 2) {
            riesgos.add("Riesgo cardiovascular moderado - Se recomienda seguimiento");
        }
        
        return riesgos;
    }
    
    public boolean requiereAtencionUrgente(SignosVitales signos) {
        return signos.tieneHipoxemia() || 
               (signos.getPresionSistolica() != null && 
                signos.getPresionSistolica().compareTo(new BigDecimal("180")) > 0) ||
               (signos.getTemperatura() != null && 
                signos.getTemperatura().compareTo(new BigDecimal("40")) > 0) ||
               (signos.getFrecuenciaCardiaca() != null && 
                (signos.getFrecuenciaCardiaca().compareTo(new BigDecimal("40")) < 0 || 
                 signos.getFrecuenciaCardiaca().compareTo(new BigDecimal("150")) > 0));
    }
}
