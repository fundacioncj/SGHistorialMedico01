package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.valueobjects.*;
import com.ug.ec.domain.consultaexterna.enums.*;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaIncompletaException;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaExternaBusinessService {

    private final ValidacionesClinicasService validacionesClinicas;
    private final Counter consultaExternaCreatedCounter;
    private final Counter consultaExternaUpdatedCounter;
    private final Timer consultaExternaProcessingTimer;

    /**
     * Procesa una consulta externa completa siguiendo principios DDD.
     * Retorna una nueva instancia inmutable de ConsultaExterna.
     */
    @Timed(value = "app.consulta_externa.procesamiento", description = "Tiempo de procesamiento de consulta externa")
    public ConsultaExterna procesarConsultaCompleta(ConsultaExterna consultaOriginal) {
        String numeroConsulta = consultaOriginal.obtenerNumeroConsulta();
        log.info("Procesando consulta externa completa: {}", numeroConsulta);

        return consultaExternaProcessingTimer.record(() -> {
            // Validar completitud antes de procesar
            consultaOriginal.validarCompletitud();

            // Aplicar lógica de negocio específica - cada método retorna nueva instancia
            ConsultaExterna consultaProcesada = consultaOriginal;


                consultaProcesada = procesarConsultaSubsecuente(consultaProcesada);
                // Incrementar contador para consultas actualizadas
                consultaExternaUpdatedCounter.increment();


            // Evaluar necesidad de interconsultas


            // Marcar como completada - retorna nueva instancia
            ConsultaExterna consultaFinal = consultaProcesada.completar();

            log.info("Consulta externa procesada exitosamente: {}", numeroConsulta);
            return consultaFinal;
        });
    }

    /**
     * Procesa consulta de primera vez con validaciones específicas
     * Retorna nueva instancia inmutable
     */
    private ConsultaExterna procesarPrimeraConsulta(ConsultaExterna consulta) {
        String cedulaPaciente = consulta.obtenerCedulaPaciente();
        log.debug("Procesando primera consulta para paciente: {}", cedulaPaciente);

        // Validaciones específicas para primera consulta
        validarDatosCompletosPrimeraConsulta(consulta);

        // Evaluaciones médicas automáticas
        SignosVitales signos = obtenerSignosVitales(consulta);
        if (signos != null) {
            evaluarRiesgosIniciales(signos);
        }

        // Retornar consulta procesada (puede incluir modificaciones futuras)
        return consulta; // Por ahora inmutable, sin cambios
    }

    /**
     * Procesa consulta subsecuente comparando con historial
     * Retorna nueva instancia inmutable
     */
    private ConsultaExterna procesarConsultaSubsecuente(ConsultaExterna consulta) {
        String cedulaPaciente = consulta.obtenerCedulaPaciente();
        log.debug("Procesando consulta subsecuente para paciente: {}", cedulaPaciente);

        // Lógica específica para consultas de seguimiento
        validarEvolucionPaciente(consulta);

        // Retornar consulta procesada
        return consulta; // Por ahora inmutable, sin cambios
    }

    /**
     * Obtiene signos vitales de forma segura desde examenFisico
     */
    private SignosVitales obtenerSignosVitales(ConsultaExterna consulta) {
        return consulta.getExamenFisico() != null ? 
            consulta.getExamenFisico().getSignosVitales() : null;
    }

    /**
     * Evalúa automáticamente si se requieren interconsultas basado en criterios médicos
     */
    private boolean evaluarNecesidadInterconsulta(ConsultaExterna consulta) {
        SignosVitales signos = obtenerSignosVitales(consulta);

        if (signos != null && signos.requiereAtencionUrgente()) {
            log.warn("Signos vitales requieren atención urgente para consulta: {}",
                    consulta.obtenerNumeroConsulta());
            return true;
        }

        // Evaluar otros criterios médicos
        if (tieneIndicadoresRiesgoCardiovascular(consulta)) {
            log.info("Detectados indicadores de riesgo cardiovascular");
            return true;
        }

        // Evaluar diagnósticos que requieren interconsulta
        if (diagnosticosRequierenInterconsulta(consulta)) {
            log.info("Diagnósticos requieren interconsulta especializada");
            return true;
        }

        return false;
    }

    /**
     * Evalúa indicadores de riesgo cardiovascular
     */
    private boolean tieneIndicadoresRiesgoCardiovascular(ConsultaExterna consulta) {
        SignosVitales signos = obtenerSignosVitales(consulta);
        if (signos == null) return false;

        return signos.tieneHipertension() ||
                "HIPERTENSION_GRADO_2".equals(signos.clasificarPresion()) ||
                "CRISIS_HIPERTENSIVA".equals(signos.clasificarPresion());
    }

    /**
     * Evalúa si los diagnósticos requieren interconsulta automática
     */
    private boolean diagnosticosRequierenInterconsulta(ConsultaExterna consulta) {
        List<Diagnostico> diagnosticos = consulta.getDiagnosticos();
        if (diagnosticos == null || diagnosticos.isEmpty()) {
            return false;
        }

        // Códigos CIE-10 que requieren interconsulta automática
        List<String> codigosInterconsulta = List.of(
                "I10", "I11", "I12", "I13", "I15", // Hipertensión
                "E10", "E11", "E12", "E13", "E14", // Diabetes
                "N18", "N19", // Enfermedad renal crónica
                "J44", "J45" // EPOC, Asma
        );

        return diagnosticos.stream()
                .anyMatch(diagnostico ->
                        codigosInterconsulta.contains(diagnostico.getCodigoCie10()));
    }

    /**
     * Genera interconsultas automáticas basadas en hallazgos clínicos
     * Retorna nueva instancia de ConsultaExterna
     */


    /**
     * Genera interconsultas basadas en criterios médicos automáticos
     */
    private List<Interconsulta> generarInterconsultasAutomaticas(ConsultaExterna consulta) {
        List<Interconsulta> interconsultas = new ArrayList<>();

        SignosVitales signos = obtenerSignosVitales(consulta);
        if (signos == null) return interconsultas;

        // Crisis hipertensiva → Cardiología urgente
        if ("CRISIS_HIPERTENSIVA".equals(signos.clasificarPresion())) {
            interconsultas.add(
                    Interconsulta.builder()
                            .especialidad("CARDIOLOGIA")
                            .motivo("Crisis hipertensiva - Presión: " +
                                    signos.getPresionSistolica() + "/" + signos.getPresionDiastolica())
                            .prioridad(PrioridadInterconsulta.URGENTE)
                            .estado(EstadoInterconsulta.SOLICITADA)
                            .fechaSolicitud(LocalDateTime.now())
                            .hallazgosRelevantes("Presión arterial crítica requiere evaluación inmediata")
                            .build()
            );
        }

        // Hipoxemia grave → Neumología urgente
        if (signos.tieneHipoxemiaGrave()) {
            interconsultas.add(
                    Interconsulta.builder()
                            .especialidad("NEUMOLOGIA")
                            .motivo("Hipoxemia grave - SatO2: " + signos.getSaturacionOxigeno() + "%")
                            .prioridad(PrioridadInterconsulta.URGENTE)
                            .estado(EstadoInterconsulta.SOLICITADA)
                            .fechaSolicitud(LocalDateTime.now())
                            .hallazgosRelevantes("Saturación de oxígeno crítica")
                            .build()
            );
        }

        // Hipotensión severa → Medicina Interna urgente
        if (signos.tieneHipotensionSevera()) {
            interconsultas.add(
                    Interconsulta.builder()
                            .especialidad("MEDICINA_INTERNA")
                            .motivo("Hipotensión severa - Evaluar shock")
                            .prioridad(PrioridadInterconsulta.URGENTE)
                            .estado(EstadoInterconsulta.SOLICITADA)
                            .fechaSolicitud(LocalDateTime.now())
                            .hallazgosRelevantes("Presión arterial severamente baja")
                            .build()
            );
        }

        // Arritmia severa → Cardiología alta prioridad
        if (signos.getFrecuenciaCardiaca() != null) {
            boolean taquicardiaGrave = signos.getFrecuenciaCardiaca().intValue() > 150;
            boolean bradicardiaGrave = signos.getFrecuenciaCardiaca().intValue() < 40;

            if (taquicardiaGrave || bradicardiaGrave) {
                interconsultas.add(
                        Interconsulta.builder()
                                .especialidad("CARDIOLOGIA")
                                .motivo("Evaluación de arritmia - FC: " + signos.getFrecuenciaCardiaca())
                                .prioridad(PrioridadInterconsulta.ALTA)
                                .estado(EstadoInterconsulta.SOLICITADA)
                                .fechaSolicitud(LocalDateTime.now())
                                .hallazgosRelevantes("Frecuencia cardíaca anormal severa")
                                .build()
                );
            }
        }

        // Fiebre alta → Infectología
        if (signos.getTemperatura() != null &&
                signos.getTemperatura().compareTo(new java.math.BigDecimal("39.5")) > 0) {
            interconsultas.add(
                    Interconsulta.builder()
                            .especialidad("INFECTOLOGIA")
                            .motivo("Fiebre alta - Temperatura: " + signos.getTemperatura() + "°C")
                            .prioridad(PrioridadInterconsulta.ALTA)
                            .estado(EstadoInterconsulta.SOLICITADA)
                            .fechaSolicitud(LocalDateTime.now())
                            .hallazgosRelevantes("Hipertermia significativa")
                            .build()
            );
        }

        log.info("Generadas {} interconsultas automáticas", interconsultas.size());
        return interconsultas;
    }

    /**
     * Valida que los datos estén completos para una primera consulta
     */
    private void validarDatosCompletosPrimeraConsulta(ConsultaExterna consulta) {
        if (consulta.getAnamnesis() == null ||
                consulta.getAnamnesis().getEnfermedadActual() == null ||
                consulta.getAnamnesis().getEnfermedadActual().trim().isEmpty()) {
            throw new ConsultaExternaIncompletaException("Anamnesis es obligatoria en primera consulta");
        }

        if (consulta.getExamenFisico() == null) {
            throw new ConsultaExternaIncompletaException("Examen físico es obligatorio en primera consulta");
        }

        if (consulta.getCedulaPaciente() == null || consulta.getCedulaPaciente().trim().isEmpty()) {
            throw new ConsultaExternaIncompletaException("Datos del paciente son obligatorios");
        }

        SignosVitales signos = obtenerSignosVitales(consulta);
        if (signos == null) {
            throw new ConsultaExternaIncompletaException("Signos vitales son obligatorios en primera consulta");
        }
    }

    /**
     * Evalúa riesgos médicos iniciales
     */
    private void evaluarRiesgosIniciales(SignosVitales signos) {
        List<String> alertas = signos.obtenerAlertas();
        if (!alertas.isEmpty()) {
            log.warn("Alertas médicas detectadas: {}", alertas);
        }

        if (signos.requiereAtencionUrgente()) {
            log.error("⚠️ ATENCIÓN URGENTE REQUERIDA para paciente con signos vitales críticos");
        }

        // Validaciones específicas usando el servicio de validaciones clínicas
        if (validacionesClinicas != null) {
            // Este método podría implementarse para evaluar riesgos específicos
            // validacionesClinicas.evaluarRiesgosVitales(signos);
        }
    }

    /**
     * Valida la evolución del paciente comparando con consultas anteriores
     */
    private void validarEvolucionPaciente(ConsultaExterna consulta) {
        // TODO: Implementar lógica de comparación con historial
        // Esto requeriría acceso al repositorio para consultar historial previo

        String cedulaPaciente = consulta.obtenerCedulaPaciente();
        log.debug("Validando evolución del paciente: {}", cedulaPaciente);

        // Validaciones básicas de evolución
        SignosVitales signosActuales = obtenerSignosVitales(consulta);
        if (signosActuales != null && signosActuales.requiereAtencionUrgente()) {
            log.warn("Deterioro en signos vitales detectado en consulta subsecuente");
        }
    }
}