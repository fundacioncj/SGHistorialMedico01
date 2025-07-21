package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosConsulta {
    
    @NotBlank(message = "El número de consulta es obligatorio")
    private String numeroConsulta;
    
    @NotNull(message = "La fecha de consulta es obligatoria")
    @PastOrPresent(message = "La fecha de consulta no puede ser futura")
    private LocalDateTime fechaConsulta;
    
    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;
    
    @NotBlank(message = "El médico tratante es obligatorio")
    private String medicoTratante;
    
    private String codigoMedico;
    
    @NotNull(message = "El tipo de consulta es obligatorio")
    private TipoConsulta tipoConsulta;
    
    @NotBlank(message = "El motivo de consulta es obligatorio")
    private String motivoConsulta;
    
    private String observaciones;
    
    // Métodos de dominio
    public String generarNumeroConsulta() {
        return "CE-" + Instant.now().toEpochMilli();
    }
    
    public boolean esConsultaDeEmergencia() {
        return this.motivoConsulta.toLowerCase().contains("emergencia") ||
               this.motivoConsulta.toLowerCase().contains("urgencia");
    }
}