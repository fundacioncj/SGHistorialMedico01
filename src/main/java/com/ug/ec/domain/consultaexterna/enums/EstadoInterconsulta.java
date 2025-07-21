package com.ug.ec.domain.consultaexterna.enums;

public enum EstadoInterconsulta {
    SOLICITADA("Solicitada", false, true),
    AGENDADA("Agendada", false, true),
    EN_PROCESO("En Proceso", false, true),
    COMPLETADA("Completada", true, false),
    CANCELADA("Cancelada", true, false),
    RECHAZADA("Rechazada", true, false),
    DIFERIDA("Diferida", false, true);
    
    private final String descripcion;
    private final boolean estadoFinal;
    private final boolean permiteModificacion;
    
    EstadoInterconsulta(String descripcion, boolean estadoFinal, boolean permiteModificacion) {
        this.descripcion = descripcion;
        this.estadoFinal = estadoFinal;
        this.permiteModificacion = permiteModificacion;
    }
    
    public String getDescripcion() { return descripcion; }
    public boolean esFinal() { return estadoFinal; }
    public boolean permiteModificacion() { return permiteModificacion; }
    
    public boolean puedeTransicionarA(EstadoInterconsulta nuevoEstado) {
        if (this.esFinal()) return false;
        
        return switch (this) {
            case SOLICITADA -> nuevoEstado == AGENDADA || nuevoEstado == CANCELADA || 
                              nuevoEstado == DIFERIDA || nuevoEstado == RECHAZADA;
            case AGENDADA -> nuevoEstado == EN_PROCESO || nuevoEstado == CANCELADA || 
                            nuevoEstado == DIFERIDA || nuevoEstado == RECHAZADA;
            case EN_PROCESO -> nuevoEstado == COMPLETADA || nuevoEstado == DIFERIDA || 
                              nuevoEstado == RECHAZADA;
            case DIFERIDA -> nuevoEstado == AGENDADA || nuevoEstado == CANCELADA || 
                            nuevoEstado == RECHAZADA;
            default -> false;
        };
    }
}