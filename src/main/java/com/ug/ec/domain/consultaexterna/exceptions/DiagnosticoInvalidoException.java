package com.ug.ec.domain.consultaexterna.exceptions;

/**
 * Excepción lanzada cuando un diagnóstico no cumple con las reglas de negocio
 */
public class DiagnosticoInvalidoException extends ConsultaExternaDomainException {
    
    private static final String CODIGO_ERROR = "DIAGNOSTICO_INVALIDO";
    
    public DiagnosticoInvalidoException(String message) {
        super(CODIGO_ERROR, message);
    }
    
    public DiagnosticoInvalidoException(String message, Throwable cause) {
        super(CODIGO_ERROR, message, cause);
    }
}