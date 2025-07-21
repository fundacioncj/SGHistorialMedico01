package com.ug.ec.domain;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "pacientes")
public class Paciente {

    @Id
    private String id;

    @Indexed
    private String historiaClinica;


    private Long idPersona;


    private String identificacion;
    private String primerApellido;
    private String segundoApellido;
    private String primerNombre;
    private String segundoNombre;
}
