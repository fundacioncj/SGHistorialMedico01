package com.ug.ec.application.signosvitales.handlers;

import com.ug.ec.application.signosvitales.commands.CrearSignosVitalesCommand;
import com.ug.ec.application.signosvitales.port.SignosVitalesRepository;
import com.ug.ec.domain.signosvitales.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CrearSignosVitalesHandler {

    private final SignosVitalesRepository repository;

    @Transactional
    public SignosVitales ejecutar(CrearSignosVitalesCommand command) {

        // Validar que no exista otro registro para la misma cita
        if (repository.existePorCitaId(command.getCitaId())) {
            throw new IllegalStateException("Ya existe un registro de signos vitales para esta cita");
        }

        SignosVitales signosVitales = SignosVitales.builder()
                .citaId(command.getCitaId())
                .pacienteId(command.getPacienteId())
                .usuarioRegistro(command.getUsuarioRegistro())
                .fechaRegistro(command.getFechaRegistro() != null ?
                        command.getFechaRegistro() : LocalDateTime.now())
                .activo(true)
                .build();

        // Agregar tomas si existen
        if (command.getTomas() != null && !command.getTomas().isEmpty()) {
            command.getTomas().forEach(toma -> {
                toma.validar();
                signosVitales.validarPresionArterial(toma);
                signosVitales.agregarToma(toma);
            });
        }

        // Validar entidad de dominio
        signosVitales.validar();

        return repository.guardar(signosVitales);
    }
}