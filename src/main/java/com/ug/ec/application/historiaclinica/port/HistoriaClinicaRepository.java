package com.ug.ec.application.historiaclinica.port;

import com.ug.ec.domain.historiaclinica.HistoriaClinica;

import java.util.List;
import java.util.Optional;

public interface HistoriaClinicaRepository {

    HistoriaClinica guardar(HistoriaClinica historiaClinica);

    Optional<HistoriaClinica> buscarPorId(String id);

    Optional<HistoriaClinica> buscarPorPacienteId(String pacienteId);

    Optional<HistoriaClinica> buscarPorCedulaPaciente(String cedulaPaciente);

    List<HistoriaClinica> listarActivas();

    List<HistoriaClinica> listarTodas();

    boolean existePorPacienteId(String pacienteId);

    boolean existePorCedulaPaciente(String cedulaPaciente);

    void eliminar(String id);
}
