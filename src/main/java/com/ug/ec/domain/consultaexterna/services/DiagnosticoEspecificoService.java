package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.valueobjects.Diagnostico;
import com.ug.ec.domain.consultaexterna.enums.TipoDiagnostico;
import com.ug.ec.domain.consultaexterna.exceptions.DiagnosticoInvalidoException;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Servicio para validar diagnósticos específicos según reglas de negocio
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosticoEspecificoService {
    
    private final CIE10ValidationService cie10Service;
    
    // Mapa de códigos CIE-10 que requieren interconsulta obligatoria
    private final Map<String, String> diagnosticosConInterconsultaObligatoria = inicializarDiagnosticosConInterconsultaObligatoria();
    
    // Mapa de códigos CIE-10 que requieren seguimiento obligatorio
    private final Map<String, Integer> diagnosticosConSeguimientoObligatorio = inicializarDiagnosticosConSeguimientoObligatorio();
    
    /**
     * Valida un diagnóstico específico según reglas de negocio
     * @param diagnostico El diagnóstico a validar
     * @throws DiagnosticoInvalidoException Si el diagnóstico no cumple con las reglas de negocio
     */
    public void validarDiagnosticoEspecifico(Diagnostico diagnostico) {
        log.info("Validando diagnóstico específico con código CIE-10: {}", diagnostico.getCodigoCie10());
        
        // Validar código CIE-10
        if (!cie10Service.esCodigoValido(diagnostico.getCodigoCie10())) {
            throw new DiagnosticoInvalidoException(
                "El código CIE-10 " + diagnostico.getCodigoCie10() + " no es válido");
        }
        
        // Validar campos obligatorios según tipo de diagnóstico
        List<String> camposFaltantes = validarCamposObligatoriosPorTipo(diagnostico);
        
        if (!camposFaltantes.isEmpty()) {
            throw new DiagnosticoInvalidoException(
                "Faltan campos obligatorios para el diagnóstico: " + 
                String.join(", ", camposFaltantes));
        }
        
        // Validar coherencia entre campos
        validarCoherenciaCampos(diagnostico);
        
        // Validar reglas específicas según el código CIE-10
        validarReglasPorCodigoCie10(diagnostico);
        
        log.info("Diagnóstico validado exitosamente");
    }
    
    /**
     * Valida los campos obligatorios según el tipo de diagnóstico
     * @param diagnostico El diagnóstico a validar
     * @return Lista de nombres de campos faltantes
     */
    private List<String> validarCamposObligatoriosPorTipo(Diagnostico diagnostico) {
        List<String> camposFaltantes = new ArrayList<>();
        
        // Validaciones específicas por tipo de diagnóstico
        if (diagnostico.getTipo() == TipoDiagnostico.PRINCIPAL) {
            if (diagnostico.getSeveridad() == null) {
                camposFaltantes.add("severidad");
            }
        }
        
        if (Boolean.TRUE.equals(diagnostico.getEsCronico())) {
            if (diagnostico.getRequiereSeguimiento() == null || !diagnostico.getRequiereSeguimiento()) {
                camposFaltantes.add("requiereSeguimiento");
            }
            if (diagnostico.getTiempoSeguimientoMeses() == null) {
                camposFaltantes.add("tiempoSeguimientoMeses");
            }
            if (diagnostico.getPlanSeguimiento() == null || diagnostico.getPlanSeguimiento().isEmpty()) {
                camposFaltantes.add("planSeguimiento");
            }
        }
        
        if (Boolean.TRUE.equals(diagnostico.getRequiereInterconsulta())) {
            if (diagnostico.getEspecialidadRecomendada() == null || 
                diagnostico.getEspecialidadRecomendada().isEmpty()) {
                camposFaltantes.add("especialidadRecomendada");
            }
        }
        
        return camposFaltantes;
    }
    
    /**
     * Valida la coherencia entre campos del diagnóstico
     * @param diagnostico El diagnóstico a validar
     * @throws DiagnosticoInvalidoException Si hay incoherencias entre los campos
     */
    private void validarCoherenciaCampos(Diagnostico diagnostico) {
        // Validar coherencia entre severidad y manifestaciones clínicas
        if ("SEVERO".equals(diagnostico.getSeveridad()) && 
            (diagnostico.getManifestacionesClinicas() == null || 
             diagnostico.getManifestacionesClinicas().isEmpty())) {
            throw new DiagnosticoInvalidoException(
                "Un diagnóstico con severidad SEVERO debe incluir manifestaciones clínicas");
        }
        
        // Validar coherencia entre diagnóstico crónico y seguimiento
        if (Boolean.TRUE.equals(diagnostico.getEsCronico()) && 
            (diagnostico.getRequiereSeguimiento() == null || 
             !diagnostico.getRequiereSeguimiento())) {
            throw new DiagnosticoInvalidoException(
                "Un diagnóstico crónico debe requerir seguimiento");
        }
        
        // Validar coherencia entre requerimiento de interconsulta y especialidad
        if (Boolean.TRUE.equals(diagnostico.getRequiereInterconsulta()) && 
            (diagnostico.getEspecialidadRecomendada() == null || 
             diagnostico.getEspecialidadRecomendada().isEmpty())) {
            throw new DiagnosticoInvalidoException(
                "Si el diagnóstico requiere interconsulta, debe especificar la especialidad recomendada");
        }
    }
    
    /**
     * Valida reglas específicas según el código CIE-10
     * @param diagnostico El diagnóstico a validar
     * @throws DiagnosticoInvalidoException Si no cumple con las reglas específicas
     */
    private void validarReglasPorCodigoCie10(Diagnostico diagnostico) {
        String codigoBase = obtenerCodigoBase(diagnostico.getCodigoCie10());
        
        // Verificar si el diagnóstico requiere interconsulta obligatoria
        if (diagnosticosConInterconsultaObligatoria.containsKey(codigoBase)) {
            String especialidadRequerida = diagnosticosConInterconsultaObligatoria.get(codigoBase);
            
            if (diagnostico.getRequiereInterconsulta() == null || 
                !diagnostico.getRequiereInterconsulta()) {
                throw new DiagnosticoInvalidoException(
                    "El diagnóstico con código " + diagnostico.getCodigoCie10() + 
                    " requiere interconsulta obligatoria");
            }
            
            if (diagnostico.getEspecialidadRecomendada() == null || 
                !diagnostico.getEspecialidadRecomendada().equalsIgnoreCase(especialidadRequerida)) {
                throw new DiagnosticoInvalidoException(
                    "El diagnóstico con código " + diagnostico.getCodigoCie10() + 
                    " requiere interconsulta con la especialidad: " + especialidadRequerida);
            }
        }
        
        // Verificar si el diagnóstico requiere seguimiento obligatorio
        if (diagnosticosConSeguimientoObligatorio.containsKey(codigoBase)) {
            Integer mesesSeguimiento = diagnosticosConSeguimientoObligatorio.get(codigoBase);
            
            if (diagnostico.getRequiereSeguimiento() == null || 
                !diagnostico.getRequiereSeguimiento()) {
                throw new DiagnosticoInvalidoException(
                    "El diagnóstico con código " + diagnostico.getCodigoCie10() + 
                    " requiere seguimiento obligatorio");
            }
            
            if (diagnostico.getTiempoSeguimientoMeses() == null || 
                diagnostico.getTiempoSeguimientoMeses() < mesesSeguimiento) {
                throw new DiagnosticoInvalidoException(
                    "El diagnóstico con código " + diagnostico.getCodigoCie10() + 
                    " requiere un seguimiento mínimo de " + mesesSeguimiento + " meses");
            }
        }
        
        // Verificar si el diagnóstico es crónico según el código
        if (esDiagnosticoCronico(codigoBase) && 
            (diagnostico.getEsCronico() == null || !diagnostico.getEsCronico())) {
            throw new DiagnosticoInvalidoException(
                "El diagnóstico con código " + diagnostico.getCodigoCie10() + 
                " debe marcarse como crónico");
        }
    }
    
    /**
     * Determina si un diagnóstico requiere interconsulta obligatoria
     * @param codigoCie10 Código CIE-10 del diagnóstico
     * @return true si requiere interconsulta obligatoria, false en caso contrario
     */
    public boolean requiereInterconsultaObligatoria(String codigoCie10) {
        if (codigoCie10 == null) {
            return false;
        }
        
        String codigoBase = obtenerCodigoBase(codigoCie10);
        return diagnosticosConInterconsultaObligatoria.containsKey(codigoBase);
    }
    
    /**
     * Determina la especialidad recomendada para un diagnóstico
     * @param codigoCie10 Código CIE-10 del diagnóstico
     * @return Especialidad recomendada o null si no hay recomendación
     */
    public String determinarEspecialidadRecomendada(String codigoCie10) {
        if (codigoCie10 == null) {
            return null;
        }
        
        String codigoBase = obtenerCodigoBase(codigoCie10);
        return diagnosticosConInterconsultaObligatoria.get(codigoBase);
    }
    
    /**
     * Determina si un diagnóstico es crónico según su código CIE-10
     * @param codigoBase Código base CIE-10 (sin subcategorías)
     * @return true si es crónico, false en caso contrario
     */
    private boolean esDiagnosticoCronico(String codigoBase) {
        // Lista de códigos de enfermedades crónicas comunes
        return codigoBase.matches("^(E10|E11|E14)$") || // Diabetes
               codigoBase.matches("^I1[0-5]$") ||      // Hipertensión
               codigoBase.matches("^J4[0-7]$") ||      // EPOC, asma
               codigoBase.matches("^F2[0-9]$") ||      // Esquizofrenia
               codigoBase.matches("^F3[0-9]$") ||      // Trastornos del humor
               codigoBase.matches("^G20$") ||          // Parkinson
               codigoBase.matches("^G35$") ||          // Esclerosis múltiple
               codigoBase.matches("^G40$") ||          // Epilepsia
               codigoBase.matches("^M0[5-6]$") ||      // Artritis reumatoide
               codigoBase.matches("^M15$") ||          // Osteoartrosis
               codigoBase.matches("^N18$");            // Insuficiencia renal crónica
    }
    
    /**
     * Obtiene el código base CIE-10 (sin subcategorías)
     * @param codigoCie10 Código CIE-10 completo
     * @return Código base
     */
    private String obtenerCodigoBase(String codigoCie10) {
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
     * Inicializa el mapa de diagnósticos que requieren interconsulta obligatoria
     * @return Mapa inicializado
     */
    private Map<String, String> inicializarDiagnosticosConInterconsultaObligatoria() {
        Map<String, String> mapa = new HashMap<>();
        
        // Enfermedades cardiovasculares
        mapa.put("I20", "CARDIOLOGÍA");  // Angina de pecho
        mapa.put("I21", "CARDIOLOGÍA");  // Infarto agudo de miocardio
        mapa.put("I25", "CARDIOLOGÍA");  // Enfermedad isquémica crónica del corazón
        mapa.put("I50", "CARDIOLOGÍA");  // Insuficiencia cardíaca
        
        // Enfermedades neurológicas
        mapa.put("G20", "NEUROLOGÍA");   // Enfermedad de Parkinson
        mapa.put("G35", "NEUROLOGÍA");   // Esclerosis múltiple
        mapa.put("G40", "NEUROLOGÍA");   // Epilepsia
        mapa.put("G45", "NEUROLOGÍA");   // Ataques isquémicos cerebrales transitorios
        
        // Enfermedades psiquiátricas
        mapa.put("F20", "PSIQUIATRÍA");  // Esquizofrenia
        mapa.put("F31", "PSIQUIATRÍA");  // Trastorno bipolar
        mapa.put("F32", "PSIQUIATRÍA");  // Episodios depresivos
        
        // Enfermedades oncológicas
        mapa.put("C50", "ONCOLOGÍA");    // Tumor maligno de la mama
        mapa.put("C61", "ONCOLOGÍA");    // Tumor maligno de la próstata
        mapa.put("C18", "ONCOLOGÍA");    // Tumor maligno del colon
        mapa.put("C34", "ONCOLOGÍA");    // Tumor maligno de los bronquios y del pulmón
        
        // Enfermedades endocrinológicas
        mapa.put("E10", "ENDOCRINOLOGÍA"); // Diabetes mellitus insulinodependiente
        mapa.put("E11", "ENDOCRINOLOGÍA"); // Diabetes mellitus no insulinodependiente
        mapa.put("E05", "ENDOCRINOLOGÍA"); // Tirotoxicosis
        
        return mapa;
    }
    
    /**
     * Inicializa el mapa de diagnósticos que requieren seguimiento obligatorio
     * @return Mapa inicializado
     */
    private Map<String, Integer> inicializarDiagnosticosConSeguimientoObligatorio() {
        Map<String, Integer> mapa = new HashMap<>();
        
        // Enfermedades cardiovasculares (meses de seguimiento)
        mapa.put("I10", 3);  // Hipertensión esencial
        mapa.put("I20", 1);  // Angina de pecho
        mapa.put("I21", 1);  // Infarto agudo de miocardio
        mapa.put("I50", 1);  // Insuficiencia cardíaca
        
        // Enfermedades endocrinológicas
        mapa.put("E10", 3);  // Diabetes mellitus insulinodependiente
        mapa.put("E11", 3);  // Diabetes mellitus no insulinodependiente
        mapa.put("E05", 2);  // Tirotoxicosis
        
        // Enfermedades neurológicas
        mapa.put("G20", 3);  // Enfermedad de Parkinson
        mapa.put("G35", 3);  // Esclerosis múltiple
        mapa.put("G40", 3);  // Epilepsia
        
        // Enfermedades psiquiátricas
        mapa.put("F20", 1);  // Esquizofrenia
        mapa.put("F31", 1);  // Trastorno bipolar
        mapa.put("F32", 1);  // Episodios depresivos
        
        // Enfermedades oncológicas
        mapa.put("C50", 1);  // Tumor maligno de la mama
        mapa.put("C61", 1);  // Tumor maligno de la próstata
        mapa.put("C18", 1);  // Tumor maligno del colon
        mapa.put("C34", 1);  // Tumor maligno de los bronquios y del pulmón
        
        return mapa;
    }
}