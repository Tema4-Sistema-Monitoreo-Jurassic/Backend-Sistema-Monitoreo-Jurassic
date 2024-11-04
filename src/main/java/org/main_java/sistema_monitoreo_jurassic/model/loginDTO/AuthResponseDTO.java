package org.main_java.sistema_monitoreo_jurassic.model.loginDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.main_java.sistema_monitoreo_jurassic.model.UsuarioDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String mensaje;
    private String token;
    private String rol;
    private String userId; // Usamos String si es consistente con el tipo de ID en UsuarioDTO
    private UsuarioDTO usuario; // Cambiamos a UsuarioDTO para mantener la consistencia
}
