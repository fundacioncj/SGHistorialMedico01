package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.valueobjects.*;
import com.ug.ec.domain.consultaexterna.exceptions.*;
import com.ug.ec.domain.consultaexterna.enums.TipoDiagnostico;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ConsultaExternaValidacionService {
    
    public void validarDatosConsulta(ConsultaExterna consultaExterna) {
        log.info("Validando datos de consulta externa: {}", consultaExterna.getNumeroConsulta());
        
      //  validarDatosPaciente(consultaExterna.getDatosPaciente());
        validarDatosConsulta(consultaExterna.getDatosConsulta());
        validarAnamnesis(consultaExterna.getAnamnesis());
        validarExamenFisico(consultaExterna.getExamenFisico());
        validarDiagnosticos(consultaExterna.getDiagnosticos());
        
        log.info("Validación completada exitosamente para consulta: {}", 
                consultaExterna.getNumeroConsulta());
    }
    
    private void validarDatosPaciente(DatosPaciente datosPaciente) {
        if (datosPaciente == null) {
            throw new DatosPacienteInvalidosException("Los datos del paciente son obligatorios");
        }
        
        if (!validarCedulaEcuatoriana(datosPaciente.getCedula())) {
            throw new DatosPacienteInvalidosException("La cédula no es válida");
        }
        
        if (datosPaciente.calcularEdad() < 0 || datosPaciente.calcularEdad() > 150) {
            throw new DatosPacienteInvalidosException("La edad calculada no es válida");
        }
    }
    
    private void validarDatosConsulta(DatosConsulta datosConsulta) {
        if (datosConsulta == null) {
            throw new DatosConsultaInvalidosException("Los datos de la consulta son obligatorios");
        }
        
        if (datosConsulta.getFechaConsulta() == null) {
            throw new DatosConsultaInvalidosException("La fecha de consulta es obligatoria");
        }
        
        if (datosConsulta.getFechaConsulta().isAfter(LocalDateTime.now())) {
            throw new DatosConsultaInvalidosException("La fecha de consulta no puede ser futura");
        }
    }
    
    private void validarAnamnesis(Anamnesis anamnesis) {
        if (anamnesis == null) {
            throw new AnamnesisInvalidaException("La anamnesis es obligatoria");
        }
        
        if (anamnesis.getEnfermedadActual() == null || 
            anamnesis.getEnfermedadActual().trim().isEmpty()) {
            throw new AnamnesisInvalidaException("La enfermedad actual es obligatoria");
        }
    }
    
    private void validarExamenFisico(ExamenFisico examenFisico) {
        if (examenFisico == null) {
            throw new ExamenFisicoInvalidoException("El examen físico es obligatorio");
        }
        

        

    }
    
    private void validarDiagnosticos(List<Diagnostico> diagnosticos) {
        if (diagnosticos == null || diagnosticos.isEmpty()) {
            throw new DiagnosticoInvalidoException("Debe existir al menos un diagnóstico");
        }
        
        long diagnosticosPrincipales = diagnosticos.stream()
                .filter(Diagnostico::esDiagnosticoPrincipal)
                .count();
        
        if (diagnosticosPrincipales != 1) {
            throw new DiagnosticoInvalidoException("Debe existir exactamente un diagnóstico principal");
        }
    }
    
    private boolean validarCedulaEcuatoriana(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return false;
        }
        
        try {
            int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int suma = 0;
            
            for (int i = 0; i < 9; i++) {
                int digito = Integer.parseInt(cedula.substring(i, i + 1));
                int producto = digito * coeficientes[i];
                
                if (producto > 9) {
                    producto -= 9;
                }
                
                suma += producto;
            }
            
            int digitoVerificador = (10 - (suma % 10)) % 10;
            int ultimoDigito = Integer.parseInt(cedula.substring(9, 10));
            
            return digitoVerificador == ultimoDigito;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
}