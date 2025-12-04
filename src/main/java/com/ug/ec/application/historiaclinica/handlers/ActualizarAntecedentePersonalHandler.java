package com.ug.ec.application.historiaclinica.handlers;

import com.ug.ec.application.historiaclinica.commands.ActualizarAntecedentePersonalCommand;
import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarAntecedentePersonalHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional
    public HistoriaClinica ejecutar(ActualizarAntecedentePersonalCommand command) {

        HistoriaClinica historiaClinica = repository.buscarPorId(command.getHistoriaClinicaId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Historia clínica no encontrada con ID: " + command.getHistoriaClinicaId()));

        if (!historiaClinica.getActivo()) {
            throw new IllegalStateException(
                    "No se pueden actualizar antecedentes de una historia clínica eliminada");
        }

        // Utilizar el método de dominio
        historiaClinica.actualizarAntecedentePersonal(
                command.getTipoAntecedenteOriginal(),
                command.getAntecedenteActualizado(),
                command.getUsuarioModificacion());

        return repository.guardar(historiaClinica);
    }
}