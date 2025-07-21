package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaConsultaException extends RuntimeException {
    public ConsultaExternaConsultaException(String message) {
        super(message);
    }
    
    public ConsultaExternaConsultaException(String message, Throwable cause) {
        super(message, cause);
    }
}