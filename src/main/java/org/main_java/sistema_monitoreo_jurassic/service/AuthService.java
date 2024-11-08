package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.Credenciales;
import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
import org.main_java.sistema_monitoreo_jurassic.model.AuthResponseDTO;
import org.main_java.sistema_monitoreo_jurassic.model.LoginRequestDTO;
import org.main_java.sistema_monitoreo_jurassic.model.RegisterRequestDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.CredencialesRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.RolRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    // Inyección de dependencias para los repositorios
    @Autowired
    private UsuarioRepository usuarioRepository;
    // Inyección de dependencias para los repositorios
    @Autowired
    private CredencialesRepository credencialesRepository;
    // Inyección de dependencias para los repositorios
    @Autowired
    private RolRepository rolRepository;
    // Inyección de dependencias para el password encoder
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // metodo para realizar el login
    public Mono<ResponseEntity<AuthResponseDTO>> login(LoginRequestDTO loginRequest) {
        return usuarioRepository.findByCorreo(loginRequest.getCorreo())
                .flatMap(usuario -> credencialesRepository.findById(usuario.getCredencialesId())
                        .flatMap(credenciales -> {
                            if (passwordEncoder.matches(loginRequest.getPassword(), credenciales.getPassword())) {
                                return Mono.just(ResponseEntity.ok(new AuthResponseDTO(
                                        "Login exitoso", "FAKE_JWT_TOKEN", usuario.getRolId())));
                            } else {
                                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new AuthResponseDTO("Credenciales incorrectas", null, null)));
                            }
                        })
                )
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new AuthResponseDTO("Usuario no encontrado", null, null))));
    }



    // metodo para realizar el registro
    public Mono<ResponseEntity<AuthResponseDTO>> register(RegisterRequestDTO registerRequest) {
        return usuarioRepository.findByCorreo(registerRequest.getCorreo())
                .flatMap(existingUser -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponseDTO("El usuario ya existe", null, null))))
                .switchIfEmpty(
                        rolRepository.findByNombre(registerRequest.getRolNombre())
                                .flatMap(rol -> {
                                    // Role found, proceed with user creation
                                    Credenciales credenciales = new Credenciales();
                                    credenciales.setUsername(registerRequest.getCorreo());
                                    credenciales.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

                                    return credencialesRepository.save(credenciales)
                                            .flatMap(savedCredenciales -> {
                                                // Crea un nuevo usuario con los datos del request
                                                Usuario nuevoUsuario = new Usuario();
                                                nuevoUsuario.setNombre(registerRequest.getNombre());
                                                nuevoUsuario.setApellido1(registerRequest.getApellido1());
                                                nuevoUsuario.setApellido2(registerRequest.getApellido2());
                                                nuevoUsuario.setCorreo(registerRequest.getCorreo());
                                                nuevoUsuario.setTelefono(registerRequest.getTelefono());
                                                nuevoUsuario.setDireccion(registerRequest.getDireccion());
                                                nuevoUsuario.setCredencialesId(savedCredenciales.getId());
                                                nuevoUsuario.setRolId(rol.getId()); // Set the role's actual ID

                                                return usuarioRepository.save(nuevoUsuario)
                                                        .map(savedUsuario -> ResponseEntity.ok(
                                                                new AuthResponseDTO("Usuario registrado con éxito", null, rol.getNombre())));
                                            });
                                })
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Rol no válido.")))
                );
    }
}

