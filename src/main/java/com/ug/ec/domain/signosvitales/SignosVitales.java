package com.ug.ec.domain.signosvitales;

import com.ug.ec.domain.signosvitales.valueobjects.TomaSignos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("signos_vitales")
public class SignosVitales {

    @Id
    private String id;
    private String citaId;
    private String pacienteId;
    private String usuarioRegistro;
    private LocalDateTime fechaRegistro;

    @Builder.Default
    private List<TomaSignos> tomas = new ArrayList<>();

    // Campos para eliminado lógico
    private Boolean activo;
    private LocalDateTime fechaEliminacion;
    private String usuarioEliminacion;

    // Métodos de negocio
    public void agregarToma(TomaSignos toma) {
        if (this.tomas == null) {
            this.tomas = new ArrayList<>();
        }

        if (this.tomas.size() >= 2) {
            throw new IllegalStateException("No se pueden agregar más de 2 tomas por registro");
        }

        // Calcular IMC automáticamente
        if (toma.getPeso() != null && toma.getTalla() != null && toma.getTalla() > 0) {
            double tallaMts = toma.getTalla() / 100.0;
            double imc = toma.getPeso() / (tallaMts * tallaMts);
            toma.setImc(Math.round(imc * 100.0) / 100.0);
        }

        this.tomas.add(toma);
    }

    public void validarPresionArterial(TomaSignos toma) {
        if (toma.getPresionSistolica() != null && toma.getPresionDiastolica() != null) {
            toma.setPresionArterial(toma.getPresionSistolica() + "/" + toma.getPresionDiastolica());
        }
    }

    public void eliminarLogicamente(String usuarioEliminacion) {
        this.activo = false;
        this.fechaEliminacion = LocalDateTime.now();
        this.usuarioEliminacion = usuarioEliminacion;
    }

    public void activar() {
        this.activo = true;
        this.fechaEliminacion = null;
        this.usuarioEliminacion = null;
    }

    // Validaciones de negocio
    public void validar() {
        if (citaId == null || citaId.isBlank()) {
            throw new IllegalArgumentException("El ID de la cita es obligatorio");
        }
        if (pacienteId == null || pacienteId.isBlank()) {
            throw new IllegalArgumentException("El ID del paciente es obligatorio");
        }
        if (usuarioRegistro == null || usuarioRegistro.isBlank()) {
            throw new IllegalArgumentException("El usuario de registro es obligatorio");
        }
    }
}