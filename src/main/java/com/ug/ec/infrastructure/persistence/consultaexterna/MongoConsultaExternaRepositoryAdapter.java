package com.ug.ec.infrastructure.persistence.consultaexterna;

import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.infrastructure.mappers.ConsultaExternaDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el port ConsultaExternaRepository
 * delegando las operaciones al MongoConsultaExternaRepository.
 * 
 * Este patrón evita conflictos de herencia múltiple y mantiene
 * la separación clara entre puertos y adaptadores.
 */
@Component
@Primary
@RequiredArgsConstructor
public class MongoConsultaExternaRepositoryAdapter implements ConsultaExternaRepository {
    
    private final MongoConsultaExternaRepository mongoRepository;
    private final ConsultaExternaDocumentMapper mapper;
    
    @Override
    public ConsultaExterna save(ConsultaExterna consultaExterna) {
        ConsultaExternaDocument document = mapper.toDocument(consultaExterna);
        ConsultaExternaDocument savedDocument = mongoRepository.save(document);
        return mapper.toDomain(savedDocument);
    }
    
    @Override
    public Optional<ConsultaExterna> findById(String id) {
        return mongoRepository.findById(id)
                .map(document -> mapper.toDomain(document));
    }

    @Override
    public Optional<ConsultaExterna> findByCitaId(String id) {
        return mongoRepository.findByCitaId(id)
                .map(document -> mapper.toDomain(document));
    }
    
    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
    
    @Override
    public List<ConsultaExterna> findAll() {
        return mongoRepository.findAll().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ConsultaExterna> findByNumeroConsulta(String numeroConsulta) {
        return mongoRepository.findByNumeroConsulta(numeroConsulta)
                .map(document -> mapper.toDomain(document));
    }
    
    @Override
    public boolean existsByNumeroConsulta(String numeroConsulta) {
        return mongoRepository.existsByNumeroConsulta(numeroConsulta);
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosPacienteCedula(String cedula, Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosPacienteCedula(cedula, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosPacienteCedulaAndDatosConsultaFechaConsultaBetween(
            String cedula, 
            LocalDateTime fechaDesde, 
            LocalDateTime fechaHasta, 
            Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByCedulaPacienteAndDatosConsultaFechaConsultaBetween(
                cedula, fechaDesde, fechaHasta, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosPacienteNumeroHistoriaClinica(
            String numeroHistoriaClinica, 
            Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosPacienteNumeroHistoriaClinica(
                numeroHistoriaClinica, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosConsultaMedicoTratante(
            String medicoTratante, 
            Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosConsultaMedicoTratante(
                medicoTratante, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosFormularioCodigoEstablecimiento(
            String codigoEstablecimiento, 
            Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosFormularioCodigoEstablecimiento(
                codigoEstablecimiento, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosConsultaFechaConsultaBetween(
            LocalDateTime fechaDesde, 
            LocalDateTime fechaHasta, 
            Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosConsultaFechaConsultaBetween(
                fechaDesde, fechaHasta, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByFiltrosAvanzados(
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            String especialidad,
            String medicoTratante,
            EstadoConsulta estado,
            Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByFiltrosAvanzados(
                fechaDesde, fechaHasta, especialidad, medicoTratante, estado, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
}
