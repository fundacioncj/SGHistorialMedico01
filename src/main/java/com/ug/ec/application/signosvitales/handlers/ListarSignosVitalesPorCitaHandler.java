package com.ug.ec.application.signosvitales.handlers;

import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.application.signosvitales.queries.ListarSignosVitalesPorCitaQuery;
import com.ug.ec.domain.signosvitales.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarSignosVitalesPorCitaHandler {

    private final SignosVitalesRepository repository;

    @Transactional(readOnly = true)
    public List<SignosVitales> ejecutar(ListarSignosVitalesPorCitaQuery query) {
        return repository.buscarPorCitaId(query.getCitaId())
                .stream()
                .filter(SignosVitales::getActivo)
                .collect(Collectors.toList());
    }
}
