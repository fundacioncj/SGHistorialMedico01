package com.ug.ec.domain.consultaexterna.valueobjects;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamenFisico {
    
    @Valid
    @NotNull(message = "Los signos vitales son obligatorios")
    private SignosVitales signosVitales;
    
    private String examenGeneral;
    
    @Builder.Default
    private List<ExamenFisicoRegional> examenesRegionales = new ArrayList<>();
    
    private MedidasAntropometricas medidasAntropometricas;
    
    // Corrección del método que causaba error de compilación
    public String obtenerResumenCompleto() {
        StringBuilder resumen = new StringBuilder();
        
        if (examenGeneral != null && !examenGeneral.trim().isEmpty()) {
            resumen.append("Examen General: ").append(examenGeneral).append("\n");
        }
        
        if (signosVitales != null) {
            resumen.append("Signos Vitales - ");
            resumen.append("PA: ").append(signosVitales.getPresionArterial() != null ? signosVitales.getPresionArterial() : "No registrada");
            resumen.append(", FC: ").append(signosVitales.getFrecuenciaCardiaca() != null ? signosVitales.getFrecuenciaCardiaca().toString() : "No registrada");
            resumen.append(", FR: ").append(signosVitales.getFrecuenciaRespiratoria() != null ? signosVitales.getFrecuenciaRespiratoria().toString() : "No registrada");
            resumen.append(", T°: ").append(signosVitales.getTemperatura() != null ? signosVitales.getTemperatura().toString() : "No registrada");
            if (signosVitales.getSaturacionOxigeno() != null) {
                resumen.append(", SpO2: ").append(signosVitales.getSaturacionOxigeno().toString()).append("%");
            }
            resumen.append("\n");
        }
        
        if (examenesRegionales != null && !examenesRegionales.isEmpty()) {
            resumen.append("Exámenes Regionales:\n");
            examenesRegionales.forEach(examen -> 
                resumen.append("- ").append(examen.obtenerDescripcionFormateada()).append("\n")
            );
        }
        
        return resumen.toString();
    }
    
    public boolean esNormal() {
        boolean signosNormales = signosVitales != null && signosVitales.estanEnRangoNormal();
        boolean regionalesNormales = examenesRegionales == null || 
            examenesRegionales.stream().allMatch(ExamenFisicoRegional::isNormal);
        
        return signosNormales && regionalesNormales;
    }
    
    public List<String> obtenerHallazgosAnormales() {
        List<String> hallazgos = new ArrayList<>();
        
        if (signosVitales != null && !signosVitales.estanEnRangoNormal()) {
            hallazgos.addAll(signosVitales.obtenerAlertas());
        }
        
        if (examenesRegionales != null) {
            examenesRegionales.stream()
                .filter(ExamenFisicoRegional::tieneHallazgosAnormales)
                .forEach(examen -> hallazgos.add(examen.obtenerDescripcionFormateada()));
        }
        
        return hallazgos;
    }
}