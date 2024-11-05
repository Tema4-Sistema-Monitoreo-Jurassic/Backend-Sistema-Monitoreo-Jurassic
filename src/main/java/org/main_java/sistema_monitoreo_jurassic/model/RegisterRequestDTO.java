package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDTO {
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String correo;
    private Integer telefono;
    private String direccion;
    private String password;  // Contraseña que le asigna el usuario
    private String rolId;     // id para el rol del usuario opcional de momento
}