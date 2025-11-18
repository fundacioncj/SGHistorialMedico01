package com.ug.ec.application.historiaclinica.handlers;

import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.application.historiaclinica.queries.ObtenerHistoriaClinicaPorIdQuery;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerHistoriaClinicaPorIdHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional(readOnly = true)
    public HistoriaClinica ejecutar(ObtenerHistoriaClinicaPorIdQuery query) {
        return repository.buscarPorId(query.getId())
                .filter(HistoriaClinica::getActivo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Historia clínica no encontrada con ID: " + query.getId()));
    }
}
