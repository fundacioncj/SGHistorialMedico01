package com.ug.ec.domain.consultaexterna.exceptions;

public class AnamnesisInvalidaException extends RuntimeException {
    public AnamnesisInvalidaException(String message) {
        super(message);
    }
    
    public AnamnesisInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}