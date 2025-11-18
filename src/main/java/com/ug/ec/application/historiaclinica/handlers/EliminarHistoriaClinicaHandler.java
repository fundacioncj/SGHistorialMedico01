package com.ug.ec.application.historiaclinica.handlers;


import com.ug.ec.application.historiaclinica.commands.EliminarHistoriaClinicaCommand;
import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarHistoriaClinicaHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional
    public void ejecutar(EliminarHistoriaClinicaCommand command) {

        HistoriaClinica historiaClinica = repository.buscarPorId(command.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Historia clínica no encontrada con ID: " + command.getId()));

        if (!historiaClinica.getActivo()) {
            throw new IllegalStateException("La historia clínica ya está eliminada");
        }

        // Eliminación lógica
        historiaClinica.eliminarLogicamente(command.getUsuarioEliminacion());

        repository.guardar(historiaClinica);
    }
}
