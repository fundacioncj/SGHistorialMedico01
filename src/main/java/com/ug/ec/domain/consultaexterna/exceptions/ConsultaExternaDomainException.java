package com.ug.ec.domain.consultaexterna.exceptions;

public abstract class ConsultaExternaDomainException extends RuntimeException {
    
    private final String codigoError;
    
    protected ConsultaExternaDomainException(String codigoError, String mensaje) {
        super(mensaje);
        this.codigoError = codigoError;
    }
    
    protected ConsultaExternaDomainException(String codigoError, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = codigoError;
    }
    
    public String getCodigoError() {
        return codigoError;
    }
}
