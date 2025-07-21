package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaActualizacionException extends RuntimeException {
    
    public ConsultaExternaActualizacionException(String message) {
        super(message);
    }
    
    public ConsultaExternaActualizacionException(String message, Throwable cause) {
        super(message, cause);
    }
}