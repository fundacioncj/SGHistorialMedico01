package com.ug.ec.application.signosvitales.handlers;

import com.ug.ec.application.signosvitales.commands.AgregarTomaCommand;
import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.domain.signosvitales.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgregarTomaHandler {

    private final SignosVitalesRepository repository;

    @Transactional
    public SignosVitales ejecutar(AgregarTomaCommand command) {

        SignosVitales signosVitales = repository.buscarPorId(command.getSignosVitalesId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Signos vitales no encontrado con ID: " + command.getSignosVitalesId()));

        if (!signosVitales.getActivo()) {
            throw new IllegalStateException("No se puede agregar tomas a un registro eliminado");
        }

        // Validar toma
        command.getToma().validar();

        // Validar y configurar presión arterial
        signosVitales.validarPresionArterial(command.getToma());

        // Agregar toma (valida límite de 2 tomas)
        signosVitales.agregarToma(command.getToma());

        return repository.guardar(signosVitales);
    }
}
