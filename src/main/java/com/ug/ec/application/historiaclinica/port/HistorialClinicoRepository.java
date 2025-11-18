package com.ug.ec.application.historiaclinica.port;

import com.ug.ec.domain.historiaclinica.HistorialClinico;

import java.util.Optional;

public interface HistorialClinicoRepository {

    Optional<HistorialClinico> findById(String id);
    Optional<HistorialClinico> findByCedulaPaciente(String cedulaPaciente);

    HistorialClinico save(HistorialClinico historiaClinica);
}
