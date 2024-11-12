package org.main_java.sistema_monitoreo_jurassic.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "credenciales")
public class Credenciales {

    @Id
    private String id;

    @Field(name = "nombre")
    private String nombre; // Nombre del usuario

    @Field(name = "correo")
    private String correo; // Correo electrónico del usuario

    @Field(name = "password")
    private String password; // Contraseña cifrada

    // Almacenamos el ID del usuario en lugar de una relación directa
    private String usuarioId;  // ID del Usuario relacionado
}
