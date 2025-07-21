package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.enums.PrioridadInterconsulta;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Servicio que contiene las reglas de negocio para la relación entre diagnósticos e interconsultas
 */
@Slf4j
@Service
public class DiagnosticoInterconsultaRulesService {

    // Mapa que relaciona códigos CIE-10 con especialidades recomendadas
    private final Map<String, Set<String>> especialidadesPorDiagnostico;
    
    // Mapa que relaciona códigos CIE-10 con prioridades recomendadas
    private final Map<String, PrioridadInterconsulta> prioridadesPorDiagnostico;
    
    public DiagnosticoInterconsultaRulesService() {
        // Inicializar mapas con reglas predefinidas
        this.especialidadesPorDiagnostico = inicializarEspecialidadesPorDiagnostico();
        this.prioridadesPorDiagnostico = inicializarPrioridadesPorDiagnostico();
        log.info("DiagnosticoInterconsultaRulesService inicializado con {} reglas de especialidad y {} reglas de prioridad", 
                especialidadesPorDiagnostico.size(), prioridadesPorDiagnostico.size());
    }
    
    /**
     * Verifica si una especialidad es adecuada para un diagnóstico
     * @param codigoCie10 Código CIE-10 del diagnóstico
     * @param especialidad Especialidad a verificar
     * @return true si la especialidad es adecuada, false en caso contrario
     */
    public boolean esEspecialidadAdecuada(String codigoCie10, String especialidad) {
        if (codigoCie10 == null || especialidad == null) {
            return false;
        }
        
        // Normalizar el código CIE-10 (solo la parte principal)
        String codigoBase = normalizarCodigoCie10(codigoCie10);
        
        // Normalizar la especialidad
        String especialidadNormalizada = especialidad.toUpperCase().trim();
        
        // Verificar si hay reglas específicas para este diagnóstico
        Set<String> especialidadesRecomendadas = especialidadesPorDiagnostico.get(codigoBase);
        
        // Si no hay reglas específicas, permitir cualquier especialidad
        if (especialidadesRecomendadas == null || especialidadesRecomendadas.isEmpty()) {
            return true;
        }
        
        // Verificar si la especialidad está en la lista de recomendadas
        return especialidadesRecomendadas.contains(especialidadNormalizada);
    }
    
    /**
     * Calcula la prioridad recomendada para una interconsulta según el diagnóstico
     * @param codigoCie10 Código CIE-10 del diagnóstico
     * @return Prioridad recomendada
     */
    public PrioridadInterconsulta calcularPrioridadRecomendada(String codigoCie10) {
        if (codigoCie10 == null) {
            return PrioridadInterconsulta.MEDIA; // Prioridad por defecto
        }
        
        // Normalizar el código CIE-10 (solo la parte principal)
        String codigoBase = normalizarCodigoCie10(codigoCie10);
        
        // Verificar si hay una prioridad específica para este diagnóstico
        PrioridadInterconsulta prioridadRecomendada = prioridadesPorDiagnostico.get(codigoBase);
        
        // Si no hay una prioridad específica, usar la prioridad por defecto
        return prioridadRecomendada != null ? prioridadRecomendada : PrioridadInterconsulta.MEDIA;
    }
    
    /**
     * Normaliza un código CIE-10 para obtener solo la parte principal (ej: "I10.1" -> "I10")
     * @param codigoCie10 Código CIE-10 completo
     * @return Código CIE-10 normalizado
     */
    private String normalizarCodigoCie10(String codigoCie10) {
        if (codigoCie10 == null) {
            return "";
        }
        
        // Obtener solo la parte principal del código (antes del punto)
        int puntoIndex = codigoCie10.indexOf('.');
        if (puntoIndex > 0) {
            return codigoCie10.substring(0, puntoIndex);
        }
        
        return codigoCie10;
    }
    
