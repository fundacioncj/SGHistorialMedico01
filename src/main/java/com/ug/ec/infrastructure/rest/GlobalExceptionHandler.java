package com.ug.ec.infrastructure.rest;

import com.ug.ec.domain.consultaexterna.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, String error) {
        Map<String, Object> body = Map.of(
                "success", false,
                "message", message,
                "error", error,
                "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ConsultaExternaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ConsultaExternaNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "Consulta externa no encontrada", e.getMessage());
    }

    @ExceptionHandler({ConsultaExternaDuplicadaException.class, ConsultaExternaNoEditableException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException e) {
        String message = (e instanceof ConsultaExternaDuplicadaException) ? "Consulta externa duplicada"
                : (e instanceof ConsultaExternaNoEditableException) ? "Consulta externa no editable"
                : "Operación en conflicto con el estado actual";
        return build(HttpStatus.CONFLICT, message, e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, DatosPacienteInvalidosException.class, DatosConsultaInvalidosException.class,
            AnamnesisInvalidaException.class, ExamenFisicoInvalidoException.class, DiagnosticoInvalidoException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException e) {
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException e) {
        String errors = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", errors);
    }

    @ExceptionHandler(ConsultaExternaConsultaException.class)
    public ResponseEntity<Map<String, Object>> handleConsultaException(ConsultaExternaConsultaException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConsultaExternaNotFoundException nf) {
            return handleNotFound(nf);
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la consulta externa", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        log.error("Error no controlado", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", e.getMessage());
    }
}
