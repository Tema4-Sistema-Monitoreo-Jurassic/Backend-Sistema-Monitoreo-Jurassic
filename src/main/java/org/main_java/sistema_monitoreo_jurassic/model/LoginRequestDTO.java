package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {
    private String nombreOrCorreo; // Campo para nombre o correo
    private String password;
}
