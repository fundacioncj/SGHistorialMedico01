package com.ug.ec.infrastructure.rest.historiaclinica;


import com.ug.ec.application.historiaclinica.commands.*;
import com.ug.ec.application.historiaclinica.handlers.*;
import com.ug.ec.application.historiaclinica.queries.ListarHistoriasClinicasQuery;
import com.ug.ec.application.historiaclinica.queries.ObtenerHistoriaClinicaPorIdQuery;
import com.ug.ec.application.historiaclinica.queries.ObtenerHistoriaClinicaPorPacienteQuery;
import com.ug.ec.domain.historiaclinica.HistoriaClinica;
import com.ug.ec.domain.historiaclinica.enums.TipoAntecedente;
import com.ug.ec.infrastructure.rest.historiaclinica.request.*;
import com.ug.ec.infrastructure.rest.historiaclinica.response.HistoriaClinicaResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/historias-clinicas")
@RequiredArgsConstructor
@Tag(name = "Historial Clinico", description = "API para la gestión de historial clinico (Formulario HCU-002)")
public class HistoriaClinicaController {

    // Command Handlers
    private final CrearHistoriaClinicaHandler crearHandler;
    private final ActualizarHistoriaClinicaHandler actualizarHandler;
    private final ActualizarDatosFormularioHandler actualizarDatosFormularioHandler;
    private final AgregarAntecedentePersonalHandler agregarAntecedentePersonalHandler;
    private final AgregarAntecedenteFamiliarHandler agregarAntecedenteFamiliarHandler;
    private final EliminarHistoriaClinicaHandler eliminarHandler;


    private final ActualizarAntecedentePersonalHandler actualizarAntecedentePersonalHandler;
    private final ActualizarAntecedenteFamiliarHandler actualizarAntecedenteFamiliarHandler;
    private final EliminarAntecedentePersonalHandler eliminarAntecedentePersonalHandler;
    private final EliminarAntecedenteFamiliarHandler eliminarAntecedenteFamiliarHandler;

    // Query Handlers
    private final ObtenerHistoriaClinicaPorIdHandler obtenerPorIdHandler;
    private final ObtenerHistoriaClinicaPorPacienteHandler obtenerPorPacienteHandler;
    private final ListarHistoriasClinicasHandler listarHandler;


    /**
     * Crear una nueva historia clínica
     */
    @PostMapping
    public ResponseEntity<HistoriaClinicaResponse> crear(
            @Valid @RequestBody CrearHistoriaClinicaRequest request) {

        CrearHistoriaClinicaCommand command = CrearHistoriaClinicaCommand.builder()
                .idPaciente(request.getIdPaciente())
                .cedulaPaciente(request.getCedulaPaciente())
                .datosFormulario(request.getDatosFormulario())
                .antecedentesPatologicosPersonales(request.getAntecedentesPatologicosPersonales())
                .antecedentesPatologicosFamiliares(request.getAntecedentesPatologicosFamiliares())
                .usuarioCreacion(request.getUsuarioCreacion())
                .build();

        HistoriaClinica resultado = crearHandler.ejecutar(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(resultado));
    }

