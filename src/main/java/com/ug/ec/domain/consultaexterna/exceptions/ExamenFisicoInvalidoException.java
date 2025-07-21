package com.ug.ec.domain.consultaexterna.exceptions;

public class ExamenFisicoInvalidoException extends RuntimeException {
    public ExamenFisicoInvalidoException(String message) {
        super(message);
    }
    
    public ExamenFisicoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}