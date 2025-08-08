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
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@With
@Jacksonized
public class CrearConsultaExternaCommand {
    
    @NotBlank(message = "El usuario creador es obligatorio")
    String usuarioCreador;
    
    @NotNull(message = "Los datos del formulario son obligatorios")
    @Valid
    DatosFormulario datosFormulario;
    
    @NotNull(message = "Los datos del paciente son obligatorios")
    @Valid
    DatosPaciente datosPaciente;
    
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
    
    public String getUsuarioCreador() {
        return usuarioCreador;
    }
    
    public DatosFormulario getDatosFormulario() {
        return datosFormulario;
    }
    
    public DatosPaciente getDatosPaciente() {
        return datosPaciente;
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