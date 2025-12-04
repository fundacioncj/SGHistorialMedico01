package com.ug.ec.application.consultaexterna.ports;

import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultaExternaRepository {

    // Operaciones básicas
    ConsultaExterna save(ConsultaExterna consultaExterna);
    Optional<ConsultaExterna> findById(String id);
    Optional<ConsultaExterna> findByCitaId(String id);
    void deleteById(String id);
    List<ConsultaExterna> findAll();

    // Búsquedas específicas
    Optional<ConsultaExterna> findByNumeroConsulta(String numeroConsulta);
    boolean existsByNumeroConsulta(String numeroConsulta);

    // Búsquedas por paciente
    Page<ConsultaExterna> findByDatosPacienteCedula(String cedula, Pageable pageable);
    Page<ConsultaExterna> findByDatosPacienteCedulaAndDatosConsultaFechaConsultaBetween(
            String cedula, 
            LocalDateTime fechaDesde, 
            LocalDateTime fechaHasta, 
            Pageable pageable);

    // Búsquedas por historia clínica
    Page<ConsultaExterna> findByDatosPacienteNumeroHistoriaClinica(String numeroHistoriaClinica, Pageable pageable);

    // Búsquedas por médico
    Page<ConsultaExterna> findByDatosConsultaMedicoTratante(String medicoTratante, Pageable pageable);

    // Búsquedas por establecimiento
    Page<ConsultaExterna> findByDatosFormularioCodigoEstablecimiento(String codigoEstablecimiento, Pageable pageable);

    // Búsquedas por fecha y otros filtros
    Page<ConsultaExterna> findByDatosConsultaFechaConsultaBetween(
            LocalDateTime fechaDesde, 
            LocalDateTime fechaHasta, 
            Pageable pageable);

    Page<ConsultaExterna> findByFiltrosAvanzados(
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            String especialidad,
            String medicoTratante,
            EstadoConsulta estado,
            Pageable pageable);
}
