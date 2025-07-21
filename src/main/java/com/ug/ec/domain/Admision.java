package com.ug.ec.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "admisiones")
public class Admision {

    @Id
    private String id;

    // Datos del establecimiento
    private String institucionSistema;
    private String establecimientoSalud;
    private String unicodigo;

   @Indexed
    private String numeroHistoriaClinica;

    private String numeroArchivo;
    // Registro de admisión
    private Date fechaAdmision;
    private String admisionista;

    // Referencia al paciente
    @DBRef
    private Paciente paciente;

    @Data
    public static class Metadata{
        private String creadoPor;
        private Date fechaCreacion;
        private Date ultimaActualizacion;
    }
}
