package com.ug.ec.domain.consultaexterna.exceptions;

public class SignosVitalesInvalidosException extends ConsultaExternaDomainException {
    
    public SignosVitalesInvalidosException(String mensaje) {
        super("SIGNOS_VITALES_INVALIDOS", mensaje);
    }
    
    public SignosVitalesInvalidosException(String mensaje, Throwable causa) {
        super("SIGNOS_VITALES_INVALIDOS", mensaje, causa);
    }
}
