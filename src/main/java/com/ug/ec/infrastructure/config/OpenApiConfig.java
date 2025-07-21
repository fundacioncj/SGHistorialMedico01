package com.ug.ec.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Value("${spring.application.name:SGHistorialMedico}")
    private String applicationName;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " - API de Consultas Externas")
                        .version("1.0.0")
                        .description("API REST para la gestión de consultas externas del formulario HCU-002 del Sistema de Gestión de Historial Médico")
                        .termsOfService("https://universidad-guayaquil.ec/terminos")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo UG")
                                .email("desarrollo@ug.edu.ec")
                                .url("https://universidad-guayaquil.ec"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api-test.hospital.ec")
                                .description("Servidor de Testing"),
                        new Server()
                                .url("https://api.hospital.ec")
                                .description("Servidor de Producción")
                ))
                .components(new Components()
                        .responses(createApiResponses()));
    }
    
    private Map<String, ApiResponse> createApiResponses() {
        Map<String, ApiResponse> responses = new HashMap<>();
        
        // Respuesta de error 400 - Bad Request
        responses.put("badRequest", new ApiResponse()
                .description("Solicitud inválida - Los datos proporcionados no son válidos")
                .content(createErrorResponseContent("Error de validación en los datos de entrada")));
        
        // Respuesta de error 404 - Not Found
        responses.put("notFound", new ApiResponse()
                .description("Recurso no encontrado")
                .content(createErrorResponseContent("El recurso solicitado no existe")));
        
        // Respuesta de error 409 - Conflict
        responses.put("conflict", new ApiResponse()
                .description("Conflicto con el estado actual del recurso")
                .content(createErrorResponseContent("La operación no puede completarse debido a un conflicto")));
        
        // Respuesta de error 500 - Internal Server Error
        responses.put("internalServerError", new ApiResponse()
                .description("Error interno del servidor")
                .content(createErrorResponseContent("Ha ocurrido un error interno en el servidor")));
        
        return responses;
    }
    
    private Content createErrorResponseContent(String defaultMessage) {
        Map<String, Object> example = new HashMap<>();
        example.put("success", false);
        example.put("message", defaultMessage);
        example.put("error", "Descripción detallada del error");
        example.put("timestamp", "2023-07-19T15:30:45.123Z");
        
        Schema<?> schema = new Schema<>()
                .type("object")
                .example(example);
        
        return new Content()
                .addMediaType("application/json", new MediaType().schema(schema));
    }
}