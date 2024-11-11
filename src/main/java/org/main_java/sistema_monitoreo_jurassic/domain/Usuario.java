package org.main_java.sistema_monitoreo_jurassic.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    @Field(name = "nombre")
    private String nombre;

    @Field(name = "apellido1")
    private String apellido1;

    @Field(name = "apellido2")
    private String apellido2;

    @Field(name = "correo")
    private String correo;

    @Field(name = "telefono")
    private Integer telefono;

    @Field(name = "direccion")
    private String direccion;

    private String rolId;  // ID del Rol relacionado
    private String credencialesId;  // ID de las Credenciales relacionadas
}
