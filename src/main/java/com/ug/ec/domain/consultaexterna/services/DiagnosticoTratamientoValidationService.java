package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.valueobjects.Diagnostico;
import com.ug.ec.domain.consultaexterna.valueobjects.Prescripcion;
import com.ug.ec.domain.consultaexterna.valueobjects.PlanTratamiento;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Servicio para validar la coherencia entre diagnósticos y tratamientos
 * Implementa reglas de validación cruzada entre diagnósticos CIE-10 y prescripciones médicas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiagnosticoTratamientoValidationService {
    
    private final CIE10ValidationService cie10ValidationService;
    
    /**
     * Valida la coherencia entre los diagnósticos y el plan de tratamiento de una consulta
     * @param consulta Consulta externa a validar
     * @return Lista de advertencias encontradas (vacía si todo es coherente)
     */
    public List<String> validarCoherenciaDiagnosticoTratamiento(ConsultaExterna consulta) {
        List<String> advertencias = new ArrayList<>();
        
        if (consulta == null) {
            advertencias.add("No se puede validar una consulta nula");
            return advertencias;
        }
        
        List<Diagnostico> diagnosticos = consulta.getDiagnosticos();
        PlanTratamiento planTratamiento = consulta.getPlanTratamiento();
        
        if (diagnosticos == null || diagnosticos.isEmpty()) {
            advertencias.add("La consulta no tiene diagnósticos registrados");
            return advertencias;
        }
        
        if (planTratamiento == null) {
            advertencias.add("La consulta no tiene plan de tratamiento registrado");
            return advertencias;
        }
        
        // Validar códigos CIE-10 de los diagnósticos
        for (Diagnostico diagnostico : diagnosticos) {
            if (diagnostico.getCodigoCie10() == null || diagnostico.getCodigoCie10().trim().isEmpty()) {
                advertencias.add("Diagnóstico sin código CIE-10: " + diagnostico.getDescripcion());
                continue;
            }
            
            List<String> erroresCIE10 = cie10ValidationService.validarCIE10(diagnostico.getCodigoCie10());
            if (!erroresCIE10.isEmpty()) {
                advertencias.addAll(erroresCIE10.stream()
                        .map(error -> "Error en diagnóstico '" + diagnostico.getDescripcion() + "': " + error)
                        .collect(Collectors.toList()));
            }
        }
        
        // Validar coherencia entre diagnósticos y prescripciones
        List<Prescripcion> prescripciones = planTratamiento.getPrescripciones();
        if (prescripciones != null && !prescripciones.isEmpty()) {
            advertencias.addAll(validarCoherenciaPrescripciones(diagnosticos, prescripciones));
        }
        
        // Validar si los diagnósticos requieren interconsultas
        advertencias.addAll(validarNecesidadInterconsultas(diagnosticos, planTratamiento));
        
        return advertencias;
    }
    
    /**
     * Valida la coherencia entre los diagnósticos y las prescripciones
     * @param diagnosticos Lista de diagnósticos
     * @param prescripciones Lista de prescripciones
     * @return Lista de advertencias encontradas
     */
    private List<String> validarCoherenciaPrescripciones(List<Diagnostico> diagnosticos, List<Prescripcion> prescripciones) {
        List<String> advertencias = new ArrayList<>();
        
        // Mapa para agrupar medicamentos por diagnóstico
        Map<String, List<String>> medicamentosPorDiagnostico = new HashMap<>();
        
        // Verificar compatibilidad de cada prescripción con los diagnósticos
        for (Prescripcion prescripcion : prescripciones) {
            String medicamento = prescripcion.getMedicamento();
            if (medicamento == null || medicamento.trim().isEmpty()) {
                advertencias.add("Prescripción sin medicamento especificado");
                continue;
            }
            
            boolean esCompatible = false;
            String diagnosticoCompatible = null;
            
            // Verificar compatibilidad con cada diagnóstico
            for (Diagnostico diagnostico : diagnosticos) {
                String codigoCIE10 = diagnostico.getCodigoCie10();
                if (codigoCIE10 == null || codigoCIE10.trim().isEmpty()) {
                    continue;
                }
                
                if (cie10ValidationService.esDiagnosticoCompatibleConTratamiento(codigoCIE10, medicamento)) {
                    esCompatible = true;
                    diagnosticoCompatible = diagnostico.getDescripcion();
                    
                    // Agregar al mapa de medicamentos por diagnóstico
                    medicamentosPorDiagnostico.computeIfAbsent(codigoCIE10, k -> new ArrayList<>())
                            .add(medicamento);
                    
                    break;
                }
            }
            
            if (!esCompatible) {
                advertencias.add("El medicamento '" + medicamento + "' no parece ser compatible con ninguno de los diagnósticos registrados");
            } else {
                log.debug("Medicamento '{}' compatible con diagnóstico '{}'", medicamento, diagnosticoCompatible);
            }
        }
        
        // Verificar si hay diagnósticos sin tratamiento
        for (Diagnostico diagnostico : diagnosticos) {
            String codigoCIE10 = diagnostico.getCodigoCie10();
            if (codigoCIE10 == null || codigoCIE10.trim().isEmpty()) {
                continue;
            }
            
            if (!medicamentosPorDiagnostico.containsKey(codigoCIE10)) {
                advertencias.add("El diagnóstico '" + diagnostico.getDescripcion() + 
                        "' (" + codigoCIE10 + ") no tiene tratamiento farmacológico asociado");
            }
        }
        
        return advertencias;
    }
    
    /**
     * Valida si los diagnósticos requieren interconsultas y si están incluidas en el plan
     * @param diagnosticos Lista de diagnósticos
     * @param planTratamiento Plan de tratamiento
     * @return Lista de advertencias encontradas
     */
    private List<String> validarNecesidadInterconsultas(List<Diagnostico> diagnosticos, PlanTratamiento planTratamiento) {
        List<String> advertencias = new ArrayList<>();
        
        // Verificar si hay diagnósticos que requieren atención especializada
        List<Diagnostico> diagnosticosEspecializados = diagnosticos.stream()
                .filter(d -> d.getCodigoCie10() != null && 
                        cie10ValidationService.requiereAtencionEspecializada(d.getCodigoCie10()))
                .collect(Collectors.toList());
        
        if (diagnosticosEspecializados.isEmpty()) {
            return advertencias;
        }
        
        // Verificar si hay interconsultas en el plan
        if (planTratamiento.getInterconsultas() == null || planTratamiento.getInterconsultas().isEmpty()) {
            advertencias.add("Se detectaron diagnósticos que requieren atención especializada, pero no hay interconsultas registradas");
            
            // Agregar detalle de los diagnósticos que requieren especialista
            diagnosticosEspecializados.forEach(d -> 
                    advertencias.add("El diagnóstico '" + d.getDescripcion() + "' (" + d.getCodigoCie10() + 
                            ") requiere valoración por especialista"));
        }
        
        return advertencias;
    }
    
    /**
     * Valida si un diagnóstico tiene el tratamiento adecuado según su código CIE-10
     * @param diagnostico Diagnóstico a validar
     * @param prescripciones Lista de prescripciones
     * @return true si el diagnóstico tiene tratamiento adecuado, false en caso contrario
     */
    public boolean tieneTratamientoAdecuado(Diagnostico diagnostico, List<Prescripcion> prescripciones) {
        if (diagnostico == null || diagnostico.getCodigoCie10() == null || 
                prescripciones == null || prescripciones.isEmpty()) {
            return false;
        }
        
        String codigoCIE10 = diagnostico.getCodigoCie10().trim();
        
        return prescripciones.stream()
                .anyMatch(p -> p.getMedicamento() != null && 
                        cie10ValidationService.esDiagnosticoCompatibleConTratamiento(
                                codigoCIE10, p.getMedicamento()));
    }
    
    /**
     * Verifica si una consulta tiene todos sus diagnósticos con tratamiento adecuado
     * @param consulta Consulta a verificar
     * @return true si todos los diagnósticos tienen tratamiento adecuado, false en caso contrario
     */
    public boolean tieneTodosLosDiagnosticosConTratamiento(ConsultaExterna consulta) {
        if (consulta == null || consulta.getDiagnosticos() == null || consulta.getDiagnosticos().isEmpty() ||
                consulta.getPlanTratamiento() == null || consulta.getPlanTratamiento().getPrescripciones() == null) {
            return false;
        }
        
        List<Diagnostico> diagnosticos = consulta.getDiagnosticos();
        List<Prescripcion> prescripciones = consulta.getPlanTratamiento().getPrescripciones();
        
        if (prescripciones.isEmpty()) {
            return false;
        }
        
        return diagnosticos.stream()
                .allMatch(d -> tieneTratamientoAdecuado(d, prescripciones));
    }
}