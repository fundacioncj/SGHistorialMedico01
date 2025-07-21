package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.valueobjects.*;
import com.ug.ec.domain.consultaexterna.enums.*;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servicio para validar los campos del formulario 002 CONSULTA EXTERNA 2021
 * Implementa reglas de validación específicas para cada sección del formulario
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FormularioConsultaExternaValidationService {
    
    private final DiagnosticoTratamientoValidationService diagnosticoTratamientoValidationService;
    private final ValidacionesClinicasService validacionesClinicasService;
    
    // Patrones para validación de campos
    private static final Pattern PATRON_CEDULA = Pattern.compile("^[0-9]{10}$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^[0-9]{7,10}$");
    private static final Pattern PATRON_EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    
    /**
     * Valida completamente una consulta externa según las reglas del formulario 002
     * @param consulta Consulta externa a validar
     * @return Lista de errores y advertencias encontrados
     */
    public List<String> validarConsultaExterna(ConsultaExterna consulta) {
        List<String> errores = new ArrayList<>();
        
        if (consulta == null) {
            errores.add("No se puede validar una consulta nula");
            return errores;
        }
        
        // Validar secciones del formulario
        errores.addAll(validarDatosFormulario(consulta.getDatosFormulario()));
        errores.addAll(validarDatosPaciente(consulta.getDatosPaciente()));
        errores.addAll(validarDatosConsulta(consulta.getDatosConsulta()));
        errores.addAll(validarAnamnesis(consulta.getAnamnesis()));
        errores.addAll(validarExamenFisico(consulta.getExamenFisico(), consulta.getDatosPaciente()));
        errores.addAll(validarDiagnosticos(consulta.getDiagnosticos()));
        errores.addAll(validarPlanTratamiento(consulta.getPlanTratamiento()));
        
        // Validar coherencia entre diagnósticos y tratamientos
        errores.addAll(diagnosticoTratamientoValidationService.validarCoherenciaDiagnosticoTratamiento(consulta));
        
        return errores;
    }
    
    /**
     * Valida los datos del formulario
     * @param datosFormulario Datos del formulario a validar
     * @return Lista de errores encontrados
     */
    private List<String> validarDatosFormulario(DatosFormulario datosFormulario) {
        List<String> errores = new ArrayList<>();
        
        if (datosFormulario == null) {
            errores.add("Los datos del formulario son obligatorios");
            return errores;
        }
        
        if (datosFormulario.getCodigoEstablecimiento() == null || datosFormulario.getCodigoEstablecimiento().trim().isEmpty()) {
            errores.add("El código del establecimiento es obligatorio");
        }
        
        if (datosFormulario.getEstablecimiento() == null || datosFormulario.getEstablecimiento().trim().isEmpty()) {
            errores.add("El nombre del establecimiento es obligatorio");
        }
        
        return errores;
    }
    
    /**
     * Valida los datos del paciente
     * @param datosPaciente Datos del paciente a validar
     * @return Lista de errores encontrados
     */
    private List<String> validarDatosPaciente(DatosPaciente datosPaciente) {
        List<String> errores = new ArrayList<>();
        
        if (datosPaciente == null) {
            errores.add("Los datos del paciente son obligatorios");
            return errores;
        }
        
        // Validar campos obligatorios
        if (datosPaciente.getPrimerNombre() == null || datosPaciente.getPrimerNombre().trim().isEmpty()) {
            errores.add("El primer nombre del paciente es obligatorio");
        }
        
        if (datosPaciente.getApellidoPaterno() == null || datosPaciente.getApellidoPaterno().trim().isEmpty()) {
            errores.add("El apellido paterno del paciente es obligatorio");
        }
        
        if (datosPaciente.getCedula() == null || datosPaciente.getCedula().trim().isEmpty()) {
            errores.add("La cédula del paciente es obligatoria");
        } else if (!PATRON_CEDULA.matcher(datosPaciente.getCedula().trim()).matches()) {
            errores.add("La cédula del paciente debe tener 10 dígitos numéricos");
        }
        
        if (datosPaciente.getFechaNacimiento() == null) {
            errores.add("La fecha de nacimiento del paciente es obligatoria");
        } else {
            // Validar que la fecha de nacimiento no sea futura
            if (datosPaciente.getFechaNacimiento().isAfter(LocalDate.now())) {
                errores.add("La fecha de nacimiento no puede ser futura");
            }
            
            // Validar edad máxima razonable (150 años)
            int edad = Period.between(datosPaciente.getFechaNacimiento(), LocalDate.now()).getYears();
            if (edad > 150) {
                errores.add("La edad del paciente excede el límite razonable (150 años)");
            }
        }
        
        if (datosPaciente.getSexo() == null) {
            errores.add("El sexo del paciente es obligatorio");
        }
        
        // Validar campos opcionales con formato específico
        if (datosPaciente.getTelefono() != null && !datosPaciente.getTelefono().trim().isEmpty() && 
                !PATRON_TELEFONO.matcher(datosPaciente.getTelefono().trim()).matches()) {
            errores.add("El formato del teléfono es inválido (debe tener entre 7 y 10 dígitos numéricos)");
        }
        
        if (datosPaciente.getEmail() != null && !datosPaciente.getEmail().trim().isEmpty() && 
                !PATRON_EMAIL.matcher(datosPaciente.getEmail().trim()).matches()) {
            errores.add("El formato del email es inválido");
        }
        
        return errores;
    }
    
    /**
     * Valida los datos de la consulta
     * @param datosConsulta Datos de la consulta a validar
     * @return Lista de errores encontrados
     */
    private List<String> validarDatosConsulta(DatosConsulta datosConsulta) {
        List<String> errores = new ArrayList<>();
        
        if (datosConsulta == null) {
            errores.add("Los datos de la consulta son obligatorios");
            return errores;
        }
        
        // Validar campos obligatorios
        if (datosConsulta.getNumeroConsulta() == null || datosConsulta.getNumeroConsulta().trim().isEmpty()) {
            errores.add("El número de consulta es obligatorio");
        }
        
        if (datosConsulta.getFechaConsulta() == null) {
            errores.add("La fecha de consulta es obligatoria");
        } else {
            // Validar que la fecha de consulta no sea futura
            if (datosConsulta.getFechaConsulta().isAfter(LocalDateTime.now())) {
                errores.add("La fecha de consulta no puede ser futura");
            }
            
            // Validar que la fecha de consulta no sea muy antigua (más de 1 año)
            if (datosConsulta.getFechaConsulta().isBefore(LocalDateTime.now().minusYears(1))) {
                errores.add("La fecha de consulta es demasiado antigua (más de 1 año)");
            }
        }
        
        if (datosConsulta.getMedicoTratante() == null || datosConsulta.getMedicoTratante().trim().isEmpty()) {
            errores.add("El médico tratante es obligatorio");
        }
        
        if (datosConsulta.getEspecialidad() == null || datosConsulta.getEspecialidad().trim().isEmpty()) {
            errores.add("La especialidad es obligatoria");
        }
        
        if (datosConsulta.getTipoConsulta() == null) {
            errores.add("El tipo de consulta es obligatorio");
        }
        
        return errores;
    }
    
    /**
     * Valida la anamnesis
     * @param anamnesis Anamnesis a validar
     * @return Lista de errores encontrados
     */
    private List<String> validarAnamnesis(Anamnesis anamnesis) {
        List<String> errores = new ArrayList<>();
        
        if (anamnesis == null) {
            errores.add("La anamnesis es obligatoria");
            return errores;
        }
        
        // No hay validación para motivo de consulta ya que no existe en la clase Anamnesis
        
        if (anamnesis.getEnfermedadActual() == null || anamnesis.getEnfermedadActual().trim().isEmpty()) {
            errores.add("La enfermedad actual es obligatoria");
        } else if (anamnesis.getEnfermedadActual().trim().length() < 10) {
            errores.add("La descripción de la enfermedad actual debe tener al menos 10 caracteres");
        }
        
        return errores;
    }
    
    /**
     * Valida el examen físico
     * @param examenFisico Examen físico a validar
     * @param datosPaciente Datos del paciente para validaciones contextuales
     * @return Lista de errores encontrados
     */
    private List<String> validarExamenFisico(ExamenFisico examenFisico, DatosPaciente datosPaciente) {
        List<String> errores = new ArrayList<>();
        
        if (examenFisico == null) {
            errores.add("El examen físico es obligatorio");
            return errores;
        }
        
        // Validar signos vitales
        SignosVitales signos = examenFisico.getSignosVitales();
        if (signos == null) {
            errores.add("Los signos vitales son obligatorios");
        } else {
            // Validar campos obligatorios de signos vitales
            if (signos.getPresionArterial() == null || signos.getPresionArterial().trim().isEmpty()) {
                errores.add("La presión arterial es obligatoria");
            } else if (!signos.getPresionArterial().contains("/")) {
                errores.add("El formato de la presión arterial es inválido (debe ser sistólica/diastólica)");
            }
            
            if (signos.getFrecuenciaCardiaca() == null) {
                errores.add("La frecuencia cardíaca es obligatoria");
            }
            
            if (signos.getFrecuenciaRespiratoria() == null) {
                errores.add("La frecuencia respiratoria es obligatoria");
            }
            
            if (signos.getTemperatura() == null) {
                errores.add("La temperatura es obligatoria");
            }
            
            // Validar signos vitales según la edad del paciente
            if (datosPaciente != null && datosPaciente.getFechaNacimiento() != null) {
                List<String> alertasSignosVitales = validacionesClinicasService.validarSignosVitalesPorEdad(
                        signos, datosPaciente.getFechaNacimiento());
                
                if (!alertasSignosVitales.isEmpty()) {
                    errores.addAll(alertasSignosVitales);
                }
            }
        }
        
        // Validar medidas antropométricas
        MedidasAntropometricas medidas = examenFisico.getMedidasAntropometricas();
        if (medidas == null) {
            errores.add("Las medidas antropométricas son obligatorias");
        } else {
            // Validar campos obligatorios de medidas antropométricas
            if (medidas.getPeso() == null) {
                errores.add("El peso es obligatorio");
            }
            
            if (medidas.getTalla() == null) {
                errores.add("La talla es obligatoria");
            }
            
            // Validar coherencia de medidas antropométricas
            if (medidas.getPeso() != null && medidas.getTalla() != null) {
                if (medidas.getPeso().doubleValue() <= 0) {
                    errores.add("El peso debe ser mayor a 0");
                }
                
                if (medidas.getTalla().doubleValue() <= 0) {
                    errores.add("La talla debe ser mayor a 0");
                }
                
                // Validar IMC en rango razonable (10-100)
                if (medidas.calcularIMC() != null) {
                    double imc = medidas.calcularIMC().doubleValue();
                    if (imc < 10 || imc > 100) {
                        errores.add("El IMC calculado está fuera del rango razonable (10-100): " + imc);
                    }
                }
            }
        }
        
        // Validar examen físico regional
        List<ExamenFisicoRegional> examenesRegionales = examenFisico.getExamenesRegionales();
        if (examenesRegionales == null || examenesRegionales.isEmpty()) {
            errores.add("Debe incluir al menos un examen físico regional");
        } else {
            // Verificar que al menos un examen regional tenga información completa
            boolean algunExamenCompleto = examenesRegionales.stream()
                .anyMatch(regional -> 
                    regional.getRegion() != null && !regional.getRegion().trim().isEmpty());
            
            if (!algunExamenCompleto) {
                errores.add("Debe completar al menos un campo del examen físico regional");
            }
        }
        
        return errores;
    }
    
    /**
     * Valida los diagnósticos
     * @param diagnosticos Lista de diagnósticos a validar
     * @return Lista de errores encontrados
     */
    private List<String> validarDiagnosticos(List<Diagnostico> diagnosticos) {
        List<String> errores = new ArrayList<>();
        
        if (diagnosticos == null || diagnosticos.isEmpty()) {
            errores.add("Debe registrar al menos un diagnóstico");
            return errores;
        }
        
        boolean tieneDiagnosticoPrincipal = false;
        
        for (int i = 0; i < diagnosticos.size(); i++) {
            Diagnostico diagnostico = diagnosticos.get(i);
            
            // Validar campos obligatorios
            if (diagnostico.getDescripcion() == null || diagnostico.getDescripcion().trim().isEmpty()) {
                errores.add("La descripción del diagnóstico " + (i + 1) + " es obligatoria");
            }
            
            if (diagnostico.getCodigoCie10() == null || diagnostico.getCodigoCie10().trim().isEmpty()) {
                errores.add("El código CIE-10 del diagnóstico " + (i + 1) + " es obligatorio");
            }
            
            if (diagnostico.getTipo() == null) {
                errores.add("El tipo del diagnóstico " + (i + 1) + " es obligatorio");
            } else if (TipoDiagnostico.PRINCIPAL.equals(diagnostico.getTipo())) {
                tieneDiagnosticoPrincipal = true;
            }
        }
        
        // Validar que exista al menos un diagnóstico principal
        if (!tieneDiagnosticoPrincipal) {
            errores.add("Debe registrar al menos un diagnóstico principal");
        }
        
        return errores;
    }
    
    /**
     * Valida el plan de tratamiento
     * @param planTratamiento Plan de tratamiento a validar
     * @return Lista de errores encontrados
     */
    private List<String> validarPlanTratamiento(PlanTratamiento planTratamiento) {
        List<String> errores = new ArrayList<>();
        
        if (planTratamiento == null) {
            errores.add("El plan de tratamiento es obligatorio");
            return errores;
        }
        
        // Validar prescripciones
        List<Prescripcion> prescripciones = planTratamiento.getPrescripciones();
        if (prescripciones == null || prescripciones.isEmpty()) {
            errores.add("Debe registrar al menos una prescripción");
        } else {
            for (int i = 0; i < prescripciones.size(); i++) {
                Prescripcion prescripcion = prescripciones.get(i);
                
                // Validar campos obligatorios
                if (prescripcion.getMedicamento() == null || prescripcion.getMedicamento().trim().isEmpty()) {
                    errores.add("El medicamento de la prescripción " + (i + 1) + " es obligatorio");
                }
                
                if (prescripcion.getDosis() == null || prescripcion.getDosis().trim().isEmpty()) {
                    errores.add("La dosis de la prescripción " + (i + 1) + " es obligatoria");
                }
                
                if (prescripcion.getFrecuencia() == null || prescripcion.getFrecuencia().trim().isEmpty()) {
                    errores.add("La frecuencia de la prescripción " + (i + 1) + " es obligatoria");
                }
                
                if (prescripcion.getViaAdministracion() == null || prescripcion.getViaAdministracion().trim().isEmpty()) {
                    errores.add("La vía de administración de la prescripción " + (i + 1) + " es obligatoria");
                }
                
                if (prescripcion.getDuracionDias() == null || prescripcion.getDuracionDias() <= 0) {
                    errores.add("La duración de la prescripción " + (i + 1) + " debe ser mayor a 0 días");
                }
            }
        }
        
        // Validar indicaciones generales
        if (planTratamiento.getIndicacionesGenerales() == null || planTratamiento.getIndicacionesGenerales().isEmpty()) {
            errores.add("Debe registrar al menos una indicación general");
        }
        
        return errores;
    }
    
    /**
     * Verifica si una consulta externa está completa según las reglas del formulario 002
     * @param consulta Consulta externa a verificar
     * @return true si la consulta está completa, false en caso contrario
     */
    public boolean esConsultaCompleta(ConsultaExterna consulta) {
        List<String> errores = validarConsultaExterna(consulta);
        return errores.isEmpty();
    }
}