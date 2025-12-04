package com.ug.ec.infrastructure.rest.consultaexterna;

import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.ActualizarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.commands.CrearTriajeCommand;
import com.ug.ec.application.consultaexterna.commands.EliminarConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
import com.ug.ec.application.consultaexterna.dto.ConsultaExternaResumenDto;
import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaCommandHandler;
import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaQueryHandler;
import com.ug.ec.application.consultaexterna.handlers.CrearTriajeCommandHandler;
import com.ug.ec.application.consultaexterna.queries.*;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.exceptions.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/consultas-externas")
@RequiredArgsConstructor
@Tag(name = "Consultas Externas", description = "API para la gestión de consultas externas médicas (Formulario HCU-002)")
public class ConsultaExternaController {
    
    private final ConsultaExternaCommandHandler commandHandler;
    private final ConsultaExternaQueryHandler queryHandler;
    private final CrearTriajeCommandHandler triajeCommandHandler;

    @PostMapping("/triaje")
    public ResponseEntity<Map<String, Object>> crearTriaje(
            @Valid @RequestBody CrearTriajeCommand command) {

        String id = triajeCommandHandler.handle(command);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Triaje registrado correctamente",
                "data", Map.of("id", id),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "Crear una nueva consulta externa",
        description = "Crea un nuevo registro de consulta externa médica con todos sus datos asociados",
        tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Consulta externa creada exitosamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Consulta externa duplicada",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearConsultaExterna(
            @Parameter(
                description = "Datos de la consulta externa a crear", 
                required = true,
                schema = @Schema(implementation = CrearConsultaExternaCommand.class)
            )
            @Valid @RequestBody CrearConsultaExternaCommand command) {
        

        
        try {
            String consultaId = commandHandler.handle(command);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consulta externa creada exitosamente",
                    "data", Map.of("id", consultaId),
                    "timestamp", LocalDateTime.now()
            );
            
            log.info("Consulta externa creada exitosamente con ID: {}", consultaId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error al crear consulta externa: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(
        summary = "Obtener una consulta externa por ID",
        description = "Recupera los datos completos de una consulta externa médica a partir de su identificador único",
        tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consulta externa encontrada exitosamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Consulta externa no encontrada",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerConsultaExternaPorId(
            @Parameter(
                description = "ID de la consulta externa a recuperar", 
                required = true,
                example = "60f1a5c2e8f87a2b94c12345"
            )
            @PathVariable String id) {
        
        log.info("Obteniendo consulta externa por ID: {}", id);
        
        try {
            BuscarConsultaExternaPorIdQuery query = BuscarConsultaExternaPorIdQuery.builder()
                    .id(id)
                    .build();
            
            ConsultaExternaDto consulta = queryHandler.handle(query);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consulta externa encontrada exitosamente",
                    "data", consulta,
                    "timestamp", LocalDateTime.now()
            );
            
            log.info("Consulta externa encontrada exitosamente con ID: {}", id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al obtener consulta externa por ID: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Obtener una consulta externa por Cita ID",
            description = "Recupera los datos completos de una consulta externa médica a partir de la cita",
            tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta externa encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consulta externa no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/cita/{id}")
    public ResponseEntity<Map<String, Object>> obtenerConsultaExternaPorCitaId(
            @Parameter(
                    description = "CITA ID de la consulta externa a recuperar",
                    required = true,
                    example = "1"
            )
            @PathVariable String id) {

        log.info("Obteniendo consulta externa por cita ID: {}", id);

        try {
            BuscarConsultaExternaPorCitaIdQuery query = BuscarConsultaExternaPorCitaIdQuery.builder()
                    .id(id)
                    .build();

            ConsultaExternaDto consulta = queryHandler.handle(query);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consulta externa encontrada exitosamente",
                    "data", consulta,
                    "timestamp", LocalDateTime.now()
            );

            log.info("Consulta externa encontrada exitosamente con CITA ID: {}", id);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener consulta externa por CITA ID: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(
        summary = "Buscar consultas externas por cédula del paciente",
        description = "Recupera todas las consultas externas médicas asociadas a un paciente identificado por su cédula",
        tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consultas externas encontradas exitosamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetros de búsqueda inválidos",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/paciente/{cedula}")
    public ResponseEntity<Map<String, Object>> buscarConsultasExternasPorCedula(
            @Parameter(
                description = "Cédula del paciente", 
                required = true,
                example = "0912345678"
            )
            @PathVariable @Pattern(regexp = "^\\d{10}$", message = "La cédula debe tener 10 dígitos numéricos") String cedula,
            @Parameter(
                description = "Fecha desde la cual filtrar las consultas (formato ISO)", 
                example = "2023-01-01"
            )
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @Parameter(
                description = "Fecha hasta la cual filtrar las consultas (formato ISO)", 
                example = "2023-12-31"
            )
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @Parameter(
                description = "Número de página (empieza en 0)", 
                example = "0"
            )
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(
                description = "Tamaño de página", 
                example = "10"
            )
            @RequestParam(defaultValue = "10") int tamanio) {
        
        log.info("Buscando consultas externas por cédula: {}", cedula);
        
        try {
            BuscarConsultaExternaPorCedulaQuery query = BuscarConsultaExternaPorCedulaQuery.builder()
                    .cedula(cedula)
                    .fechaDesde(fechaDesde)
                    .fechaHasta(fechaHasta)
                    .pagina(pagina)
                    .tamanio(tamanio)
                    .build();
            
            Page<ConsultaExternaResumenDto> consultasPage = queryHandler.handle(query);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consultas externas encontradas exitosamente",
                    "data", consultasPage.getContent(),
                    "pagination", Map.of(
                            "totalElements", consultasPage.getTotalElements(),
                            "totalPages", consultasPage.getTotalPages(),
                            "currentPage", consultasPage.getNumber(),
                            "pageSize", consultasPage.getSize()
                    ),
                    "timestamp", LocalDateTime.now()
            );
            
            log.info("Encontradas {} consultas externas para cédula: {}", 
                    consultasPage.getTotalElements(), cedula);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al buscar consultas externas por cédula: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(
        summary = "Buscar consulta externa por número de consulta",
        description = "Recupera una consulta externa médica a partir de su número de consulta único",
        tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consulta externa encontrada exitosamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Consulta externa no encontrada",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/numero/{numeroConsulta}")
    public ResponseEntity<Map<String, Object>> buscarConsultaExternaPorNumero(
            @Parameter(
                description = "Número de consulta externa", 
                required = true,
                example = "CE-2023-001234"
            )
            @PathVariable String numeroConsulta) {
        
        log.info("Buscando consulta externa por número: {}", numeroConsulta);
        
        try {
            BuscarConsultaExternaPorNumeroConsultaQuery query = 
                    BuscarConsultaExternaPorNumeroConsultaQuery.builder()
                            .numeroConsulta(numeroConsulta)
                            .build();
            
            ConsultaExternaDto consulta = queryHandler.handle(query);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consulta externa encontrada exitosamente",
                    "data", consulta,
                    "timestamp", LocalDateTime.now()
            );
            
            log.info("Consulta externa encontrada exitosamente con número: {}", numeroConsulta);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al buscar consulta externa por número: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(
        summary = "Buscar consultas externas con filtros avanzados",
        description = "Permite realizar búsquedas avanzadas de consultas externas médicas aplicando múltiples filtros",
        tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consultas externas encontradas exitosamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetros de búsqueda inválidos",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarConsultasExternasConFiltros(
            @Parameter(
                description = "Fecha desde la cual filtrar las consultas (formato ISO)", 
                required = true,
                example = "2023-01-01"
            )
            @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @Parameter(
                description = "Fecha hasta la cual filtrar las consultas (formato ISO)", 
                required = true,
                example = "2023-12-31"
            )
            @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @Parameter(
                description = "Especialidad médica", 
                example = "Cardiología"
            )
            @RequestParam(required = false) String especialidad,
            @Parameter(
                description = "Nombre del médico tratante", 
                example = "Dr. Juan Pérez"
            )
            @RequestParam(required = false) String medicoTratante,
            @Parameter(
                description = "Estado de la consulta", 
                example = "COMPLETADA"
            )
            @RequestParam(required = false) EstadoConsulta estado,
            @Parameter(
                description = "Número de página (empieza en 0)", 
                example = "0"
            )
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(
                description = "Tamaño de página", 
                example = "10"
            )
            @RequestParam(defaultValue = "10") int tamanio) {
        
        log.info("Buscando consultas externas con filtros avanzados");
        
        try {
            BuscarConsultasExternasPorFechaQuery query = 
                    BuscarConsultasExternasPorFechaQuery.builder()
                            .fechaDesde(fechaDesde)
                            .fechaHasta(fechaHasta)
                            .especialidad(especialidad)
                            .medicoTratante(medicoTratante)
                            .estado(estado)
                            .pagina(pagina)
                            .tamanio(tamanio)
                            .build();
            
            Page<ConsultaExternaResumenDto> consultasPage = queryHandler.handle(query);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consultas externas encontradas exitosamente",
                    "data", consultasPage.getContent(),
                    "pagination", Map.of(
                            "totalElements", consultasPage.getTotalElements(),
                            "totalPages", consultasPage.getTotalPages(),
                            "currentPage", consultasPage.getNumber(),
                            "pageSize", consultasPage.getSize()
                    ),
                    "timestamp", LocalDateTime.now()
            );
            
            log.info("Encontradas {} consultas externas con filtros aplicados", 
                    consultasPage.getTotalElements());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al buscar consultas externas con filtros: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(
        summary = "Actualizar una consulta externa existente",
        description = "Actualiza los datos de una consulta externa médica existente a partir de su identificador único",
        tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consulta externa actualizada exitosamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos o ID no coincidente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Consulta externa no encontrada",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Consulta externa no editable",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarConsultaExterna(
            @Parameter(
                description = "ID de la consulta externa a actualizar", 
                required = true,
                example = "60f1a5c2e8f87a2b94c12345"
            )
            @PathVariable String id,
            @Parameter(
                description = "Datos actualizados de la consulta externa", 
                required = true,
                schema = @Schema(implementation = ActualizarConsultaExternaCommand.class)
            )
            @Valid @RequestBody ActualizarConsultaExternaCommand command) {
        
        log.info("Actualizando consulta externa con ID: {}", id);
        
        try {
            // Asegurar que el ID en el path coincida con el ID en el comando
            if (!id.equals(command.getId())) {
                throw new IllegalArgumentException("El ID en la URL no coincide con el ID en el cuerpo de la solicitud");
            }
            
            commandHandler.handle(command);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consulta externa actualizada exitosamente",
                    "data", Map.of("id", id),
                    "timestamp", LocalDateTime.now()
            );
            
            log.info("Consulta externa actualizada exitosamente con ID: {}", id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al actualizar consulta externa: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(
        summary = "Eliminar una consulta externa",
        description = "Elimina lógicamente una consulta externa médica existente a partir de su identificador único",
        tags = {"Consultas Externas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consulta externa eliminada exitosamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetros de entrada inválidos",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Consulta externa no encontrada",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Consulta externa no eliminable",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarConsultaExterna(
            @Parameter(
                description = "ID de la consulta externa a eliminar", 
                required = true,
                example = "60f1a5c2e8f87a2b94c12345"
            )
            @PathVariable String id,
            @Parameter(
                description = "Motivo de la eliminación", 
                required = true,
                example = "Registro duplicado"
            )
            @RequestParam String motivo,
            @Parameter(
                description = "Usuario que realiza la eliminación", 
                example = "admin.sistema"
            )
            @RequestParam(defaultValue = "sistema") String usuario) {
        
        log.info("Eliminando consulta externa con ID: {}, Motivo: {}", id, motivo);
        
        try {
            // Validar parámetros de entrada
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("El ID no puede estar vacío");
            }
        
            if (motivo == null || motivo.trim().isEmpty()) {
                throw new IllegalArgumentException("El motivo de eliminación es obligatorio");
            }
        
            // Crear comando de eliminación
            EliminarConsultaExternaCommand command = EliminarConsultaExternaCommand.builder()
                    .id(id)
                    .usuarioEliminador(usuario)
                    .motivoEliminacion(motivo)
                    .build();
        
            // Ejecutar eliminación
            commandHandler.handle(command);
        
            // Respuesta exitosa
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Consulta externa eliminada exitosamente",
                    "data", Map.of(
                            "id", id,
                            "motivo", motivo,
                            "usuario", usuario
                    ),
                    "timestamp", LocalDateTime.now()
            );
        
            log.info("Consulta externa eliminada exitosamente con ID: {}", id);
        
            return ResponseEntity.ok(response);
        
        } catch (Exception e) {
            log.error("Error al eliminar consulta externa: {}", e.getMessage(), e);
            throw e;
        }
    }
}