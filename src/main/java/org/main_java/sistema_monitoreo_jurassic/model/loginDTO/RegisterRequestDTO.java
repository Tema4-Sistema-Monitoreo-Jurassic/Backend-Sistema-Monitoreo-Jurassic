package org.main_java.sistema_monitoreo_jurassic.model.loginDTO;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String correo;
    private Integer telefono;
    private String direccion;
    private String contrasena;
    private String rolId; // ID del Rol relacionado, si es necesario
    private String poder; // Campo adicional si se usa en el registro
}

