package com.ug.ec.domain.historiaclinica;


import com.ug.ec.domain.consultaexterna.valueobjects.DatosAuditoria;
import com.ug.ec.domain.historiaclinica.enums.TipoAntecedente;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoFamiliar;
import com.ug.ec.domain.historiaclinica.valueobjects.AntecedentePatologicoPersonal;
import com.ug.ec.domain.historiaclinica.valueobjects.DatosFormulario;
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
@Document("historias_clinicas")
public class HistoriaClinica {

    @Id
    private String id;

    private String idPaciente;
    private String cedulaPaciente;

    private DatosFormulario datosFormulario;

    @Builder.Default
    private List<AntecedentePatologicoPersonal> antecedentesPatologicosPersonales = new ArrayList<>();

    @Builder.Default
    private List<AntecedentePatologicoFamiliar> antecedentesPatologicosFamiliares = new ArrayList<>();

    private LocalDateTime fechaUltimaActualizacion;
    private DatosAuditoria auditoria;

    // Campos para eliminado lógico
    private Boolean activo;
    private LocalDateTime fechaEliminacion;
    private String usuarioEliminacion;

    // ============= MÉTODOS DE NEGOCIO =============

    /**
     * Actualiza los datos del formulario
     */
    public void actualizarDatosFormulario(DatosFormulario nuevosDatos, String usuario) {
        nuevosDatos.validar();
        this.datosFormulario = nuevosDatos;
        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.auditoria = this.auditoria.actualizar(usuario);
    }

    /**
     * Agrega un antecedente patológico personal
     */
    public void agregarAntecedentePersonal(AntecedentePatologicoPersonal antecedente, String usuario) {
        antecedente.validar();

        if (this.antecedentesPatologicosPersonales == null) {
            this.antecedentesPatologicosPersonales = new ArrayList<>();
        }

        // Validar que no exista el mismo tipo ya registrado
        boolean existeTipo = this.antecedentesPatologicosPersonales.stream()
                .anyMatch(a -> a.getTipo() == antecedente.getTipo());

        if (existeTipo) {
            throw new IllegalStateException(
                    "Ya existe un antecedente personal del tipo: " + antecedente.getTipo().getDescripcion());
        }

        this.antecedentesPatologicosPersonales.add(antecedente);
        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.auditoria = this.auditoria.actualizar(usuario);
    }

    /**
     * Agrega un antecedente patológico familiar
     */
    public void agregarAntecedenteFamiliar(AntecedentePatologicoFamiliar antecedente, String usuario) {
        antecedente.validar();

        if (this.antecedentesPatologicosFamiliares == null) {
            this.antecedentesPatologicosFamiliares = new ArrayList<>();
        }

        this.antecedentesPatologicosFamiliares.add(antecedente);
        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.auditoria = this.auditoria.actualizar(usuario);
    }

    /**
     * Actualiza un antecedente personal existente
     */
    public void actualizarAntecedentePersonal(
            AntecedentePatologicoPersonal antecedenteActualizado, String usuario) {

        antecedenteActualizado.validar();

        boolean encontrado = false;
        for (int i = 0; i < this.antecedentesPatologicosPersonales.size(); i++) {
            if (this.antecedentesPatologicosPersonales.get(i).getTipo() ==
                    antecedenteActualizado.getTipo()) {
                this.antecedentesPatologicosPersonales.set(i, antecedenteActualizado);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new IllegalArgumentException(
                    "No existe un antecedente personal del tipo: " +
                            antecedenteActualizado.getTipo().getDescripcion());
        }

        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.auditoria = this.auditoria.actualizar(usuario);
    }

    /**
     * Elimina un antecedente personal
     */
    public void eliminarAntecedentePersonal(TipoAntecedente tipo, String usuario) {
        boolean eliminado = this.antecedentesPatologicosPersonales.removeIf(
                a -> a.getTipo() == tipo);

        if (!eliminado) {
            throw new IllegalArgumentException(
                    "No existe un antecedente personal del tipo: " + tipo.getDescripcion());
        }

        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.auditoria = this.auditoria.actualizar(usuario);
    }

    /**
     * Elimina un antecedente familiar
     */
    public void eliminarAntecedenteFamiliar(TipoAntecedente tipo, String parentesco, String usuario) {
        boolean eliminado = this.antecedentesPatologicosFamiliares.removeIf(
                a -> a.getTipo() == tipo &&
                        (parentesco == null || a.getParentesco().equals(parentesco)));

        if (!eliminado) {
            throw new IllegalArgumentException("No se encontró el antecedente familiar a eliminar");
        }

        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.auditoria = this.auditoria.actualizar(usuario);
    }

    /**
     * Eliminación lógica de la historia clínica
     */
    public void eliminarLogicamente(String usuarioEliminacion) {
        this.activo = false;
        this.fechaEliminacion = LocalDateTime.now();
        this.usuarioEliminacion = usuarioEliminacion;
    }

    /**
     * Reactiva una historia clínica eliminada
     */
    public void activar() {
        this.activo = true;
        this.fechaEliminacion = null;
        this.usuarioEliminacion = null;
    }

    // ============= VALIDACIONES =============

    /**
     * Valida la integridad de la historia clínica
     */
    public void validar() {
        if (idPaciente == null || idPaciente.isBlank()) {
            throw new IllegalArgumentException("El ID del paciente es obligatorio");
        }

        if (cedulaPaciente == null || cedulaPaciente.isBlank()) {
            throw new IllegalArgumentException("La cédula del paciente es obligatoria");
        }

        if (datosFormulario == null) {
            throw new IllegalArgumentException("Los datos del formulario son obligatorios");
        }

        datosFormulario.validar();

        if (auditoria == null) {
            throw new IllegalArgumentException("Los datos de auditoría son obligatorios");
        }
    }

    /**
     * Verifica si la historia clínica está completa
     */
    public boolean estaCompleta() {
        return datosFormulario != null &&
                antecedentesPatologicosPersonales != null &&
                !antecedentesPatologicosPersonales.isEmpty() &&
                antecedentesPatologicosFamiliares != null &&
                !antecedentesPatologicosFamiliares.isEmpty();
    }

    /**
     * Obtiene un resumen de la historia clínica
     */
    public String obtenerResumen() {
        return String.format(
                "Historia Clínica - Paciente: %s, Antecedentes Personales: %d, Antecedentes Familiares: %d",
                cedulaPaciente,
                antecedentesPatologicosPersonales != null ? antecedentesPatologicosPersonales.size() : 0,
                antecedentesPatologicosFamiliares != null ? antecedentesPatologicosFamiliares.size() : 0
        );
    }
}