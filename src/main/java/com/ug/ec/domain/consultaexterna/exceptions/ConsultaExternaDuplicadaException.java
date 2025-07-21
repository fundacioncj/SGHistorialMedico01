package com.ug.ec.domain.consultaexterna.exceptions;

public class ConsultaExternaDuplicadaException extends RuntimeException {
    public ConsultaExternaDuplicadaException(String message) {
        super(message);
    }
    
    public ConsultaExternaDuplicadaException(String message, Throwable cause) {
        super(message, cause);
    }
}