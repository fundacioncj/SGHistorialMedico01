package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.valueobjects.Prescripcion;
import com.ug.ec.domain.consultaexterna.valueobjects.DatosPaciente;
import com.ug.ec.domain.consultaexterna.exceptions.PrescripcionInvalidaException;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * Servicio para validar prescripciones según reglas de negocio
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrescripcionValidationService {
    
    private final InteraccionesMedicamentosService interaccionesService;
    
    /**
     * Valida una prescripción según reglas de negocio específicas
     * @param prescripcion La prescripción a validar
     * @param prescripcionesExistentes Lista de prescripciones existentes
     * @param datosPaciente Datos del paciente
     * @throws PrescripcionInvalidaException Si la prescripción no cumple con las reglas de negocio
     */
    public void validarPrescripcion(Prescripcion prescripcion, 
                                   List<Prescripcion> prescripcionesExistentes, 
                                   DatosPaciente datosPaciente) {
        log.info("Validando prescripción para medicamento: {}", prescripcion.getMedicamento());
        
        // Validar dosis según peso y edad
        validarDosisPorPesoYEdad(prescripcion, datosPaciente);
        
        // Validar interacciones medicamentosas
        List<String> interacciones = interaccionesService.verificarInteracciones(
            prescripcion, prescripcionesExistentes);
            
        if (!interacciones.isEmpty()) {
            throw new PrescripcionInvalidaException(
                "Se detectaron posibles interacciones medicamentosas: " + 
                String.join(", ", interacciones));
        }
        
        // Validar contraindicaciones
        List<String> contraindicaciones = validarContraindicaciones(prescripcion, datosPaciente);
        
        if (!contraindicaciones.isEmpty()) {
            throw new PrescripcionInvalidaException(
                "El medicamento está contraindicado: " + 
                String.join(", ", contraindicaciones));
        }
        
        // Validar duración del tratamiento
        validarDuracionTratamiento(prescripcion);
        
        // Validar campos obligatorios según tipo de medicamento
        validarCamposObligatoriosPorTipoMedicamento(prescripcion);
        
        log.info("Prescripción validada exitosamente");
    }
    
    /**
     * Valida la dosis según la edad del paciente
     * @param prescripcion La prescripción a validar
     * @param datosPaciente Datos del paciente
     * @throws PrescripcionInvalidaException Si la dosis no es adecuada
     */
    private void validarDosisPorPesoYEdad(Prescripcion prescripcion, DatosPaciente datosPaciente) {
        if (datosPaciente == null || datosPaciente.getFechaNacimiento() == null) {
            log.warn("No se puede validar dosis por edad: datos del paciente incompletos");
            return;
        }
        
        // Calcular la edad del paciente
        int edadAnios = datosPaciente.calcularEdad();
        
        // Validar medicamentos específicos
        String medicamento = prescripcion.getMedicamento().toLowerCase();
        
        // Validar paracetamol
        if (medicamento.contains("paracetamol") || medicamento.contains("acetaminofen")) {
            validarDosisParacetamol(prescripcion, edadAnios);
        }
        
        // Validar ibuprofeno
        else if (medicamento.contains("ibuprofeno")) {
            validarDosisIbuprofeno(prescripcion, edadAnios);
        }
        
        // Validar antibióticos
        else if (prescripcion.esAntibiotico()) {
            validarDosisAntibiotico(prescripcion, edadAnios);
        }
        
        // Validar medicamentos controlados
        else if (prescripcion.esControlada()) {
            validarDosisMedicamentoControlado(prescripcion, edadAnios);
        }
    }
    
    /**
     * Valida la dosis de paracetamol según edad
     * @param prescripcion La prescripción a validar
     * @param edadAnios Edad del paciente en años
     * @throws PrescripcionInvalidaException Si la dosis no es adecuada
     */
    private void validarDosisParacetamol(Prescripcion prescripcion, int edadAnios) {
        // Extraer la dosis numérica (asumiendo formato como "500 mg" o "1000mg")
        String dosisTxt = prescripcion.getDosis().replaceAll("[^0-9]", "");
        if (dosisTxt.isEmpty()) {
            log.warn("No se puede validar dosis de paracetamol: formato de dosis no reconocido");
            return;
        }
        
        int dosisMg;
        try {
            dosisMg = Integer.parseInt(dosisTxt);
        } catch (NumberFormatException e) {
            log.warn("No se puede validar dosis de paracetamol: error al parsear dosis");
            return;
        }
        
        // Validar según edad
        if (edadAnios < 12) {
            // Niños: validación básica por edad
            if (edadAnios < 2 && dosisMg > 120) {
                throw new PrescripcionInvalidaException(
                    "La dosis de paracetamol excede el máximo recomendado para niños menores de 2 años (120 mg)");
            } else if (edadAnios < 6 && dosisMg > 240) {
                throw new PrescripcionInvalidaException(
                    "La dosis de paracetamol excede el máximo recomendado para niños de 2-5 años (240 mg)");
            } else if (edadAnios < 12 && dosisMg > 480) {
                throw new PrescripcionInvalidaException(
                    "La dosis de paracetamol excede el máximo recomendado para niños de 6-11 años (480 mg)");
            }
        } else {
            // Adultos: máximo 1000 mg por dosis, máximo 4000 mg al día
            if (dosisMg > 1000) {
                throw new PrescripcionInvalidaException(
                    "La dosis de paracetamol excede el máximo recomendado para adultos (1000 mg por dosis)");
            }
            
            // Verificar frecuencia para calcular dosis diaria
            String frecuencia = prescripcion.getFrecuencia().toLowerCase();
            int dosisAlDia = 0;
            
            if (frecuencia.contains("cada 4") || frecuencia.contains("cada 4-6") || 
                frecuencia.contains("4 veces") || frecuencia.contains("c/4h") || 
                frecuencia.contains("qid")) {
                dosisAlDia = 6;
            } else if (frecuencia.contains("cada 6") || frecuencia.contains("cada 6-8") || 
                       frecuencia.contains("4 veces") || frecuencia.contains("c/6h") || 
                       frecuencia.contains("qid")) {
                dosisAlDia = 4;
            } else if (frecuencia.contains("cada 8") || frecuencia.contains("3 veces") || 
                       frecuencia.contains("c/8h") || frecuencia.contains("tid")) {
                dosisAlDia = 3;
            } else if (frecuencia.contains("cada 12") || frecuencia.contains("2 veces") || 
                       frecuencia.contains("c/12h") || frecuencia.contains("bid")) {
                dosisAlDia = 2;
            } else if (frecuencia.contains("cada 24") || frecuencia.contains("1 vez") || 
                       frecuencia.contains("c/24h") || frecuencia.contains("qd")) {
                dosisAlDia = 1;
            }
            
            if (dosisAlDia > 0 && dosisMg * dosisAlDia > 4000) {
                throw new PrescripcionInvalidaException(
                    "La dosis diaria total de paracetamol (" + (dosisMg * dosisAlDia) + 
                    " mg) excede el máximo recomendado para adultos (4000 mg al día)");
            }
        }
    }
    
    /**
     * Valida la dosis de ibuprofeno según edad
     * @param prescripcion La prescripción a validar
     * @param edadAnios Edad del paciente en años
     * @throws PrescripcionInvalidaException Si la dosis no es adecuada
     */
    private void validarDosisIbuprofeno(Prescripcion prescripcion, int edadAnios) {
        // Implementación similar a validarDosisParacetamol
        // Reglas específicas para ibuprofeno
        
        // Extraer la dosis numérica
        String dosisTxt = prescripcion.getDosis().replaceAll("[^0-9]", "");
        if (dosisTxt.isEmpty()) {
            log.warn("No se puede validar dosis de ibuprofeno: formato de dosis no reconocido");
            return;
        }
        
        int dosisMg;
        try {
            dosisMg = Integer.parseInt(dosisTxt);
        } catch (NumberFormatException e) {
            log.warn("No se puede validar dosis de ibuprofeno: error al parsear dosis");
            return;
        }
        
        // Validar según edad
        if (edadAnios < 6) {
            throw new PrescripcionInvalidaException(
                "El ibuprofeno no está recomendado para niños menores de 6 años sin supervisión especializada");
        } else if (edadAnios < 12) {
            // Niños: validación básica por edad
            if (dosisMg > 200) {
                throw new PrescripcionInvalidaException(
                    "La dosis de ibuprofeno excede el máximo recomendado para niños de 6-11 años (200 mg)");
            }
        } else {
            // Adultos: máximo 800 mg por dosis, máximo 3200 mg al día
            if (dosisMg > 800) {
                throw new PrescripcionInvalidaException(
                    "La dosis de ibuprofeno excede el máximo recomendado para adultos (800 mg por dosis)");
            }
        }
    }
    
    /**
     * Valida la dosis de antibióticos según edad
     * @param prescripcion La prescripción a validar
     * @param edadAnios Edad del paciente en años
     * @throws PrescripcionInvalidaException Si la dosis no es adecuada
     */
    private void validarDosisAntibiotico(Prescripcion prescripcion, int edadAnios) {
        // Implementación básica para antibióticos
        // En un sistema real, esto sería mucho más complejo y específico para cada antibiótico
        
        if (edadAnios < 12) {
            log.warn("Se recomienda validación especializada de dosis de antibiótico para niños");
        }
        
        // Validar duración del tratamiento para antibióticos
        if (prescripcion.getDuracionDias() < 5) {
            throw new PrescripcionInvalidaException(
                "La duración del tratamiento antibiótico es demasiado corta. " +
                "Se recomienda un mínimo de 5 días para la mayoría de infecciones.");
        }
        
        if (prescripcion.getDuracionDias() > 14 && 
            prescripcion.getJustificacionClinica() == null) {
            throw new PrescripcionInvalidaException(
                "Los tratamientos antibióticos de más de 14 días requieren justificación clínica.");
        }
    }
    
    /**
     * Valida la dosis de medicamentos controlados según edad
     * @param prescripcion La prescripción a validar
     * @param edadAnios Edad del paciente en años
     * @throws PrescripcionInvalidaException Si la dosis no es adecuada
     */
    private void validarDosisMedicamentoControlado(Prescripcion prescripcion, int edadAnios) {
        // Validaciones específicas para medicamentos controlados
        
        // Verificar si el medicamento es apropiado para la edad
        if (edadAnios < 18) {
            // Verificar si es un medicamento controlado no recomendado para menores
            String medicamento = prescripcion.getMedicamento().toLowerCase();
            if (medicamento.contains("alprazolam") || 
                medicamento.contains("diazepam") || 
                medicamento.contains("clonazepam") || 
                medicamento.contains("tramadol") || 
                medicamento.contains("morfina") || 
                medicamento.contains("fentanilo")) {
                
                if (prescripcion.getJustificacionClinica() == null || 
                    prescripcion.getJustificacionClinica().isEmpty()) {
                    throw new PrescripcionInvalidaException(
                        "El medicamento " + prescripcion.getMedicamento() + 
                        " requiere justificación clínica para pacientes menores de 18 años");
                }
            }
        }
        
        // Verificar duración del tratamiento
        if (prescripcion.getDuracionDias() > 30) {
            throw new PrescripcionInvalidaException(
                "La duración del tratamiento con medicamentos controlados no debe exceder los 30 días");
        }
    }
    
    /**
     * Valida contraindicaciones según datos del paciente
     * @param prescripcion La prescripción a validar
     * @param datosPaciente Datos del paciente
     * @return Lista de contraindicaciones encontradas
     */
    private List<String> validarContraindicaciones(Prescripcion prescripcion, DatosPaciente datosPaciente) {
        List<String> contraindicaciones = new ArrayList<>();
        
        if (datosPaciente == null) {
            return contraindicaciones;
        }
        
        String medicamento = prescripcion.getMedicamento().toLowerCase();
        int edadAnios = 0;
        
        if (datosPaciente.getFechaNacimiento() != null) {
            edadAnios = Period.between(datosPaciente.getFechaNacimiento(), LocalDate.now()).getYears();
        }
        
        // Contraindicaciones por edad
        if (edadAnios < 12 && medicamento.contains("aspirina")) {
            contraindicaciones.add("La aspirina está contraindicada en niños menores de 12 años " +
                                  "por riesgo de síndrome de Reye");
        }
        
        // Contraindicaciones por sexo (simplificado ya que no tenemos información de embarazo)
        if (datosPaciente.getSexo() != null && 
            datosPaciente.getSexo() == com.ug.ec.domain.consultaexterna.enums.Sexo.FEMENINO) {
            
            // Nota: En un sistema real, se debería verificar si la paciente está embarazada
            // Aquí solo mostramos advertencias generales para medicamentos que podrían estar
            // contraindicados en mujeres embarazadas
            
            if (edadAnios >= 15 && edadAnios <= 50) {
                // Edad fértil - mostrar advertencias
                if (medicamento.contains("ibuprofeno") || 
                    medicamento.contains("naproxeno") || 
                    medicamento.contains("diclofenaco")) {
                    contraindicaciones.add("Los AINEs como " + prescripcion.getMedicamento() + 
                                          " pueden estar contraindicados en mujeres embarazadas, especialmente " +
                                          "en el tercer trimestre. Verificar estado de embarazo.");
                }
                
                if (medicamento.contains("warfarina") || 
                    medicamento.contains("acenocumarol")) {
                    contraindicaciones.add("Los anticoagulantes orales como " + prescripcion.getMedicamento() + 
                                          " pueden estar contraindicados durante el embarazo. Verificar estado de embarazo.");
                }
                
                if (medicamento.contains("atorvastatina") || 
                    medicamento.contains("simvastatina") || 
                    medicamento.contains("rosuvastatina")) {
                    contraindicaciones.add("Las estatinas como " + prescripcion.getMedicamento() + 
                                          " pueden estar contraindicadas durante el embarazo. Verificar estado de embarazo.");
                }
            }
        }
        
        return contraindicaciones;
    }
    
    /**
     * Valida la duración del tratamiento
     * @param prescripcion La prescripción a validar
     * @throws PrescripcionInvalidaException Si la duración no es adecuada
     */
    private void validarDuracionTratamiento(Prescripcion prescripcion) {
        // Validar que la duración sea positiva
        if (prescripcion.getDuracionDias() <= 0) {
            throw new PrescripcionInvalidaException(
                "La duración del tratamiento debe ser mayor a cero días");
        }
        
        // Validar duración máxima según tipo de medicamento
        String medicamento = prescripcion.getMedicamento().toLowerCase();
        
        // Corticoides
        if (medicamento.contains("prednisona") || 
            medicamento.contains("prednisolona") || 
            medicamento.contains("dexametasona") || 
            medicamento.contains("betametasona")) {
            
            if (prescripcion.getDuracionDias() > 14 && 
                (prescripcion.getJustificacionClinica() == null || 
                 prescripcion.getJustificacionClinica().isEmpty())) {
                throw new PrescripcionInvalidaException(
                    "Los tratamientos con corticoides de más de 14 días requieren justificación clínica");
            }
        }
        
        // Antibióticos
        if (prescripcion.esAntibiotico()) {
            if (prescripcion.getDuracionDias() < 3) {
                throw new PrescripcionInvalidaException(
                    "La duración mínima recomendada para antibióticos es de 3 días");
            }
        }
        
        // Medicamentos controlados
        if (prescripcion.esControlada()) {
            if (prescripcion.getDuracionDias() > 30) {
                throw new PrescripcionInvalidaException(
                    "La duración máxima para medicamentos controlados es de 30 días");
            }
        }
    }
    
    /**
     * Valida campos obligatorios según el tipo de medicamento
     * @param prescripcion La prescripción a validar
     * @throws PrescripcionInvalidaException Si faltan campos obligatorios
     */
    private void validarCamposObligatoriosPorTipoMedicamento(Prescripcion prescripcion) {
        // Validar campos obligatorios para medicamentos controlados
        if (prescripcion.esControlada()) {
            if (prescripcion.getJustificacionClinica() == null || 
                prescripcion.getJustificacionClinica().isEmpty()) {
                throw new PrescripcionInvalidaException(
                    "La justificación clínica es obligatoria para medicamentos controlados");
            }
            
            if (Boolean.TRUE.equals(prescripcion.getRequiereRecetaEspecial()) && 
                (prescripcion.getConcentracion() == null || prescripcion.getConcentracion().isEmpty())) {
                throw new PrescripcionInvalidaException(
                    "La concentración es obligatoria para medicamentos que requieren receta especial");
            }
        }
        
        // Validar campos obligatorios para antibióticos
        if (prescripcion.esAntibiotico()) {
            if (prescripcion.getViaAdministracion() == null || 
                prescripcion.getViaAdministracion().isEmpty()) {
                throw new PrescripcionInvalidaException(
                    "La vía de administración es obligatoria para antibióticos");
            }
            
            if (prescripcion.getIndicaciones() == null || 
                prescripcion.getIndicaciones().isEmpty()) {
                throw new PrescripcionInvalidaException(
                    "Las indicaciones son obligatorias para antibióticos");
            }
        }
    }
}