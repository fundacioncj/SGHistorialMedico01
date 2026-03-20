package com.ug.ec.infrastructure.pdf;

import com.ug.ec.infrastructure.pdf.dto.CoreMedicoPdfResponse;
import com.ug.ec.infrastructure.pdf.dto.CorePacientePdfResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CoreApiClientService {

    @Value("${core.backend.url:http://localhost:8084}")
    private String coreBaseUrl;

    private final RestTemplate restTemplate;

    public CoreApiClientService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtiene datos del paciente desde el Core para el PDF.
     *
     * @param cedula    Cédula del paciente
     * @param authToken Bearer token de autenticación
     * @return Datos del paciente o null si no se encuentra
     */
    public CorePacientePdfResponse obtenerPaciente(String cedula, String authToken) {
        if (cedula == null || cedula.isBlank()) {
            return null;
        }
        String url = coreBaseUrl + "/api/pacientes/cedula/" + cedula + "/pdf";
        try {
            HttpHeaders headers = buildHeaders(authToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<CorePacientePdfResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, CorePacientePdfResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn("No se pudo obtener datos del paciente {} desde Core: {}", cedula, e.getStatusCode());
            return null;
        } catch (Exception e) {
            log.warn("Error al consultar datos del paciente {} desde Core", cedula, e);
            return null;
        }
    }

    /**
     * Obtiene datos del médico desde el Core para el PDF.
     *
     * @param cedula    Cédula del médico
     * @param authToken Bearer token de autenticación
     * @return Datos del médico o null si no se encuentra
     */
    public CoreMedicoPdfResponse obtenerMedico(String cedula, String authToken) {
        if (cedula == null || cedula.isBlank()) {
            return null;
        }
        String url = coreBaseUrl + "/api/medicos/cedula/" + cedula + "/pdf";
        try {
            HttpHeaders headers = buildHeaders(authToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<CoreMedicoPdfResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, CoreMedicoPdfResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn("No se pudo obtener datos del médico {} desde Core: {}", cedula, e.getStatusCode());
            return null;
        } catch (Exception e) {
            log.warn("Error al consultar datos del médico {} desde Core", cedula, e);
            return null;
        }
    }

    private HttpHeaders buildHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null && !authToken.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authToken);
        }
        return headers;
    }
}