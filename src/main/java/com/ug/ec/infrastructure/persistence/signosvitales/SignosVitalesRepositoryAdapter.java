package com.ug.ec.infrastructure.persistence.signosvitales;

import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.domain.signosvitales.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SignosVitalesRepositoryAdapter implements SignosVitalesRepository {

    private final SignosVitalesMongoRepository mongoRepository;

    @Override
    public SignosVitales guardar(SignosVitales signosVitales) {
        return mongoRepository.save(signosVitales);
    }

    @Override
    public Optional<SignosVitales> buscarPorId(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public List<SignosVitales> buscarPorPacienteId(String pacienteId) {
        return mongoRepository.findByPacienteIdAndActivoTrue(pacienteId);
    }

    @Override
    public List<SignosVitales> buscarPorCitaId(String citaId) {
        return mongoRepository.findByCitaIdAndActivoTrue(citaId);
    }

    @Override
    public List<SignosVitales> listarActivos() {
        return mongoRepository.findByActivoTrue();
    }

    @Override
    public boolean existePorCitaId(String citaId) {
        return mongoRepository.existsByCitaIdAndActivoTrue(citaId);
    }

    @Override
    public void eliminar(String id) {
        mongoRepository.deleteById(id);
    }
}