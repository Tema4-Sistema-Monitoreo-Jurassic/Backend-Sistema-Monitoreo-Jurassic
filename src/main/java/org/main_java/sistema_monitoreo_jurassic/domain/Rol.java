package org.main_java.sistema_monitoreo_jurassic.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Getter
@Setter
@Document(collection = "roles")
public class Rol {

    @Id
    private String id;

    @Field(name = "nombre")
    private String nombre;

    private Set<String> usuarios;

    public Rol(String id, String nombre, Set<String> usuarios) {
        this.id = id;
        this.nombre = nombre;
        this.usuarios = usuarios;
    }

    // Add this constructor
    public Rol(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}