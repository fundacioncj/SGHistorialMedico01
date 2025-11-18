package com.ug.ec.application.historiaclinica.handlers;

import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.application.historiaclinica.queries.ObtenerHistoriaClinicaPorPacienteQuery;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerHistoriaClinicaPorPacienteHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional(readOnly = true)
    public HistoriaClinica ejecutar(ObtenerHistoriaClinicaPorPacienteQuery query) {

        // Buscar por ID de paciente o cédula
        if (query.getPacienteId() != null && !query.getPacienteId().isBlank()) {
            return repository.buscarPorPacienteId(query.getPacienteId())
                    .filter(HistoriaClinica::getActivo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No se encontró historia clínica para el paciente con ID: " +
                                    query.getPacienteId()));
        }

        if (query.getCedulaPaciente() != null && !query.getCedulaPaciente().isBlank()) {
            return repository.buscarPorCedulaPaciente(query.getCedulaPaciente())
                    .filter(HistoriaClinica::getActivo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No se encontró historia clínica para el paciente con cédula: " +
                                    query.getCedulaPaciente()));
        }

        throw new IllegalArgumentException(
                "Debe proporcionar el ID del paciente o la cédula");
    }
}
