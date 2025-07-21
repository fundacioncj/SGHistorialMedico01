package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaIncompletaException extends RuntimeException {
    public ConsultaExternaIncompletaException(String message) {
        super(message);
    }
    
    public ConsultaExternaIncompletaException(String message, Throwable cause) {
        super(message, cause);
    }
}