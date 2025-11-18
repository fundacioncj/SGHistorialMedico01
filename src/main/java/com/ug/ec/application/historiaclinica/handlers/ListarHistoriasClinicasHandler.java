package com.ug.ec.application.historiaclinica.handlers;

import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.application.historiaclinica.queries.ListarHistoriasClinicasQuery;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarHistoriasClinicasHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional(readOnly = true)
    public List<HistoriaClinica> ejecutar(ListarHistoriasClinicasQuery query) {
        if (query.getSoloActivas() != null && query.getSoloActivas()) {
            return repository.listarActivas();
        }
        return repository.listarTodas();
    }
}
