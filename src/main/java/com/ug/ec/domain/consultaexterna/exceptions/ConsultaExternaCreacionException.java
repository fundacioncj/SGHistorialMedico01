package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaCreacionException extends RuntimeException {
    
    public ConsultaExternaCreacionException(String message) {
        super(message);
    }
    
    public ConsultaExternaCreacionException(String message, Throwable cause) {
        super(message, cause);
    }
}