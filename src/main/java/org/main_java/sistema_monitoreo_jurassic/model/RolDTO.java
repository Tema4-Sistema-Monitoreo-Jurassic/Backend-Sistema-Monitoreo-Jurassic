package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Data
@Getter
@Setter
public class RolDTO {
    private String id;
    private String nombre;
    private Set<String> usuarios;
}