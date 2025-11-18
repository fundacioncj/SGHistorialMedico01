package com.ug.ec.application.signosvitales.handlers;

import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.application.signosvitales.queries.ObtenerSignosVitalesPorIdQuery;
import com.ug.ec.domain.signosvitales.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerSignosVitalesPorIdHandler {

    private final SignosVitalesRepository repository;

    @Transactional(readOnly = true)
    public SignosVitales ejecutar(ObtenerSignosVitalesPorIdQuery query) {
        return repository.buscarPorId(query.getId())
                .filter(SignosVitales::getActivo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Signos vitales no encontrado con ID: " + query.getId()));
    }
}