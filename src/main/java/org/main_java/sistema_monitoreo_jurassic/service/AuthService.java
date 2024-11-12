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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CredencialesRepository credencialesRepository;

    @Autowired
    private RolRepository rolRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // AuthService.java
    // AuthService.java
    public Mono<ResponseEntity<AuthResponseDTO>> login(LoginRequestDTO loginRequest) {
        System.out.println("Inicio de autenticación para: " + loginRequest.getNombreOrCorreo());

        return usuarioRepository.findFirstByNombre(loginRequest.getNombreOrCorreo())
                .switchIfEmpty(usuarioRepository.findFirstByCorreo(loginRequest.getNombreOrCorreo()))
                .flatMap(usuario -> {
                    System.out.println("Usuario encontrado: " + usuario.getNombre() + " con correo: " + usuario.getCorreo());

                    return credencialesRepository.findById(usuario.getCredencialesId())
                            .flatMap(credenciales -> {
                                if (passwordEncoder.matches(loginRequest.getPassword(), credenciales.getPassword())) {
                                    System.out.println("Contraseña coincidente para usuario: " + usuario.getNombre());

                                    // Aquí se obtiene el nombre del rol en lugar del ID
                                    return rolRepository.findById(usuario.getRolId())
                                            .map(rol -> new AuthResponseDTO(
                                                    "Login exitoso",
                                                    "FAKE_JWT_TOKEN",
                                                    rol.getNombre() // Usar el nombre del rol en lugar del ID
                                            ))
                                            .map(authResponse -> ResponseEntity.ok(authResponse));
                                } else {
                                    System.out.println("Contraseña incorrecta para usuario: " + usuario.getNombre());
                                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                            .body(new AuthResponseDTO("Credenciales incorrectas", null, null)));
                                }
                            });
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new AuthResponseDTO("Usuario no encontrado", null, null))));
    }








    public Mono<ResponseEntity<AuthResponseDTO>> register(RegisterRequestDTO registerRequest) {
        return credencialesRepository.findByNombreOrCorreo(registerRequest.getNombre(), registerRequest.getCorreo())
                .flatMap(existingUser -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponseDTO("El usuario ya existe", null, null))))
                .switchIfEmpty(
                        rolRepository.findByNombre(registerRequest.getRolNombre())
                                .flatMap(rol -> {
                                    Credenciales credenciales = new Credenciales();
                                    credenciales.setNombre(registerRequest.getNombre());
                                    credenciales.setCorreo(registerRequest.getCorreo());
                                    credenciales.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

                                    return credencialesRepository.save(credenciales)
                                            .flatMap(savedCredenciales -> {
                                                Usuario nuevoUsuario = new Usuario();
                                                nuevoUsuario.setNombre(registerRequest.getNombre());
                                                nuevoUsuario.setApellido1(registerRequest.getApellido1());
                                                nuevoUsuario.setApellido2(registerRequest.getApellido2());
                                                nuevoUsuario.setCorreo(registerRequest.getCorreo());
                                                nuevoUsuario.setTelefono(registerRequest.getTelefono());
                                                nuevoUsuario.setDireccion(registerRequest.getDireccion());
                                                nuevoUsuario.setCredencialesId(savedCredenciales.getId());
                                                nuevoUsuario.setRolId(rol.getId());

                                                return usuarioRepository.save(nuevoUsuario)
                                                        .map(savedUsuario -> ResponseEntity.ok(
                                                                new AuthResponseDTO("Usuario registrado con éxito", null, rol.getNombre())));
                                            });
                                })
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Rol no válido.")))
                );
    }
}
