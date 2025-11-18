package com.ug.ec.infrastructure.persistence.historiaClinica;

import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HistoriaClinicaRepositoryAdapter implements HistoriaClinicaRepository {


    private final HistoriaClinicaMongoRepository mongoRepository;

    @Override
    public HistoriaClinica guardar(HistoriaClinica historiaClinica) {
        return mongoRepository.save(historiaClinica);
    }

    @Override
    public Optional<HistoriaClinica> buscarPorId(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public Optional<HistoriaClinica> buscarPorPacienteId(String pacienteId) {
        return mongoRepository.findByIdPacienteAndActivoTrue(pacienteId);
    }

    @Override
    public Optional<HistoriaClinica> buscarPorCedulaPaciente(String cedulaPaciente) {
        return mongoRepository.findByCedulaPacienteAndActivoTrue(cedulaPaciente);
    }

    @Override
    public List<HistoriaClinica> listarActivas() {
        return mongoRepository.findByActivoTrue();
    }

    @Override
    public List<HistoriaClinica> listarTodas() {
        return mongoRepository.findAll();
    }

    @Override
    public boolean existePorPacienteId(String pacienteId) {
        return mongoRepository.existsByIdPacienteAndActivoTrue(pacienteId);
    }

    @Override
    public boolean existePorCedulaPaciente(String cedulaPaciente) {
        return mongoRepository.existsByCedulaPacienteAndActivoTrue(cedulaPaciente);
    }

    @Override
    public void eliminar(String id) {
        mongoRepository.deleteById(id);
    }
}
