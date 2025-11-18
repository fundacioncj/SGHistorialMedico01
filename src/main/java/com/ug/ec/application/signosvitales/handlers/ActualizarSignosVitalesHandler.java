package com.ug.ec.application.signosvitales.handlers;

import com.ug.ec.application.signosvitales.commands.ActualizarSignosVitalesCommand;
import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.domain.signosvitales.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarSignosVitalesHandler {

    private final SignosVitalesRepository repository;

    @Transactional
    public SignosVitales ejecutar(ActualizarSignosVitalesCommand command) {

        SignosVitales signosVitales = repository.buscarPorId(command.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Signos vitales no encontrado con ID: " + command.getId()));

        if (!signosVitales.getActivo()) {
            throw new IllegalStateException("No se puede actualizar un registro eliminado");
        }

        // Actualizar campos
        if (command.getCitaId() != null) {
            signosVitales.setCitaId(command.getCitaId());
        }
        if (command.getPacienteId() != null) {
            signosVitales.setPacienteId(command.getPacienteId());
        }
        if (command.getUsuarioRegistro() != null) {
            signosVitales.setUsuarioRegistro(command.getUsuarioRegistro());
        }
        if (command.getTomas() != null) {
            signosVitales.setTomas(command.getTomas());
            // Validar y recalcular IMC para cada toma
            command.getTomas().forEach(toma -> {
                toma.validar();
                signosVitales.validarPresionArterial(toma);
            });
        }

        signosVitales.validar();

        return repository.guardar(signosVitales);
    }
}