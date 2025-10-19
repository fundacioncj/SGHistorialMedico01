package com.ug.ec.application.consultaexterna.commands;

import com.ug.ec.domain.consultaexterna.valueobjects.*;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@With
@Jacksonized
public class ActualizarConsultaExternaCommand {
    
    @NotBlank(message = "El ID de la consulta es obligatorio")
    String id;
    
    @NotBlank(message = "El usuario actualizador es obligatorio")
    String usuarioActualizador;

    
    @NotNull(message = "Los datos de la consulta son obligatorios")
    @Valid
    DatosConsulta datosConsulta;
    
    @NotNull(message = "La anamnesis es obligatoria")
    @Valid
    Anamnesis anamnesis;
    
    @NotNull(message = "El examen físico es obligatorio")
    @Valid
    ExamenFisico examenFisico;
    
    @NotEmpty(message = "Debe incluir al menos un diagnóstico")
    @Size(min = 1, message = "Debe incluir al menos un diagnóstico")
    @Valid
    List<Diagnostico> diagnosticos;
    
    @NotNull(message = "El plan de tratamiento es obligatorio")
    @Valid
    PlanTratamiento planTratamiento;
    
    // ========== MÉTODOS DE ACCESO ==========
    
    public String getId() {
        return id;
    }
    
    public String getUsuarioActualizador() {
        return usuarioActualizador;
    }

    
    public DatosConsulta getDatosConsulta() {
        return datosConsulta;
    }
    
    public Anamnesis getAnamnesis() {
        return anamnesis;
    }
    
    public ExamenFisico getExamenFisico() {
        return examenFisico;
    }
    
    public List<Diagnostico> getDiagnosticos() {
        return diagnosticos;
    }
    
    public PlanTratamiento getPlanTratamiento() {
        return planTratamiento;
    }
}