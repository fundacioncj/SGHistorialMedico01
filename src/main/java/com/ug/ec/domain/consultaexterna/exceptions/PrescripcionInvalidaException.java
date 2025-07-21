package com.ug.ec.domain.consultaexterna.exceptions;

/**
 * Excepción lanzada cuando una prescripción no cumple con las reglas de negocio
 */
public class PrescripcionInvalidaException extends ConsultaExternaDomainException {
    
    private static final String CODIGO_ERROR = "PRESCRIPCION_INVALIDA";
    
    public PrescripcionInvalidaException(String message) {
        super(CODIGO_ERROR, message);
    }
    
    public PrescripcionInvalidaException(String message, Throwable cause) {
        super(CODIGO_ERROR, message, cause);
    }
}