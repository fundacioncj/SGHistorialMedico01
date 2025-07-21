package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaEliminacionException extends RuntimeException {
    
    public ConsultaExternaEliminacionException(String message) {
        super(message);
    }
    
    public ConsultaExternaEliminacionException(String message, Throwable cause) {
        super(message, cause);
    }
}