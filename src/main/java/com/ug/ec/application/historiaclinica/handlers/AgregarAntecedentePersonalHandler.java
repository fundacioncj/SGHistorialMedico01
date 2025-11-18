package com.ug.ec.application.historiaclinica.handlers;

import com.ug.ec.application.historiaclinica.commands.AgregarAntecedentePersonalCommand;
import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgregarAntecedentePersonalHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional
    public HistoriaClinica ejecutar(AgregarAntecedentePersonalCommand command) {

        HistoriaClinica historiaClinica = repository.buscarPorId(command.getHistoriaClinicaId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Historia clínica no encontrada con ID: " + command.getHistoriaClinicaId()));

        if (!historiaClinica.getActivo()) {
            throw new IllegalStateException(
                    "No se pueden agregar antecedentes a una historia clínica eliminada");
        }

        // Utilizar el método de dominio
        historiaClinica.agregarAntecedentePersonal(
                command.getAntecedente(),
                command.getUsuarioModificacion());

        return repository.guardar(historiaClinica);
    }

}
