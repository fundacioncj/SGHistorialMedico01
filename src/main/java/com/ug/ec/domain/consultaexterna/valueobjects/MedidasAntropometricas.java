package com.ug.ec.domain.consultaexterna.valueobjects;

import com.ug.ec.domain.consultaexterna.enums.CategoriaPeso;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidasAntropometricas {
    
    @Positive(message = "El peso debe ser mayor a cero")
    @DecimalMin(value = "0.5", message = "El peso debe ser mayor a 0.5 kg")
    @DecimalMax(value = "300.0", message = "El peso debe ser menor a 300 kg")
    private BigDecimal peso; // en kg
    
    @Positive(message = "La talla debe ser mayor a cero")
    @DecimalMin(value = "30.0", message = "La talla debe ser mayor a 30 cm")
    @DecimalMax(value = "250.0", message = "La talla debe ser menor a 250 cm")
    private BigDecimal talla; // en cm
    
    private BigDecimal perimetroCefalico; // en cm
    private BigDecimal perimetroAbdominal; // en cm
    private BigDecimal perimetroToracico; // en cm
    
    // Métodos calculados
    public BigDecimal calcularIMC() {
        if (peso == null || talla == null || 
            peso.compareTo(BigDecimal.ZERO) <= 0 || 
            talla.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        
        // IMC = peso(kg) / (talla(m))²
        BigDecimal tallaEnMetros = talla.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal tallaCuadrada = tallaEnMetros.multiply(tallaEnMetros);
        
        return peso.divide(tallaCuadrada, 2, RoundingMode.HALF_UP);
    }
    
    public CategoriaPeso categorizarPeso() {
        BigDecimal imc = calcularIMC();
        if (imc == null) return null;
        
        if (imc.compareTo(new BigDecimal("18.5")) < 0) {
            return CategoriaPeso.BAJO_PESO;
        } else if (imc.compareTo(new BigDecimal("25.0")) < 0) {
            return CategoriaPeso.NORMAL;
        } else if (imc.compareTo(new BigDecimal("30.0")) < 0) {
            return CategoriaPeso.SOBREPESO;
        } else if (imc.compareTo(new BigDecimal("35.0")) < 0) {
            return CategoriaPeso.OBESIDAD_GRADO_I;
        } else if (imc.compareTo(new BigDecimal("40.0")) < 0) {
            return CategoriaPeso.OBESIDAD_GRADO_II;
        } else {
            return CategoriaPeso.OBESIDAD_GRADO_III;
        }
    }
    
    public String obtenerResumen() {
        StringBuilder resumen = new StringBuilder();
        
        if (peso != null) {
            resumen.append("Peso: ").append(peso).append(" kg");
        }
        
        if (talla != null) {
            if (resumen.length() > 0) resumen.append(", ");
            resumen.append("Talla: ").append(talla).append(" cm");
        }
        
        BigDecimal imc = calcularIMC();
        if (imc != null) {
            if (resumen.length() > 0) resumen.append(", ");
            resumen.append("IMC: ").append(imc);
            
            CategoriaPeso categoria = categorizarPeso();
            if (categoria != null) {
                resumen.append(" (").append(categoria.getDescripcion()).append(")");
            }
        }
        
        return resumen.toString();
    }
    
    public boolean sonMedidasCompletas() {
        return peso != null && talla != null;
    }
    
    /**
     * Alias para categorizarPeso() para mantener compatibilidad con el código existente
     * @return La categoría de peso según el IMC
     */
    public CategoriaPeso calcularCategoriaPeso() {
        return categorizarPeso();
    }
    
    /**
     * Determina si la persona tiene obesidad abdominal según su sexo
     * @param esHombre true si es hombre, false si es mujer
     * @return true si tiene obesidad abdominal, false en caso contrario
     */
    public boolean tieneObesidadAbdominal(boolean esHombre) {
        if (perimetroAbdominal == null) return false;
        
        // Criterios de obesidad abdominal según sexo
        // Hombres: > 102 cm, Mujeres: > 88 cm
        BigDecimal umbral = esHombre ? new BigDecimal("102") : new BigDecimal("88");
        return perimetroAbdominal.compareTo(umbral) > 0;
    }
    
    public static MedidasAntropometricas crear(double pesoKg, double tallaCm) {
        return MedidasAntropometricas.builder()
                .peso(new BigDecimal(String.valueOf(pesoKg)))
                .talla(new BigDecimal(String.valueOf(tallaCm)))
                .build();
    }
}