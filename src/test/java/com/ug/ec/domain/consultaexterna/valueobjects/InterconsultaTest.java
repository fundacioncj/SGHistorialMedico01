package com.ug.ec.domain.consultaexterna.valueobjects;

import com.ug.ec.domain.consultaexterna.enums.EstadoInterconsulta;
import com.ug.ec.domain.consultaexterna.enums.PrioridadInterconsulta;
import com.ug.ec.domain.consultaexterna.exceptions.EstadoInterconsultaInvalidoException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class InterconsultaTest {

    @Disabled
    @Test
    @DisplayName("Debe crear interconsulta urgente correctamente")
    void debeCrearInterconsultaUrgente() {
        // Given
        String especialidad = "Cardiología";
        String motivo = "Dolor torácico";
        
        // When
        Interconsulta interconsulta = Interconsulta.urgente(especialidad, motivo);
        
        // Then
        assertTrue(interconsulta.esUrgente());
        assertEquals(EstadoInterconsulta.SOLICITADA, interconsulta.getEstado());
        assertEquals(PrioridadInterconsulta.URGENTE, interconsulta.getPrioridad());
        assertTrue(interconsulta.requiereAtencionInmediata());
        assertNotNull(interconsulta.getFechaSolicitud());
    }
    @Disabled
    @Test
    @DisplayName("Debe completar interconsulta siguiendo flujo correcto")
    void debeCompletarInterconsultaCorrectamente() {
        // Given
        Interconsulta interconsulta = Interconsulta.urgente("Cardiología", "Dolor torácico")
            .agendar()
            .iniciarProceso();
        
        String respuesta = "Electrocardiograma normal. Descartado síndrome coronario agudo.";
        String medico = "Dr. García";
        
        // When
        Interconsulta completada = interconsulta.completar(respuesta, medico);
        
        // Then
        assertTrue(completada.estaCompletada());
        assertEquals(respuesta, completada.getRespuesta());
        assertEquals(medico, completada.getMedicoInterconsultado());
        assertNotNull(completada.getFechaRespuesta());
        assertFalse(completada.requiereAtencionInmediata());
    }

    @Disabled
    @Test
    @DisplayName("No debe permitir transiciones inválidas de estado")
    void noDebePermitirTransicionesInvalidas() {
        // Given
        Interconsulta interconsulta = Interconsulta.urgente("Cardiología", "Dolor torácico")
            .completar("Respuesta", "Dr. García");
        
        // When & Then
        assertThrows(EstadoInterconsultaInvalidoException.class, () -> {
            interconsulta.agendar();
        });
    }

    @Disabled
    @Test
    @DisplayName("Debe detectar vencimiento de tiempo correctamente")
    void debeDetectarVencimientoTiempo() {
        // Given - Interconsulta urgente con fecha anterior simulada
        Interconsulta interconsulta = Interconsulta.builder()
            .especialidad("Cardiología")
            .motivo("Dolor torácico")
            .prioridad(PrioridadInterconsulta.URGENTE)
            .estado(EstadoInterconsulta.SOLICITADA)
            .fechaSolicitud(java.time.LocalDateTime.now().minusHours(3))
            .build();
        
        // When & Then
        assertTrue(interconsulta.haVencidoTiempo());
        assertTrue(interconsulta.requiereAtencionInmediata());
    }
}