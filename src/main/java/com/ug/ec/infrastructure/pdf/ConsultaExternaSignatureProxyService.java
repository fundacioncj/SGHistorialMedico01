package com.ug.ec.infrastructure.pdf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ConsultaExternaSignatureProxyService {

    @Value("${firma.digital.core.url:http://localhost:8080/api/firma/firmar-pdf}")
    private String coreUrl;

    // Coordenadas de la firma digital en el PDF (A4: 595x842 puntos)
    @Value("${firma.digital.posicion.x:350}")
    private float signatureX;

    @Value("${firma.digital.posicion.y:55}")
    private float signatureY;

    @Value("${firma.digital.posicion.width:200}")
    private float signatureWidth;

    @Value("${firma.digital.posicion.height:50}")
    private float signatureHeight;

    @Value("${firma.digital.posicion.page:0}")
    private int signaturePage; // 0 = última página

    private final RestTemplate restTemplate;

    public ConsultaExternaSignatureProxyService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Firma un PDF remotamente usando el servicio de firma digital del Core
     *
     * @param pdfOriginal PDF original en bytes
     * @param cedula Cédula del profesional que firma
     * @param pin PIN del certificado digital
     * @param authToken Token de autorización (Bearer token)
     * @return PDF firmado digitalmente en bytes
     */
    public byte[] firmarRemotamente(byte[] pdfOriginal, String cedula, String pin, String authToken) {
        log.info("Solicitando firma remota al Core para cédula: {}", cedula);

        try {
            // Preparar Multipart Request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(pdfOriginal) {
                @Override
                public String getFilename() {
                    return "document.pdf";
                }
            });
            body.add("cedula", cedula);
            body.add("pin", pin);

            // Coordenadas de posición de la firma
            body.add("x", String.valueOf(signatureX));
            body.add("y", String.valueOf(signatureY));
            body.add("width", String.valueOf(signatureWidth));
            body.add("height", String.valueOf(signatureHeight));
            body.add("page", String.valueOf(signaturePage));

            log.debug("Posición de firma: x={}, y={}, width={}, height={}, page={}",
                    signatureX, signatureY, signatureWidth, signatureHeight, signaturePage);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Añadir token de autorización si está presente
            if (authToken != null && !authToken.isEmpty()) {
                headers.set(HttpHeaders.AUTHORIZATION, authToken);
                log.debug("Token de autorización incluido en la petición");
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Llamada al Core
            ResponseEntity<byte[]> response = restTemplate.postForEntity(coreUrl, requestEntity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Firma remota exitosa.");
                return response.getBody();
            } else {
                throw new RuntimeException("Error en firma remota. Core respondió: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.NOT_FOUND) {
                String msg = "PIN incorrecto o certificado no encontrado";
                if (e.getResponseHeaders() != null && e.getResponseHeaders().containsKey("X-Error-Message")) {
                    msg = e.getResponseHeaders().getFirst("X-Error-Message");
                }
                throw new RuntimeException(msg, e);
            }
            throw new RuntimeException("Error comunicando con servicio de firma", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado en firma remota", e);
        }
    }
}