package com.ug.ec.domain.consultaexterna.exceptions;

public class DatosPacienteInvalidosException extends RuntimeException {
    public DatosPacienteInvalidosException(String message) {
        super(message);
    }
    
    public DatosPacienteInvalidosException(String message, Throwable cause) {
        super(message, cause);
    }
}