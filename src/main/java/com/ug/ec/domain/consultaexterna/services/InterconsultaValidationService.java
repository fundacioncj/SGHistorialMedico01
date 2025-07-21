package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.valueobjects.Interconsulta;
import com.ug.ec.domain.consultaexterna.enums.PrioridadInterconsulta;
import com.ug.ec.domain.consultaexterna.exceptions.InterconsultaInvalidaException;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para validar interconsultas según reglas de negocio
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterconsultaValidationService {
    
    private final DiagnosticoInterconsultaRulesService diagnosticoRulesService;
    
    /**
     * Valida una interconsulta según reglas de negocio específicas
     * @param interconsulta La interconsulta a validar
     * @param codigoDiagnostico El código CIE-10 del diagnóstico relacionado
     * @throws InterconsultaInvalidaException Si la interconsulta no cumple con las reglas de negocio
     */
    public void validarInterconsulta(Interconsulta interconsulta, String codigoDiagnostico) {
        log.info("Validando interconsulta para diagnóstico: {}", codigoDiagnostico);
        
        // Validar que la especialidad sea adecuada para el diagnóstico
        if (!diagnosticoRulesService.esEspecialidadAdecuada(codigoDiagnostico, interconsulta.getEspecialidad())) {
            throw new InterconsultaInvalidaException(
                "La especialidad " + interconsulta.getEspecialidad() + 
                " no es adecuada para el diagnóstico con código " + codigoDiagnostico);
        }
        
        // Validar prioridad según el diagnóstico
        PrioridadInterconsulta prioridadRecomendada = 
            diagnosticoRulesService.calcularPrioridadRecomendada(codigoDiagnostico);
            
        if (prioridadRecomendada == PrioridadInterconsulta.URGENTE && 
            interconsulta.getPrioridad() != PrioridadInterconsulta.URGENTE) {
            throw new InterconsultaInvalidaException(
                "El diagnóstico requiere una interconsulta URGENTE");
        }
        
        // Validar campos obligatorios según especialidad
        List<String> camposFaltantes = validarCamposObligatoriosPorEspecialidad(
            interconsulta, interconsulta.getEspecialidad());
            
        if (!camposFaltantes.isEmpty()) {
            throw new InterconsultaInvalidaException(
                "Faltan campos obligatorios para la especialidad: " + 
                String.join(", ", camposFaltantes));
        }
        
        log.info("Interconsulta validada exitosamente");
    }
    
    /**
     * Valida los campos obligatorios según la especialidad
     * @param interconsulta La interconsulta a validar
     * @param especialidad La especialidad de la interconsulta
     * @return Lista de nombres de campos faltantes
     */
    private List<String> validarCamposObligatoriosPorEspecialidad(
            Interconsulta interconsulta, String especialidad) {
        List<String> camposFaltantes = new ArrayList<>();
        
        // Validaciones específicas por especialidad
        switch (especialidad.toUpperCase()) {
            case "CARDIOLOGÍA":
                if (interconsulta.getExamenesRealizados() == null || 
                    interconsulta.getExamenesRealizados().isEmpty()) {
                    camposFaltantes.add("examenesRealizados");
                }
                if (interconsulta.getHallazgosRelevantes() == null || 
                    interconsulta.getHallazgosRelevantes().isEmpty()) {
                    camposFaltantes.add("hallazgosRelevantes");
                }
                break;
                
            case "NEUROLOGÍA":
                if (interconsulta.getHallazgosRelevantes() == null || 
                    interconsulta.getHallazgosRelevantes().isEmpty()) {
                    camposFaltantes.add("hallazgosRelevantes");
                }
                if (interconsulta.getPreguntaEspecifica() == null || 
                    interconsulta.getPreguntaEspecifica().isEmpty()) {
                    camposFaltantes.add("preguntaEspecifica");
                }
                break;
                
            case "ONCOLOGÍA":
                if (interconsulta.getExamenesRealizados() == null || 
                    interconsulta.getExamenesRealizados().isEmpty()) {
                    camposFaltantes.add("examenesRealizados");
                }
                if (interconsulta.getResultadosExamenes() == null || 
                    interconsulta.getResultadosExamenes().isEmpty()) {
                    camposFaltantes.add("resultadosExamenes");
                }
                if (interconsulta.getHallazgosRelevantes() == null || 
                    interconsulta.getHallazgosRelevantes().isEmpty()) {
                    camposFaltantes.add("hallazgosRelevantes");
                }
                break;
                
            case "PSIQUIATRÍA":
                if (interconsulta.getHallazgosRelevantes() == null || 
                    interconsulta.getHallazgosRelevantes().isEmpty()) {
                    camposFaltantes.add("hallazgosRelevantes");
                }
                if (interconsulta.getPreguntaEspecifica() == null || 
                    interconsulta.getPreguntaEspecifica().isEmpty()) {
                    camposFaltantes.add("preguntaEspecifica");
                }
                break;
                
            // Otras especialidades pueden tener validaciones específicas
            default:
                // Validaciones generales para todas las especialidades
                if (interconsulta.getMotivo() == null || interconsulta.getMotivo().isEmpty()) {
                    camposFaltantes.add("motivo");
                }
        }
        
        return camposFaltantes;
    }
}