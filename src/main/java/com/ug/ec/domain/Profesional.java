package com.ug.ec.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "profesionales")
public class Profesional {
    @Id
    private String id;
    private Long idProfesional;
    private String identificacion;
    private String primerApellido;
    private String segundoApellido;

}
