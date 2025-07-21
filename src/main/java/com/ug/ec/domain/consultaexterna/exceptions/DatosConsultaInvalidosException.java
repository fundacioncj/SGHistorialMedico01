package com.ug.ec.domain.consultaexterna.exceptions;

public class DatosConsultaInvalidosException extends RuntimeException {
    public DatosConsultaInvalidosException(String message) {
        super(message);
    }
    
    public DatosConsultaInvalidosException(String message, Throwable cause) {
        super(message, cause);
    }
}