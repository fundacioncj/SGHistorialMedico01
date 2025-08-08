package com.ug.ec.domain.consultaexterna.valueobjects;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import com.ug.ec.domain.consultaexterna.enums.Sexo;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.time.Period;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class DatosPaciente {
    
    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "^\\d{10}$", message = "La cédula debe tener 10 dígitos")
    private String cedula;
    
    @NotBlank(message = "El número de historia clínica es obligatorio")
    private String numeroHistoriaClinica;
    
    @NotBlank(message = "El primer nombre es obligatorio")
    private String primerNombre;
    
    private String segundoNombre;
    
    @NotBlank(message = "El apellido paterno es obligatorio")
    private String apellidoPaterno;
    
    private String apellidoMaterno;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;
    
    @NotNull(message = "El sexo es obligatorio")
    private Sexo sexo;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
    private String telefono;
    
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private String contactoEmergencia;
    private String telefonoEmergencia;
    
    // Métodos de dominio
    public int calcularEdad() {
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }
    
    public String obtenerNombreCompleto() {
        StringBuilder nombre = new StringBuilder();
        nombre.append(this.primerNombre);
        
        if (this.segundoNombre != null) {
            nombre.append(" ").append(this.segundoNombre);
        }
        
        nombre.append(" ").append(this.apellidoPaterno);
        
        if (this.apellidoMaterno != null) {
            nombre.append(" ").append(this.apellidoMaterno);
        }
        
        return nombre.toString();
    }
    
    public boolean esMayorDeEdad() {
        return calcularEdad() >= 18;
    }
}