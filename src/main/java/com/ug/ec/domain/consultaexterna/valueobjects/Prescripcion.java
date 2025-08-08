package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Prescripcion {
    
    @NotBlank(message = "El nombre del medicamento es obligatorio")
    private String medicamento;
    
    @NotBlank(message = "La dosis es obligatoria")
    private String dosis;
    
    @NotBlank(message = "La frecuencia es obligatoria")
    private String frecuencia;
    
    @NotBlank(message = "La vía de administración es obligatoria")
    private String viaAdministracion;
    
    @Positive(message = "La duración debe ser mayor a cero")
    private Integer duracionDias;
    
    private String indicaciones;
    
    @NotNull(message = "La fecha de prescripción es obligatoria")
    @Builder.Default
    private LocalDate fechaPrescripcion = LocalDate.now();
    
    private BigDecimal cantidad;
    private String unidadMedida;
    
    // Nuevos campos
    private String codigoDiagnosticoRelacionado;
    private String descripcionDiagnosticoRelacionado;
    private String concentracion;
    private String presentacion;
    private String laboratorio;
    private String instruccionesEspeciales;
    private Boolean requiereRecetaEspecial;
    private String justificacionClinica;
    private List<String> advertencias;
    private List<String> contraindicaciones;
    private List<String> efectosSecundarios;
    private String alternativas;
    private Boolean medicamentoGenerico;
    
    public boolean esValida() {
        return medicamento != null && !medicamento.trim().isEmpty() &&
               dosis != null && !dosis.trim().isEmpty() &&
               frecuencia != null && !frecuencia.trim().isEmpty() &&
               duracionDias != null && duracionDias > 0;
    }
    
    public String obtenerResumen() {
        return String.format("%s - %s %s por %d días", 
                medicamento, dosis, frecuencia, duracionDias);
    }
    
    /**
     * Determina si el medicamento es controlado (narcóticos, psicotrópicos, etc.)
     * @return true si el medicamento es controlado, false en caso contrario
     */
    public boolean esControlada() {
        if (medicamento == null) return false;
        
        String med = medicamento.toLowerCase();
        
        // Lista de palabras clave que indican medicamentos controlados
        return med.contains("narcótico") || 
               med.contains("narcotico") || 
               med.contains("opioide") || 
               med.contains("opiáceo") || 
               med.contains("opiaceo") || 
               med.contains("benzodiacepina") || 
               med.contains("anfetamina") || 
               med.contains("barbitúrico") || 
               med.contains("barbiturico") || 
               med.contains("psicotrópico") || 
               med.contains("psicotropico") || 
               med.contains("estupefaciente") ||
               // Medicamentos específicos controlados comunes
               med.contains("morfina") || 
               med.contains("fentanilo") || 
               med.contains("oxicodona") || 
               med.contains("metadona") || 
               med.contains("tramadol") || 
               med.contains("diazepam") || 
               med.contains("alprazolam") || 
               med.contains("clonazepam") || 
               med.contains("lorazepam") || 
               med.contains("midazolam") ||
               // Verificación explícita
               (requiereRecetaEspecial != null && requiereRecetaEspecial);
    }
    
    /**
     * Determina si el medicamento es un antibiótico
     * @return true si el medicamento es un antibiótico, false en caso contrario
     */
    public boolean esAntibiotico() {
        if (medicamento == null) return false;
        
        String med = medicamento.toLowerCase();
        
        return med.contains("antibiótico") || 
               med.contains("antibiotico") || 
               med.contains("penicilina") || 
               med.contains("amoxicilina") || 
               med.contains("ampicilina") || 
               med.contains("cefalosporina") || 
               med.contains("ciprofloxacino") || 
               med.contains("levofloxacino") || 
               med.contains("azitromicina") || 
               med.contains("claritromicina") || 
               med.contains("eritromicina") || 
               med.contains("tetraciclina") || 
               med.contains("doxiciclina") || 
               med.contains("metronidazol") || 
               med.contains("clindamicina") || 
               med.contains("vancomicina") || 
               med.contains("sulfametoxazol") || 
               med.contains("trimetoprima");
    }
    
    /**
     * Determina si el medicamento es un analgésico
     * @return true si el medicamento es un analgésico, false en caso contrario
     */
    public boolean esAnalgesico() {
        if (medicamento == null) return false;
        
        String med = medicamento.toLowerCase();
        
        return med.contains("analgésico") || 
               med.contains("analgesico") || 
               med.contains("paracetamol") || 
               med.contains("acetaminofén") || 
               med.contains("acetaminofen") || 
               med.contains("ibuprofeno") || 
               med.contains("naproxeno") || 
               med.contains("diclofenaco") || 
               med.contains("ketorolaco") || 
               med.contains("tramadol") || 
               med.contains("codeína") || 
               med.contains("codeina") || 
               med.contains("morfina") || 
               med.contains("fentanilo") || 
               med.contains("oxicodona") || 
               med.contains("aspirina") || 
               med.contains("ácido acetilsalicílico") || 
               med.contains("acido acetilsalicilico");
    }
    
    // Nuevos métodos de dominio
    public boolean requiereMonitorizacion() {
        return esControlada() || 
               (advertencias != null && !advertencias.isEmpty()) ||
               (contraindicaciones != null && !contraindicaciones.isEmpty());
    }
    
    public String generarInstruccionesCompletas() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Tomar %s de %s %s", dosis, medicamento, frecuencia));
        
        if (viaAdministracion != null && !viaAdministracion.isEmpty()) {
            sb.append(String.format(" por vía %s", viaAdministracion));
        }
        
        if (duracionDias != null) {
            sb.append(String.format(" durante %d días", duracionDias));
        }
        
        if (indicaciones != null && !indicaciones.isEmpty()) {
            sb.append(String.format(". %s", indicaciones));
        }
        
        if (instruccionesEspeciales != null && !instruccionesEspeciales.isEmpty()) {
            sb.append(String.format(". %s", instruccionesEspeciales));
        }
        
        return sb.toString();
    }
    
    public static Prescripcion crear(String medicamento, String dosis, String frecuencia, Integer duracion) {
        return Prescripcion.builder()
                .medicamento(medicamento)
                .dosis(dosis)
                .frecuencia(frecuencia)
                .duracionDias(duracion)
                .viaAdministracion("Oral") // Default
                .fechaPrescripcion(LocalDate.now())
                .build();
    }
}