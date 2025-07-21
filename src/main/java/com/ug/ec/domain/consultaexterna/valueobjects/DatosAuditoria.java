package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@With
public class DatosAuditoria {
    
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;
    
    public static DatosAuditoria crearNuevo(String usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        return DatosAuditoria.builder()
                .creadoPor(usuario)
                .fechaCreacion(ahora)
                .modificadoPor(usuario)
                .fechaModificacion(ahora)
                .build();
    }
    
    public DatosAuditoria actualizar(String usuario) {
        return this.toBuilder()
                .modificadoPor(usuario)
                .fechaModificacion(LocalDateTime.now())
                .build();
    }
    
    // Método alternativo para compatibilidad
    public DatosAuditoria actualizarModificacion(String usuario) {
        return actualizar(usuario);
    }
}