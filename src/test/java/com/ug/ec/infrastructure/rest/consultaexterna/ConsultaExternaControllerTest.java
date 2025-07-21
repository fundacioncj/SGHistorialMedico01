package com.ug.ec.infrastructure.rest.consultaexterna;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ug.ec.application.consultaexterna.commands.CrearConsultaExternaCommand;
import com.ug.ec.application.consultaexterna.dto.ConsultaExternaDto;
import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaCommandHandler;
import com.ug.ec.application.consultaexterna.handlers.ConsultaExternaQueryHandler;
import com.ug.ec.application.consultaexterna.queries.BuscarConsultaExternaPorIdQuery;
import com.ug.ec.domain.consultaexterna.enums.EstadoConsulta;
import com.ug.ec.domain.consultaexterna.exceptions.ConsultaExternaNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConsultaExternaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ConsultaExternaCommandHandler commandHandler;

    @Mock
    private ConsultaExternaQueryHandler queryHandler;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper to handle Java 8 date/time types
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Create controller with mocked dependencies
        ConsultaExternaController controller = new ConsultaExternaController(commandHandler, queryHandler);

        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void obtenerConsultaExternaPorId_DebeRetornarConsulta_CuandoExiste() throws Exception {
        // Arrange
        String consultaId = "test-id";
        ConsultaExternaDto consultaDto = new ConsultaExternaDto();
        consultaDto.setId(consultaId);
        consultaDto.setNumeroConsulta("CE-123456");
        consultaDto.setEstado(EstadoConsulta.COMPLETADA);

        when(queryHandler.handle(any(BuscarConsultaExternaPorIdQuery.class))).thenReturn(consultaDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/consultas-externas/{id}", consultaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(consultaId))
                .andExpect(jsonPath("$.data.numeroConsulta").value("CE-123456"))
                .andExpect(jsonPath("$.data.estado").value("COMPLETADA"));
    }

    @Test
    void obtenerConsultaExternaPorId_DebeRetornarNotFound_CuandoNoExiste() throws Exception {
        // Arrange
        String consultaId = "nonexistent-id";
        when(queryHandler.handle(any(BuscarConsultaExternaPorIdQuery.class)))
                .thenThrow(new ConsultaExternaNotFoundException("Consulta externa no encontrada con ID: " + consultaId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/consultas-externas/{id}", consultaId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Consulta externa no encontrada"));
    }

    @Test
    void crearConsultaExterna_DebeRetornarCreated_CuandoDatosValidos() throws Exception {
        // Arrange
        String consultaId = "new-id";
        Map<String, Object> requestBody = new HashMap<>();

        // Datos mínimos para la solicitud
        Map<String, Object> datosFormulario = new HashMap<>();
        datosFormulario.put("numeroFormulario", "HCU-002");
        datosFormulario.put("establecimiento", "Hospital Universitario");

        Map<String, Object> datosPaciente = new HashMap<>();
        datosPaciente.put("cedula", "0987654321");
        datosPaciente.put("numeroHistoriaClinica", "HC-12345");
        datosPaciente.put("primerNombre", "Juan");
        datosPaciente.put("apellidoPaterno", "Pérez");

        Map<String, Object> datosConsulta = new HashMap<>();
        datosConsulta.put("numeroConsulta", "CE-123456");
        datosConsulta.put("fechaConsulta", LocalDateTime.now().toString());
        datosConsulta.put("especialidad", "Medicina General");
        datosConsulta.put("medicoTratante", "Dr. Ejemplo");
        datosConsulta.put("tipoConsulta", "PRIMERA_VEZ");
        datosConsulta.put("motivoConsulta", "Dolor abdominal");

        Map<String, Object> anamnesis = new HashMap<>();
        anamnesis.put("enfermedadActual", "Dolor abdominal de 2 días de evolución");

        requestBody.put("datosFormulario", datosFormulario);
        requestBody.put("datosPaciente", datosPaciente);
        requestBody.put("datosConsulta", datosConsulta);
        requestBody.put("anamnesis", anamnesis);

        when(commandHandler.handle(any(CrearConsultaExternaCommand.class))).thenReturn(consultaId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/consultas-externas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(consultaId));
    }
}
