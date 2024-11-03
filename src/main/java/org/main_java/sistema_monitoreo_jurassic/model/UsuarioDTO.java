package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String id;
    private String nombre;
    private String email;
    private String rol;
}