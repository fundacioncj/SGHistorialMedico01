package com.ug.ec.infrastructure.persistence.consultaexterna;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.infrastructure.persistence.consultaexterna.dto.ConsultaExternaResumenDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio MongoDB específico para ConsultaExternaDocument.
 * Extiende solo MongoRepository para evitar conflictos de métodos.
 * 
 * La implementación del port ConsultaExternaRepository se realizará
 * mediante un adaptador separado.
 * 
 * Se han añadido métodos con proyecciones para optimizar las consultas
 * más frecuentes y reducir la cantidad de datos transferidos.
 */
@Repository
public interface MongoConsultaExternaRepository extends 
    MongoRepository<ConsultaExternaDocument, String>, 
    MongoConsultaExternaRepositoryCustom {

    // Búsquedas específicas de MongoDB
    Optional<ConsultaExternaDocument> findByNumeroConsulta(String numeroConsulta);

    boolean existsByNumeroConsulta(String numeroConsulta);

    // Métodos originales (sin proyección)
    Page<ConsultaExternaDocument> findByDatosPacienteCedula(String cedula, Pageable pageable);

    Page<ConsultaExternaDocument> findByDatosPacienteCedulaAndDatosConsultaFechaConsultaBetween(
            String cedula, 
            LocalDateTime fechaDesde, 
            LocalDateTime fechaHasta, 
            Pageable pageable);

    Page<ConsultaExternaDocument> findByDatosPacienteNumeroHistoriaClinica(
            String numeroHistoriaClinica, 
            Pageable pageable);

    Page<ConsultaExternaDocument> findByDatosConsultaMedicoTratante(
            String medicoTratante, 
            Pageable pageable);

    Page<ConsultaExternaDocument> findByDatosFormularioCodigoEstablecimiento(
            String codigoEstablecimiento, 
            Pageable pageable);

    Page<ConsultaExternaDocument> findByDatosConsultaFechaConsultaBetween(
            LocalDateTime fechaDesde, 
            LocalDateTime fechaHasta, 
            Pageable pageable);
            
    // ========== MÉTODOS CON PROYECCIÓN ==========
    
    /**
     * Busca consultas por cédula del paciente con proyección para resumen
     */
    @Query(value = "{'datosPaciente.cedula': ?0}", 
           fields = "{'id': 1, 'numeroConsulta': 1, 'datosPaciente.cedula': 1, 'datosPaciente.nombre': 1, " +
                    "'datosPaciente.numeroHistoriaClinica': 1, 'datosConsulta.fechaConsulta': 1, " +
                    "'datosConsulta.medicoTratante': 1, 'datosConsulta.especialidad': 1, " +
                    "'datosConsulta.tipoConsulta': 1, 'estado': 1, 'auditoria.fechaCreacion': 1, " +
                    "'auditoria.fechaModificacion': 1}")
    Page<ConsultaExternaDocument> findResumenByDatosPacienteCedula(String cedula, Pageable pageable);
    
    /**
     * Busca consultas por número de historia clínica con proyección para resumen
     */
    @Query(value = "{'datosPaciente.numeroHistoriaClinica': ?0}", 
           fields = "{'id': 1, 'numeroConsulta': 1, 'datosPaciente.cedula': 1, 'datosPaciente.nombre': 1, " +
                    "'datosPaciente.numeroHistoriaClinica': 1, 'datosConsulta.fechaConsulta': 1, " +
                    "'datosConsulta.medicoTratante': 1, 'datosConsulta.especialidad': 1, " +
                    "'datosConsulta.tipoConsulta': 1, 'estado': 1, 'auditoria.fechaCreacion': 1, " +
                    "'auditoria.fechaModificacion': 1}")
    Page<ConsultaExternaDocument> findResumenByDatosPacienteNumeroHistoriaClinica(
            String numeroHistoriaClinica, Pageable pageable);
    
    /**
     * Busca consultas por rango de fechas con proyección para resumen
     */
    @Query(value = "{'datosConsulta.fechaConsulta': {$gte: ?0, $lte: ?1}}", 
           fields = "{'id': 1, 'numeroConsulta': 1, 'datosPaciente.cedula': 1, 'datosPaciente.nombre': 1, " +
                    "'datosPaciente.numeroHistoriaClinica': 1, 'datosConsulta.fechaConsulta': 1, " +
                    "'datosConsulta.medicoTratante': 1, 'datosConsulta.especialidad': 1, " +
                    "'datosConsulta.tipoConsulta': 1, 'estado': 1, 'auditoria.fechaCreacion': 1, " +
                    "'auditoria.fechaModificacion': 1}")
    Page<ConsultaExternaDocument> findResumenByDatosConsultaFechaConsultaBetween(
            LocalDateTime fechaDesde, LocalDateTime fechaHasta, Pageable pageable);
            
    /**
     * Busca consultas por médico tratante con proyección para resumen
     */
    @Query(value = "{'datosConsulta.medicoTratante': ?0}", 
           fields = "{'id': 1, 'numeroConsulta': 1, 'datosPaciente.cedula': 1, 'datosPaciente.nombre': 1, " +
                    "'datosPaciente.numeroHistoriaClinica': 1, 'datosConsulta.fechaConsulta': 1, " +
                    "'datosConsulta.medicoTratante': 1, 'datosConsulta.especialidad': 1, " +
                    "'datosConsulta.tipoConsulta': 1, 'estado': 1, 'auditoria.fechaCreacion': 1, " +
                    "'auditoria.fechaModificacion': 1}")
    Page<ConsultaExternaDocument> findResumenByDatosConsultaMedicoTratante(
            String medicoTratante, Pageable pageable);
}