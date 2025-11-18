package com.ug.ec.infrastructure.persistence.historiaClinica;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoHistorialClinicoRepository extends MongoRepository<HistoriaClinicaDocument, String> {

    Optional<HistoriaClinicaDocument> findByCedulaPaciente(String cedulaPaciente);
}
