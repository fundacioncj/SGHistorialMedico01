package com.ug.ec.domain.signosvitales.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TomaSignos {

    private LocalDateTime fechaHoraToma;

    // Constantes vitales
    private Double temperatura;
    private String presionArterial;
    private Integer presionSistolica;
    private Integer presionDiastolica;
    private Integer pulso;
    private Integer frecuenciaRespiratoria;
    private Integer pulsioximetria;

    // Antropometría
    private Double peso;
    private Double talla;
    private Double imc;
    private Double perimetroAbdominal;

    // Pruebas rápidas
    private Double hemoglobinaCapilar;
    private Double glucosaCapilar;

    private String observaciones;

    // Validaciones
    public void validar() {
        if (fechaHoraToma == null) {
            throw new IllegalArgumentException("La fecha y hora de la toma es obligatoria");
        }

        // Validaciones de rangos normales (opcional)
        if (temperatura != null && (temperatura < 35.0 || temperatura > 42.0)) {
            throw new IllegalArgumentException("Temperatura fuera de rango válido (35-42°C)");
        }

        if (pulsioximetria != null && (pulsioximetria < 0 || pulsioximetria > 100)) {
            throw new IllegalArgumentException("Pulsioximetría debe estar entre 0 y 100%");
        }

        if (presionSistolica != null && presionDiastolica != null) {
            if (presionSistolica <= presionDiastolica) {
                throw new IllegalArgumentException("La presión sistólica debe ser mayor que la diastólica");
            }
        }
    }
}