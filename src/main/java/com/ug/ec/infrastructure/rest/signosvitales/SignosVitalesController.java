package com.ug.ec.infrastructure.rest.signosvitales;

import com.ug.ec.application.signosvitales.commands.ActualizarSignosVitalesCommand;
import com.ug.ec.application.signosvitales.commands.AgregarTomaCommand;
import com.ug.ec.application.signosvitales.commands.CrearSignosVitalesCommand;
import com.ug.ec.application.signosvitales.commands.EliminarSignosVitalesCommand;
import com.ug.ec.application.signosvitales.handlers.*;
import com.ug.ec.application.signosvitales.queries.ListarSignosVitalesPorCitaQuery;
import com.ug.ec.application.signosvitales.queries.ListarSignosVitalesPorPacienteQuery;
import com.ug.ec.application.signosvitales.queries.ObtenerSignosVitalesPorIdQuery;
import com.ug.ec.domain.signosvitales.SignosVitales;
import com.ug.ec.infrastructure.rest.signosvitales.request.ActualizarSignosVitalesRequest;
import com.ug.ec.infrastructure.rest.signosvitales.request.AgregarTomaRequest;
import com.ug.ec.infrastructure.rest.signosvitales.request.CrearSignosVitalesRequest;
import com.ug.ec.infrastructure.rest.signosvitales.response.SignosVitalesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/signos-vitales")
@RequiredArgsConstructor
public class SignosVitalesController {

    // Command Handlers
    private final CrearSignosVitalesHandler crearHandler;
    private final ActualizarSignosVitalesHandler actualizarHandler;
    private final AgregarTomaHandler agregarTomaHandler;
    private final EliminarSignosVitalesHandler eliminarHandler;

    // Query Handlers
    private final ObtenerSignosVitalesPorIdHandler obtenerPorIdHandler;
    private final ListarSignosVitalesPorPacienteHandler listarPorPacienteHandler;
    private final ListarSignosVitalesPorCitaHandler listarPorCitaHandler;

    /**
     * Crear un nuevo registro de signos vitales
     */
    @PostMapping
    public ResponseEntity<SignosVitalesResponse> crear(
            @Valid @RequestBody CrearSignosVitalesRequest request) {

        CrearSignosVitalesCommand command = CrearSignosVitalesCommand.builder()
                .citaId(request.getCitaId())
                .pacienteId(request.getPacienteId())
                .usuarioRegistro(request.getUsuarioRegistro())
                .fechaRegistro(request.getFechaRegistro())
                .tomas(request.getTomas())
                .build();

        SignosVitales resultado = crearHandler.ejecutar(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(resultado));
    }

    /**
     * Obtener signos vitales por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SignosVitalesResponse> obtenerPorId(@PathVariable String id) {

        ObtenerSignosVitalesPorIdQuery query = new ObtenerSignosVitalesPorIdQuery(id);
        SignosVitales resultado = obtenerPorIdHandler.ejecutar(query);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Listar signos vitales por paciente
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<SignosVitalesResponse>> listarPorPaciente(
            @PathVariable String pacienteId) {

        ListarSignosVitalesPorPacienteQuery query =
                new ListarSignosVitalesPorPacienteQuery(pacienteId);
        List<SignosVitales> resultado = listarPorPacienteHandler.ejecutar(query);

        List<SignosVitalesResponse> response = resultado.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Listar signos vitales por cita
     */
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<List<SignosVitalesResponse>> listarPorCita(
            @PathVariable String citaId) {

        ListarSignosVitalesPorCitaQuery query = new ListarSignosVitalesPorCitaQuery(citaId);
        List<SignosVitales> resultado = listarPorCitaHandler.ejecutar(query);

        List<SignosVitalesResponse> response = resultado.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    /**
     * Actualizar signos vitales
     */
    @PutMapping("/{id}")
    public ResponseEntity<SignosVitalesResponse> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ActualizarSignosVitalesRequest request) {

        ActualizarSignosVitalesCommand command = ActualizarSignosVitalesCommand.builder()
                .id(id)
                .citaId(request.getCitaId())
                .pacienteId(request.getPacienteId())
                .usuarioRegistro(request.getUsuarioRegistro())
                .tomas(request.getTomas())
                .build();

        SignosVitales resultado = actualizarHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Agregar una nueva toma de signos vitales
     */
    @PostMapping("/{id}/tomas")
    public ResponseEntity<SignosVitalesResponse> agregarToma(
            @PathVariable String id,
            @Valid @RequestBody AgregarTomaRequest request) {

        AgregarTomaCommand command = AgregarTomaCommand.builder()
                .signosVitalesId(id)
                .toma(request.getToma())
                .build();

        SignosVitales resultado = agregarTomaHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Eliminar lógicamente signos vitales
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id,
            @RequestParam String usuarioEliminacion) {

        EliminarSignosVitalesCommand command = EliminarSignosVitalesCommand.builder()
                .id(id)
                .usuarioEliminacion(usuarioEliminacion)
                .build();

        eliminarHandler.ejecutar(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * Mapper de entidad a response
     */
    private SignosVitalesResponse toResponse(SignosVitales signosVitales) {
        return SignosVitalesResponse.builder()
                .id(signosVitales.getId())
                .citaId(signosVitales.getCitaId())
                .pacienteId(signosVitales.getPacienteId())
                .usuarioRegistro(signosVitales.getUsuarioRegistro())
                .fechaRegistro(signosVitales.getFechaRegistro())
                .tomas(signosVitales.getTomas())
                .activo(signosVitales.getActivo())
                .build();
    }
}