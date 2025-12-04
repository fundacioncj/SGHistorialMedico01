package com.ug.ec.infrastructure.persistence.consultaexterna;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.infrastructure.mappers.ConsultaExternaDocumentMapper;
import com.ug.ec.infrastructure.persistence.consultaexterna.ConsultaExternaDocument;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConsultaExternaRepositoryImpl implements ConsultaExternaRepository {
    
    private final MongoConsultaExternaRepository mongoRepository;
    private final ConsultaExternaDocumentMapper mapper;
    
    @Override
    public ConsultaExterna save(ConsultaExterna consulta) {
        ConsultaExternaDocument document = mapper.toDocument(consulta);
        ConsultaExternaDocument savedDocument = mongoRepository.save(document);
        return mapper.toDomain(savedDocument);
    }
    
    @Override
    public Optional<ConsultaExterna> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConsultaExterna> findByCitaId(String id) {
        return mongoRepository.findByCitaId(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
    
    @Override
    public List<ConsultaExterna> findAll() {
        // Excluimos registros eliminados lógicamente
        List<ConsultaExternaDocument> documents = mongoRepository.findByEliminadaNot(true);
        return documents.stream()
                .map(mapper::toDomain)
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
    
    // Implementación de los demás métodos del repositorio...
    
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
            String cedula, LocalDateTime fechaDesde, LocalDateTime fechaHasta, Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByCedulaPacienteAndDatosConsultaFechaConsultaBetween(
                cedula, fechaDesde, fechaHasta, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosPacienteNumeroHistoriaClinica(String numeroHistoriaClinica, Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosPacienteNumeroHistoriaClinica(
                numeroHistoriaClinica, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosConsultaMedicoTratante(String medicoTratante, Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosConsultaMedicoTratante(
                medicoTratante, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosFormularioCodigoEstablecimiento(String codigoEstablecimiento, Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosFormularioCodigoEstablecimiento(
                codigoEstablecimiento, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByDatosConsultaFechaConsultaBetween(
            LocalDateTime fechaDesde, LocalDateTime fechaHasta, Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByDatosConsultaFechaConsultaBetween(
                fechaDesde, fechaHasta, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
    
    @Override
    public Page<ConsultaExterna> findByFiltrosAvanzados(
            LocalDateTime fechaDesde, LocalDateTime fechaHasta, 
            String especialidad, String medicoTratante, 
            EstadoConsulta estado, Pageable pageable) {
        Page<ConsultaExternaDocument> documentPage = mongoRepository.findByFiltrosAvanzados(
                fechaDesde, fechaHasta, especialidad, medicoTratante, estado, pageable);
        List<ConsultaExterna> consultas = documentPage.getContent().stream()
                .map(document -> mapper.toDomain(document))
                .collect(Collectors.toList());
        return new PageImpl<>(consultas, pageable, documentPage.getTotalElements());
    }
}