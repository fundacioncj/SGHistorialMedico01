package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaNoEditableException extends RuntimeException {
    public ConsultaExternaNoEditableException(String message) {
        super(message);
    }
    
    public ConsultaExternaNoEditableException(String message, Throwable cause) {
        super(message, cause);
    }
}