package com.ug.ec.application.historiaclinica.handlers;


import com.ug.ec.application.historiaclinica.commands.ActualizarHistoriaClinicaCommand;
import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActualizarHistoriaClinicaHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional
    public HistoriaClinica ejecutar(ActualizarHistoriaClinicaCommand command) {

        HistoriaClinica historiaClinica = repository.buscarPorId(command.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Historia clínica no encontrada con ID: " + command.getId()));

        if (!historiaClinica.getActivo()) {
            throw new IllegalStateException("No se puede actualizar una historia clínica eliminada");
        }

        // Actualizar campos
        if (command.getIdPaciente() != null) {
            historiaClinica.setIdPaciente(command.getIdPaciente());
        }

        if (command.getCedulaPaciente() != null) {
            historiaClinica.setCedulaPaciente(command.getCedulaPaciente());
        }

        if (command.getDatosFormulario() != null) {
            command.getDatosFormulario().validar();
            historiaClinica.setDatosFormulario(command.getDatosFormulario());
        }

        if (command.getAntecedentesPatologicosPersonales() != null) {
            command.getAntecedentesPatologicosPersonales().forEach(a -> a.validar());
            historiaClinica.setAntecedentesPatologicosPersonales(
                    command.getAntecedentesPatologicosPersonales());
        }

        if (command.getAntecedentesPatologicosFamiliares() != null) {
            command.getAntecedentesPatologicosFamiliares().forEach(a -> a.validar());
            historiaClinica.setAntecedentesPatologicosFamiliares(
                    command.getAntecedentesPatologicosFamiliares());
        }

        // Actualizar auditoría
        historiaClinica.setFechaUltimaActualizacion(LocalDateTime.now());
        historiaClinica.setAuditoria(
                historiaClinica.getAuditoria().actualizar(command.getUsuarioModificacion()));

        // Validar integridad
        historiaClinica.validar();

        return repository.guardar(historiaClinica);
    }

}