    /**
     * Inicializa el mapa de especialidades recomendadas por diagnóstico
     * @return Mapa inicializado
     */
    private Map<String, Set<String>> inicializarEspecialidadesPorDiagnostico() {
        Map<String, Set<String>> mapa = new HashMap<>();
        
        // Enfermedades cardiovasculares (I00-I99)
        mapa.put("I10", new HashSet<>(Arrays.asList("CARDIOLOGÍA", "MEDICINA INTERNA")));
        mapa.put("I20", new HashSet<>(Arrays.asList("CARDIOLOGÍA")));
        mapa.put("I21", new HashSet<>(Arrays.asList("CARDIOLOGÍA", "MEDICINA INTERNA", "CUIDADOS INTENSIVOS")));
        mapa.put("I50", new HashSet<>(Arrays.asList("CARDIOLOGÍA", "MEDICINA INTERNA")));
        
        // Enfermedades neurológicas (G00-G99)
        mapa.put("G40", new HashSet<>(Arrays.asList("NEUROLOGÍA")));
        mapa.put("G43", new HashSet<>(Arrays.asList("NEUROLOGÍA")));
        mapa.put("G45", new HashSet<>(Arrays.asList("NEUROLOGÍA", "MEDICINA INTERNA")));
        
        // Enfermedades psiquiátricas (F00-F99)
        mapa.put("F20", new HashSet<>(Arrays.asList("PSIQUIATRÍA")));
        mapa.put("F32", new HashSet<>(Arrays.asList("PSIQUIATRÍA", "PSICOLOGÍA")));
        mapa.put("F41", new HashSet<>(Arrays.asList("PSIQUIATRÍA", "PSICOLOGÍA")));
        
        // Enfermedades oncológicas (C00-D48)
        mapa.put("C50", new HashSet<>(Arrays.asList("ONCOLOGÍA", "CIRUGÍA ONCOLÓGICA")));
        mapa.put("C61", new HashSet<>(Arrays.asList("ONCOLOGÍA", "UROLOGÍA")));
        mapa.put("C18", new HashSet<>(Arrays.asList("ONCOLOGÍA", "GASTROENTEROLOGÍA")));
        
        // Enfermedades endocrinológicas (E00-E90)
        mapa.put("E10", new HashSet<>(Arrays.asList("ENDOCRINOLOGÍA", "MEDICINA INTERNA")));
        mapa.put("E11", new HashSet<>(Arrays.asList("ENDOCRINOLOGÍA", "MEDICINA INTERNA")));
        mapa.put("E05", new HashSet<>(Arrays.asList("ENDOCRINOLOGÍA")));
        
        return mapa;
    }
    
    /**
     * Inicializa el mapa de prioridades recomendadas por diagnóstico
     * @return Mapa inicializado
     */
    private Map<String, PrioridadInterconsulta> inicializarPrioridadesPorDiagnostico() {
        Map<String, PrioridadInterconsulta> mapa = new HashMap<>();
        
        // Diagnósticos que requieren atención urgente
        mapa.put("I21", PrioridadInterconsulta.URGENTE); // Infarto agudo de miocardio
        mapa.put("I22", PrioridadInterconsulta.URGENTE); // Infarto subsecuente
        mapa.put("I60", PrioridadInterconsulta.URGENTE); // Hemorragia subaracnoidea
        mapa.put("I61", PrioridadInterconsulta.URGENTE); // Hemorragia intracerebral
        mapa.put("I63", PrioridadInterconsulta.URGENTE); // Infarto cerebral
        mapa.put("I64", PrioridadInterconsulta.URGENTE); // Accidente cerebrovascular
        mapa.put("G45", PrioridadInterconsulta.URGENTE); // Ataque isquémico transitorio
        mapa.put("K92", PrioridadInterconsulta.URGENTE); // Hemorragia gastrointestinal
        mapa.put("C50", PrioridadInterconsulta.URGENTE); // Cáncer de mama
        mapa.put("C61", PrioridadInterconsulta.URGENTE); // Cáncer de próstata
        mapa.put("C18", PrioridadInterconsulta.URGENTE); // Cáncer de colon
        
        // Diagnósticos que requieren atención alta
        mapa.put("I20", PrioridadInterconsulta.ALTA); // Angina de pecho
        mapa.put("I50", PrioridadInterconsulta.ALTA); // Insuficiencia cardíaca
        mapa.put("E10", PrioridadInterconsulta.ALTA); // Diabetes tipo 1
        mapa.put("E11", PrioridadInterconsulta.ALTA); // Diabetes tipo 2 con complicaciones
        mapa.put("G40", PrioridadInterconsulta.ALTA); // Epilepsia
        mapa.put("F20", PrioridadInterconsulta.ALTA); // Esquizofrenia
        
        // Diagnósticos que requieren atención media (por defecto)
        mapa.put("I10", PrioridadInterconsulta.MEDIA); // Hipertensión esencial
        mapa.put("E78", PrioridadInterconsulta.MEDIA); // Trastornos del metabolismo de lipoproteínas
        mapa.put("F32", PrioridadInterconsulta.MEDIA); // Episodio depresivo
        mapa.put("F41", PrioridadInterconsulta.MEDIA); // Trastornos de ansiedad
        
        // Diagnósticos que requieren atención baja
        mapa.put("M54", PrioridadInterconsulta.BAJA); // Dorsalgia
        mapa.put("J30", PrioridadInterconsulta.BAJA); // Rinitis alérgica
        mapa.put("L20", PrioridadInterconsulta.BAJA); // Dermatitis atópica
        
        return mapa;
    }
}