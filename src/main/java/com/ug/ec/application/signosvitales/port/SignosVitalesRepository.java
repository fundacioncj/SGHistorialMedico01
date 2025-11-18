package com.ug.ec.application.signosvitales.port;

import com.ug.ec.domain.signosvitales.SignosVitales;

import java.util.List;
import java.util.Optional;

public interface SignosVitalesRepository {

    SignosVitales guardar(SignosVitales signosVitales);

    Optional<SignosVitales> buscarPorId(String id);

    List<SignosVitales> buscarPorPacienteId(String pacienteId);

    List<SignosVitales> buscarPorCitaId(String citaId);

    List<SignosVitales> listarActivos();

    boolean existePorCitaId(String citaId);

    void eliminar(String id);
}
