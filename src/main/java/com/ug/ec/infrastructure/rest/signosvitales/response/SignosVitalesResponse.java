package com.ug.ec.infrastructure.rest.signosvitales.response;

import com.ug.ec.domain.signosvitales.valueobjects.TomaSignos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignosVitalesResponse {
    private String id;
    private String citaId;
    private String pacienteId;
    private String usuarioRegistro;
    private LocalDateTime fechaRegistro;
    private List<TomaSignos> tomas;
    private Boolean activo;
}
