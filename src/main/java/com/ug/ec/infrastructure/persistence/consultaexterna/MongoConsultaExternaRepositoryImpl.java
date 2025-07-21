package com.ug.ec.infrastructure.persistence.consultaexterna;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MongoConsultaExternaRepositoryImpl implements MongoConsultaExternaRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    public MongoConsultaExternaRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public Page<ConsultaExternaDocument> findByFiltrosAvanzados(
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            String especialidad,
            String medicoTratante,
            EstadoConsulta estado,
            Pageable pageable) {
        
        Query query = new Query();
        
        // Criterios de búsqueda
        if (fechaDesde != null && fechaHasta != null) {
            query.addCriteria(Criteria.where("datosConsulta.fechaConsulta")
                    .gte(fechaDesde).lte(fechaHasta));
        }
        
        if (especialidad != null && !especialidad.isEmpty()) {
            query.addCriteria(Criteria.where("datosConsulta.especialidad").is(especialidad));
        }
        
        if (medicoTratante != null && !medicoTratante.isEmpty()) {
            query.addCriteria(Criteria.where("datosConsulta.medicoTratante").is(medicoTratante));
        }
        
        if (estado != null) {
            query.addCriteria(Criteria.where("estado").is(estado));
        }
        
        // Aplicar paginación
        query.with(pageable);
        
        // Ejecutar consulta
        List<ConsultaExternaDocument> consultas = mongoTemplate.find(query, ConsultaExternaDocument.class);
        
        // Contar total de elementos para paginación
        long count = mongoTemplate.count(query.skip(0).limit(0), ConsultaExternaDocument.class);
        
        return PageableExecutionUtils.getPage(consultas, pageable, () -> count);
    }
}