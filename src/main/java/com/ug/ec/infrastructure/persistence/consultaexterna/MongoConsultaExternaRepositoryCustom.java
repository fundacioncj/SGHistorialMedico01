package com.ug.ec.infrastructure.persistence.consultaexterna;

import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface MongoConsultaExternaRepositoryCustom {
    
    Page<ConsultaExternaDocument> findByFiltrosAvanzados(
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            String especialidad,
            String medicoTratante,
            EstadoConsulta estado,
            Pageable pageable);
}