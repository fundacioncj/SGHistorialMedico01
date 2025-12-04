package com.ug.ec.application.consultaexterna.handlers;

import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
import com.ug.ec.application.consultaexterna.dto.ConsultaExternaResumenDto;
import com.ug.ec.application.consultaexterna.mappers.ConsultaExternaMapper;
import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.application.consultaexterna.queries.*;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaConsultaException;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultaExternaQueryHandler {
    
    private final ConsultaExternaRepository consultaExternaRepository;

    private final ConsultaExternaMapper mapper;
    
    public ConsultaExternaDto handle(BuscarConsultaExternaPorIdQuery query) {
        log.info("Buscando consulta externa por ID: {}", query.getId());
        
        try {
            ConsultaExterna consultaExterna = consultaExternaRepository.findById(query.getId())
                    .orElseThrow(() -> new ConsultaExternaNotFoundException(
                        "Consulta externa no encontrada con ID: " + query.getId()));

            ConsultaExternaDto resultado = mapper.entityToDto(consultaExterna);
            log.info("Consulta externa encontrada exitosamente con ID: {}", query.getId());
            
            return resultado;
            
        } catch (Exception e) {
            log.error("Error al buscar consulta externa por ID: {}", e.getMessage(), e);
            throw new ConsultaExternaConsultaException("Error al buscar la consulta externa", e);
        }
    }

    public ConsultaExternaDto handle(BuscarConsultaExternaPorCitaIdQuery query) {
        log.info("Buscando consulta externa por cita ID: {}", query.getId());

        try {
            ConsultaExterna consultaExterna = consultaExternaRepository.findByCitaId(query.getId())
                    .orElseThrow(() -> new ConsultaExternaNotFoundException(
                            "Consulta externa no encontrada con CITA ID: " + query.getId()));

            ConsultaExternaDto resultado = mapper.entityToDto(consultaExterna);
            log.info("Consulta externa encontrada exitosamente con CITA ID: {}", query.getId());

            return resultado;

        } catch (Exception e) {
            log.error("Error al buscar consulta externa por ID: {}", e.getMessage(), e);
            throw new ConsultaExternaConsultaException("Error al buscar la consulta externa", e);
        }
    }
    
    public Page<ConsultaExternaResumenDto> handle(BuscarConsultaExternaPorCedulaQuery query) {
        log.info("Buscando consultas externas por cédula: {}", query.getCedula());
        
        try {
            Pageable pageable = PageRequest.of(
                    query.getPagina(), 
                    query.getTamanio(), 
                    Sort.by("auditoria.fechaCreacion").descending());
            
            Page<ConsultaExterna> consultasPage;
            
            if (query.getFechaDesde() != null && query.getFechaHasta() != null) {
                consultasPage = consultaExternaRepository
                        .findByDatosPacienteCedulaAndDatosConsultaFechaConsultaBetween(
                                query.getCedula(),
                                query.getFechaDesde().atStartOfDay(),
                                query.getFechaHasta().atTime(23, 59, 59),
                                pageable);
            } else {
                consultasPage = consultaExternaRepository
                        .findByDatosPacienteCedula(query.getCedula(), pageable);
            }
            
            Page<ConsultaExternaResumenDto> resultadoPage = consultasPage.map(mapper::entityToResumenDto);
            
            log.info("Encontradas {} consultas externas para cédula: {}", 
                    resultadoPage.getTotalElements(), query.getCedula());
            
            return resultadoPage;
            
        } catch (Exception e) {
            log.error("Error al buscar consultas externas por cédula: {}", e.getMessage(), e);
            throw new ConsultaExternaConsultaException("Error al buscar las consultas externas", e);
        }
    }
    
    public ConsultaExternaDto handle(BuscarConsultaExternaPorNumeroConsultaQuery query) {
        log.info("Buscando consulta externa por número de consulta: {}", query.getNumeroConsulta());
        
        try {
            ConsultaExterna consultaExterna = consultaExternaRepository
                    .findByNumeroConsulta(query.getNumeroConsulta())
                    .orElseThrow(() -> new ConsultaExternaNotFoundException(
                        "Consulta externa no encontrada con número: " + query.getNumeroConsulta()));
            
            ConsultaExternaDto resultado = mapper.entityToDto(consultaExterna);
            
            log.info("Consulta externa encontrada exitosamente con número: {}", query.getNumeroConsulta());
            
            return resultado;
            
        } catch (Exception e) {
            log.error("Error al buscar consulta externa por número: {}", e.getMessage(), e);
            throw new ConsultaExternaConsultaException("Error al buscar la consulta externa", e);
        }
    }
    
    public Page<ConsultaExternaResumenDto> handle(BuscarConsultasExternasPorFechaQuery query) {
        log.info("Buscando consultas externas por fecha desde: {} hasta: {}", 
                query.getFechaDesde(), query.getFechaHasta());
        
        try {
            Pageable pageable = PageRequest.of(
                    query.getPagina(), 
                    query.getTamanio(), 
                    Sort.by("datosConsulta.fechaConsulta").descending());
            
            Page<ConsultaExterna> consultasPage = consultaExternaRepository
                    .findByFiltrosAvanzados(
                            query.getFechaDesde().atStartOfDay(),
                            query.getFechaHasta().atTime(23, 59, 59),
                            query.getEspecialidad(),
                            query.getMedicoTratante(),
                            query.getEstado(),
                            pageable);
            
            Page<ConsultaExternaResumenDto> resultadoPage = consultasPage.map(mapper::entityToResumenDto);
            
            log.info("Encontradas {} consultas externas para el rango de fechas", 
                    resultadoPage.getTotalElements());
            
            return resultadoPage;
            
        } catch (Exception e) {
            log.error("Error al buscar consultas externas por fecha: {}", e.getMessage(), e);
            throw new ConsultaExternaConsultaException("Error al buscar las consultas externas", e);
        }
    }
}