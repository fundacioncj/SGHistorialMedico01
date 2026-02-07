package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import com.ug.ec.domain.consultaexterna.enums.TipoConsulta;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class DatosConsulta {

//    @NotBlank(message = "El número de consulta es obligatorio")
    private String numeroConsulta;

//    @NotNull(message = "La fecha de consulta es obligatoria")
//    @PastOrPresent(message = "La fecha de consulta no puede ser futura")
    private LocalDateTime fechaConsulta;

    
//    @NotBlank(message = "El médico tratante es obligatorio")
    private String medicoTratante;

    private String codigoMedico;

    private String especialidad;
    
//    @NotNull(message = "El tipo de consulta es obligatorio")
    private TipoConsulta tipoConsulta;
    
//    @NotBlank(message = "El motivo de consulta es obligatorio")
    private String motivoConsulta;
    

    
    // Métodos de dominio
    public String generarNumeroConsulta() {
        return "CE-" + Instant.now().toEpochMilli();
    }
    
    public boolean esConsultaDeEmergencia() {
        return this.motivoConsulta.toLowerCase().contains("emergencia") ||
               this.motivoConsulta.toLowerCase().contains("urgencia");
    }
}