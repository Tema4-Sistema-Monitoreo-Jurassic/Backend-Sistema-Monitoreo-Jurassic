package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class CredencialesDTO {
    private String id;
    private String username;
    private String password;  // Incluir solo si realmente lo necesitas
    private String usuarioId; // Relación con el usuario en forma de ID
}
