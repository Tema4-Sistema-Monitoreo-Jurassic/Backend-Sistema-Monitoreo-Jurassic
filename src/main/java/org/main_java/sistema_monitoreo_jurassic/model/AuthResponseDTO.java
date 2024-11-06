package org.main_java.sistema_monitoreo_jurassic.model;

import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String message;
    private String token;      // Para el token de autenticación
    private String role;       // Rol del usuario
}

