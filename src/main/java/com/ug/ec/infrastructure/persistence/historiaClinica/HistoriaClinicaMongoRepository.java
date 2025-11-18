package com.ug.ec.infrastructure.persistence.historiaClinica;

import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaClinicaMongoRepository extends MongoRepository<HistoriaClinica, String> {

    Optional<HistoriaClinica> findByIdPacienteAndActivoTrue(String idPaciente);

    Optional<HistoriaClinica> findByCedulaPacienteAndActivoTrue(String cedulaPaciente);

    List<HistoriaClinica> findByActivoTrue();

    boolean existsByIdPacienteAndActivoTrue(String idPaciente);

    boolean existsByCedulaPacienteAndActivoTrue(String cedulaPaciente);
}