    /**
     * Obtener historia clínica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistoriaClinicaResponse> obtenerPorId(@PathVariable String id) {

        ObtenerHistoriaClinicaPorIdQuery query = new ObtenerHistoriaClinicaPorIdQuery(id);
        HistoriaClinica resultado = obtenerPorIdHandler.ejecutar(query);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Obtener historia clínica por paciente
     */
    @GetMapping("/paciente")
    public ResponseEntity<HistoriaClinicaResponse> obtenerPorPaciente(
            @RequestParam(required = false) String pacienteId,
            @RequestParam(required = false) String cedulaPaciente) {

        ObtenerHistoriaClinicaPorPacienteQuery query =
                new ObtenerHistoriaClinicaPorPacienteQuery(pacienteId, cedulaPaciente);
        HistoriaClinica resultado = obtenerPorPacienteHandler.ejecutar(query);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Listar todas las historias clínicas
     */
    @GetMapping
    public ResponseEntity<List<HistoriaClinicaResponse>> listar(
            @RequestParam(defaultValue = "true") Boolean soloActivas) {

        ListarHistoriasClinicasQuery query = ListarHistoriasClinicasQuery.builder()
                .soloActivas(soloActivas)
                .build();

        List<HistoriaClinica> resultado = listarHandler.ejecutar(query);

        List<HistoriaClinicaResponse> response = resultado.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar historia clínica completa
     */
    @PutMapping("/{id}")
    public ResponseEntity<HistoriaClinicaResponse> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ActualizarHistoriaClinicaRequest request) {

        ActualizarHistoriaClinicaCommand command = ActualizarHistoriaClinicaCommand.builder()
                .id(id)
                .idPaciente(request.getIdPaciente())
                .cedulaPaciente(request.getCedulaPaciente())
                .datosFormulario(request.getDatosFormulario())
                .antecedentesPatologicosPersonales(request.getAntecedentesPatologicosPersonales())
                .antecedentesPatologicosFamiliares(request.getAntecedentesPatologicosFamiliares())
                .usuarioModificacion(request.getUsuarioModificacion())
                .build();

        HistoriaClinica resultado = actualizarHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Actualizar solo datos del formulario
     */
    @PatchMapping("/{id}/datos-formulario")
    public ResponseEntity<HistoriaClinicaResponse> actualizarDatosFormulario(
            @PathVariable String id,
            @Valid @RequestBody ActualizarDatosFormularioRequest request) {

        ActualizarDatosFormularioCommand command = ActualizarDatosFormularioCommand.builder()
                .historiaClinicaId(id)
                .datosFormulario(request.getDatosFormulario())
                .usuarioModificacion(request.getUsuarioModificacion())
                .build();

        HistoriaClinica resultado = actualizarDatosFormularioHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Agregar antecedente patológico personal
     */
    @PostMapping("/{id}/antecedentes-personales")
    public ResponseEntity<HistoriaClinicaResponse> agregarAntecedentePersonal(
            @PathVariable String id,
            @Valid @RequestBody AgregarAntecedentePersonalRequest request) {

        AgregarAntecedentePersonalCommand command = AgregarAntecedentePersonalCommand.builder()
                .historiaClinicaId(id)
                .antecedente(request.getAntecedente())
                .usuarioModificacion(request.getUsuarioModificacion())
                .build();

        HistoriaClinica resultado = agregarAntecedentePersonalHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Agregar antecedente patológico familiar
     */
    @PostMapping("/{id}/antecedentes-familiares")
    public ResponseEntity<HistoriaClinicaResponse> agregarAntecedenteFamiliar(
            @PathVariable String id,
            @Valid @RequestBody AgregarAntecedenteFamiliarRequest request) {

        AgregarAntecedenteFamiliarCommand command = AgregarAntecedenteFamiliarCommand.builder()
                .historiaClinicaId(id)
                .antecedente(request.getAntecedente())
                .usuarioModificacion(request.getUsuarioModificacion())
                .build();

        HistoriaClinica resultado = agregarAntecedenteFamiliarHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Actualizar un antecedente patológico personal
     */
    @PutMapping("/{id}/antecedentes-personales")
    public ResponseEntity<HistoriaClinicaResponse> actualizarAntecedentePersonal(
            @PathVariable String id,
            @Valid @RequestBody ActualizarAntecedentePersonalRequest request) {

        ActualizarAntecedentePersonalCommand command = ActualizarAntecedentePersonalCommand.builder()
                .historiaClinicaId(id)
                .tipoAntecedenteOriginal(request.getTipoAntecedenteOriginal())
                .antecedenteActualizado(request.getAntecedenteActualizado())
                .usuarioModificacion(request.getUsuarioModificacion())
                .build();

        HistoriaClinica resultado = actualizarAntecedentePersonalHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Actualizar un antecedente patológico familiar
     */
    @PutMapping("/{id}/antecedentes-familiares/{indice}")
    public ResponseEntity<HistoriaClinicaResponse> actualizarAntecedenteFamiliar(
            @PathVariable String id,
            @PathVariable Integer indice,
            @Valid @RequestBody ActualizarAntecedenteFamiliarRequest request) {

        // El índice viene en el path, lo actualizamos en el request
        request.setIndice(indice);

        ActualizarAntecedenteFamiliarCommand command = ActualizarAntecedenteFamiliarCommand.builder()
                .historiaClinicaId(id)
                .indice(request.getIndice())
                .antecedenteActualizado(request.getAntecedenteActualizado())
                .usuarioModificacion(request.getUsuarioModificacion())
                .build();

        HistoriaClinica resultado = actualizarAntecedenteFamiliarHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Eliminar un antecedente patológico personal por tipo
     */
    @DeleteMapping("/{id}/antecedentes-personales/{tipoAntecedente}")
    public ResponseEntity<HistoriaClinicaResponse> eliminarAntecedentePersonal(
            @PathVariable String id,
            @PathVariable TipoAntecedente tipoAntecedente,
            @RequestParam String usuarioModificacion) {

        EliminarAntecedentePersonalCommand command = EliminarAntecedentePersonalCommand.builder()
                .historiaClinicaId(id)
                .tipoAntecedente(tipoAntecedente)
                .usuarioModificacion(usuarioModificacion)
                .build();

        HistoriaClinica resultado = eliminarAntecedentePersonalHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Eliminar un antecedente patológico familiar por índice
     */
    @DeleteMapping("/{id}/antecedentes-familiares/{indice}")
    public ResponseEntity<HistoriaClinicaResponse> eliminarAntecedenteFamiliar(
            @PathVariable String id,
            @PathVariable Integer indice,
            @RequestParam String usuarioModificacion) {

        EliminarAntecedenteFamiliarCommand command = EliminarAntecedenteFamiliarCommand.builder()
                .historiaClinicaId(id)
                .indice(indice)
                .usuarioModificacion(usuarioModificacion)
                .build();

        HistoriaClinica resultado = eliminarAntecedenteFamiliarHandler.ejecutar(command);

        return ResponseEntity.ok(toResponse(resultado));
    }

    /**
     * Eliminar lógicamente historia clínica
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id,
            @RequestParam String usuarioEliminacion) {

        EliminarHistoriaClinicaCommand command = EliminarHistoriaClinicaCommand.builder()
                .id(id)
                .usuarioEliminacion(usuarioEliminacion)
                .build();

        eliminarHandler.ejecutar(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * Mapper de entidad a response
     */
    private HistoriaClinicaResponse toResponse(HistoriaClinica historiaClinica)  {
        return HistoriaClinicaResponse.builder()
                .id(historiaClinica.getId())
                .idPaciente(historiaClinica.getIdPaciente())
                .cedulaPaciente(historiaClinica.getCedulaPaciente())
                .datosFormulario(historiaClinica.getDatosFormulario())
                .antecedentesPatologicosPersonales(historiaClinica.getAntecedentesPatologicosPersonales())
                .antecedentesPatologicosFamiliares(historiaClinica.getAntecedentesPatologicosFamiliares())
                .fechaUltimaActualizacion(historiaClinica.getFechaUltimaActualizacion())
                .auditoria(historiaClinica.getAuditoria())
                .activo(historiaClinica.getActivo())
                .resumen(historiaClinica.obtenerResumen())
                .estaCompleta(historiaClinica.estaCompleta())
                .build();
    }



}
