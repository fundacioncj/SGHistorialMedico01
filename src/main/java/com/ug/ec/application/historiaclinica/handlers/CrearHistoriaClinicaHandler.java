package com.ug.ec.application.historiaclinica.handlers;

import com.ug.ec.application.historiaclinica.commands.CrearHistoriaClinicaCommand;
import com.ug.ec.application.historiaclinica.port.HistoriaClinicaRepository;
import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CrearHistoriaClinicaHandler {

    private final HistoriaClinicaRepository repository;

    @Transactional
    public HistoriaClinica ejecutar(CrearHistoriaClinicaCommand command) {

        // Validar que no exista una historia clínica para el paciente
        if (repository.existePorPacienteId(command.getIdPaciente())) {
            throw new IllegalStateException(
                    "Ya existe una historia clínica para el paciente con ID: " + command.getIdPaciente());
        }

        if (repository.existePorCedulaPaciente(command.getCedulaPaciente())) {
            throw new IllegalStateException(
                    "Ya existe una historia clínica para el paciente con cédula: " +
                            command.getCedulaPaciente());
        }

        // Crear la historia clínica
        HistoriaClinica historiaClinica = HistoriaClinica.builder()
                .idPaciente(command.getIdPaciente())
                .cedulaPaciente(command.getCedulaPaciente())
                .datosFormulario(command.getDatosFormulario())
                .antecedentesPatologicosPersonales(
                        command.getAntecedentesPatologicosPersonales() != null ?
                                command.getAntecedentesPatologicosPersonales() : new ArrayList<>())
                .antecedentesPatologicosFamiliares(
                        command.getAntecedentesPatologicosFamiliares() != null ?
                                command.getAntecedentesPatologicosFamiliares() : new ArrayList<>())
                .fechaUltimaActualizacion(LocalDateTime.now())
                .auditoria(DatosAuditoria.crearNuevo(command.getUsuarioCreacion()))
                .activo(true)
                .build();

        // Validar entidad de dominio
        historiaClinica.validar();

        // Validar antecedentes si existen
        if (historiaClinica.getAntecedentesPatologicosPersonales() != null) {
            historiaClinica.getAntecedentesPatologicosPersonales()
                    .forEach(a -> a.validar());
        }

        if (historiaClinica.getAntecedentesPatologicosFamiliares() != null) {
            historiaClinica.getAntecedentesPatologicosFamiliares()
                    .forEach(a -> a.validar());
        }

        return repository.guardar(historiaClinica);
    }
}