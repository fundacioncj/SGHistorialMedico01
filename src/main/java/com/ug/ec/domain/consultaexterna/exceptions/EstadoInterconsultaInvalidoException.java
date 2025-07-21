package com.ug.ec.domain.consultaexterna.exceptions;

public class EstadoInterconsultaInvalidoException extends ConsultaExternaDomainException {
    
    public EstadoInterconsultaInvalidoException(String mensaje) {
        super("ESTADO_INTERCONSULTA_INVALIDO", mensaje);
    }
    
    public EstadoInterconsultaInvalidoException(String mensaje, Throwable causa) {
        super("ESTADO_INTERCONSULTA_INVALIDO", mensaje, causa);
    }
}
