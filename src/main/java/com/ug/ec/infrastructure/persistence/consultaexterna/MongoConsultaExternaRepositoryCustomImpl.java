package com.ug.ec.infrastructure.persistence.consultaexterna;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MongoConsultaExternaRepositoryCustomImpl implements MongoConsultaExternaRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public MongoConsultaExternaRepositoryCustomImpl(MongoTemplate mongoTemplate) {
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

        final Query query = new Query().with(pageable);
        final List<Criteria> criteriaList = new ArrayList<>();

        // Criterio de fecha
        if (fechaDesde != null && fechaHasta != null) {
            criteriaList.add(Criteria.where("datosConsulta.fechaConsulta").gte(fechaDesde).lte(fechaHasta));
        }

        // Criterio de especialidad
        if (especialidad != null && !especialidad.isEmpty()) {
            criteriaList.add(Criteria.where("datosConsulta.especialidad").regex(especialidad, "i"));
        }

        // Criterio de médico tratante
        if (medicoTratante != null && !medicoTratante.isEmpty()) {
            criteriaList.add(Criteria.where("datosConsulta.medicoTratante").regex(medicoTratante, "i"));
        }

        // Criterio de estado
        if (estado != null) {
            criteriaList.add(Criteria.where("estado").is(estado));
        }

        // Aplicar criterios a la consulta
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Ejecutar consulta
        List<ConsultaExternaDocument> consultas = mongoTemplate.find(query, ConsultaExternaDocument.class);

        // Contar total de elementos para paginación
        return PageableExecutionUtils.getPage(
                consultas,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ConsultaExternaDocument.class));
    }
}