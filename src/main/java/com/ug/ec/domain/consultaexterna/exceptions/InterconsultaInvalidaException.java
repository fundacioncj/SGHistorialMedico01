package com.ug.ec.domain.consultaexterna.exceptions;

/**
 * Excepción lanzada cuando una interconsulta no cumple con las reglas de negocio
 */
public class InterconsultaInvalidaException extends ConsultaExternaDomainException {
    
    private static final String CODIGO_ERROR = "INTERCONSULTA_INVALIDA";
    
    public InterconsultaInvalidaException(String message) {
        super(CODIGO_ERROR, message);
    }
    
    public InterconsultaInvalidaException(String message, Throwable cause) {
        super(CODIGO_ERROR, message, cause);
    }
}