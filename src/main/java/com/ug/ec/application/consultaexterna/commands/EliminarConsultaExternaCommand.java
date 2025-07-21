package com.ug.ec.application.consultaexterna.commands;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import jakarta.validation.constraints.NotBlank;

@Value
@Builder(toBuilder = true)
@With
public class EliminarConsultaExternaCommand {
    
    @NotBlank(message = "El ID es obligatorio")
    String id;
    
    @NotBlank(message = "El usuario eliminador es obligatorio")
    String usuarioEliminador;
    
    @NotBlank(message = "El motivo de eliminación es obligatorio")
    String motivoEliminacion;
    
    // ========== MÉTODOS DE ACCESO ==========
    
    public String getId() {
        return id;
    }
    
    public String getUsuarioEliminador() {
        return usuarioEliminador;
    }
    
    public String getMotivoEliminacion() {
        return motivoEliminacion;
    }
    
    // ========== MÉTODOS DE VALIDACIÓN ==========
    
    /**
     * Valida que los datos del comando sean correctos
     */
    public void validar() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
        
        if (usuarioEliminador == null || usuarioEliminador.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario eliminador no puede estar vacío");
        }
        
        if (motivoEliminacion == null || motivoEliminacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de eliminación no puede estar vacío");
        }
        
        if (motivoEliminacion.length() > 500) {
            throw new IllegalArgumentException("El motivo de eliminación no puede exceder 500 caracteres");
        }
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Crea un comando de eliminación simple
     */
    public static EliminarConsultaExternaCommand crear(String id, String usuarioEliminador, String motivoEliminacion) {
        EliminarConsultaExternaCommand command = EliminarConsultaExternaCommand.builder()
                .id(id)
                .usuarioEliminador(usuarioEliminador)
                .motivoEliminacion(motivoEliminacion)
                .build();
        
        command.validar();
        return command;
    }
}