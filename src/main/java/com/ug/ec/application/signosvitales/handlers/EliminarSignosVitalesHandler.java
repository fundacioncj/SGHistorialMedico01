package com.ug.ec.application.signosvitales.handlers;

import com.ug.ec.application.signosvitales.commands.EliminarSignosVitalesCommand;
import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.domain.signosvitales.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarSignosVitalesHandler {

    private final SignosVitalesRepository repository;

    @Transactional
    public void ejecutar(EliminarSignosVitalesCommand command) {

        SignosVitales signosVitales = repository.buscarPorId(command.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Signos vitales no encontrado con ID: " + command.getId()));

        if (!signosVitales.getActivo()) {
            throw new IllegalStateException("El registro ya está eliminado");
        }

        // Eliminación lógica
        signosVitales.eliminarLogicamente(command.getUsuarioEliminacion());

        repository.guardar(signosVitales);
    }
}
