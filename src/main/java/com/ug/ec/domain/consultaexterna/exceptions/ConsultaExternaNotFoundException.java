package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaNotFoundException extends RuntimeException {
    
    public ConsultaExternaNotFoundException(String message) {
        super(message);
    }
    
    public ConsultaExternaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}