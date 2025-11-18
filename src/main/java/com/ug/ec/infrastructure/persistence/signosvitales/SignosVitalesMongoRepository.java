package com.ug.ec.infrastructure.persistence.signosvitales;

import com.ug.ec.domain.signosvitales.SignosVitales;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignosVitalesMongoRepository extends MongoRepository<SignosVitales, String> {

    List<SignosVitales> findByPacienteIdAndActivoTrue(String pacienteId);

    List<SignosVitales> findByCitaIdAndActivoTrue(String citaId);

    List<SignosVitales> findByActivoTrue();

    boolean existsByCitaIdAndActivoTrue(String citaId);
}
