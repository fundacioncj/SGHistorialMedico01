package com.ug.ec.application.consultaexterna.handlers;

import com.ug.ec.application.consultaexterna.commands.CrearTriajeCommand;
import com.ug.ec.application.consultaexterna.ports.ConsultaExternaRepository;
import com.ug.ec.domain.consultaexterna.ConsultaExterna;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.consultaexterna.valueobjects.ExamenFisico;
import com.ug.ec.domain.consultaexterna.valueobjects.SignosVitales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrearTriajeCommandHandler {

    private final ConsultaExternaRepository repository;

    public String handle(CrearTriajeCommand command) {
        // Construir Examen Físico con signos vitales
        ExamenFisico examenFisico = ExamenFisico.builder()
                .signosVitales(command.getSignosVitales())
                .build();

        // Evaluar prioridad
        String nivelPrioridad = determinarPrioridad(command.getSignosVitales());

        // Crear consulta inicial con estado TRIAJE
        ConsultaExterna consulta = ConsultaExterna.builder()
                .cedulaPaciente(command.getCedulaPaciente())
                .examenFisico(examenFisico)
                .estado(EstadoConsulta.INICIADA)
                .auditoria(DatosAuditoria.crearNuevo(command.getUsuarioCreador()))
                .build();

        ConsultaExterna guardada = repository.save(consulta);
        return guardada.getId();
    }

    private String determinarPrioridad(SignosVitales signos) {
        if (signos.requiereAtencionUrgente()) return "ROJO";
        if (signos.tieneFiebre() || signos.tieneHipoxemia()) return "AMARILLO";
        return "VERDE";
    }
}
