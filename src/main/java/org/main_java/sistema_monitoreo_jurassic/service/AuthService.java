package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.Credenciales;
import org.main_java.sistema_monitoreo_jurassic.domain.Rol;
import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
import org.main_java.sistema_monitoreo_jurassic.model.AuthResponseDTO;
import org.main_java.sistema_monitoreo_jurassic.model.LoginRequestDTO;
import org.main_java.sistema_monitoreo_jurassic.model.RegisterRequestDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.CredencialesRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.RolRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final CredencialesRepository credencialesRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UsuarioRepository usuarioRepository,
                       CredencialesRepository credencialesRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.credencialesRepository = credencialesRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // Login method
    public Mono<AuthResponseDTO> login(LoginRequestDTO request) {
        return usuarioRepository.findByCorreo(request.getCorreo())
                .flatMap(usuario -> credencialesRepository.findById(usuario.getCredencialesId())
                        .filter(credenciales -> passwordEncoder.matches(request.getPassword(), credenciales.getPassword()))
                        .flatMap(validCredenciales -> Mono.just(new AuthResponseDTO("Login successful", "mock-token", usuario.getRolId())))
                )
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }


    // Register method
    public Mono<AuthResponseDTO> register(RegisterRequestDTO request) {
        return usuarioRepository.findByCorreo(request.getCorreo())
                .flatMap(existingUser -> Mono.error(new RuntimeException("User already exists with email: " + request.getCorreo())))
                .switchIfEmpty(
                        rolRepository.findById(request.getRolId())
                                .defaultIfEmpty(new Rol("default-role-id", "USER_ROLE", Set.of()))
                                .flatMap(rol -> {
                                    // Create and save credentials
                                    Credenciales credenciales = new Credenciales();
                                    credenciales.setUsername(request.getCorreo());
                                    credenciales.setPassword(passwordEncoder.encode(request.getPassword()));
                                    return credencialesRepository.save(credenciales)
                                            .flatMap(savedCredenciales -> {
                                                // Create and save the user
                                                Usuario usuario = new Usuario();
                                                usuario.setNombre(request.getNombre());
                                                usuario.setApellido1(request.getApellido1());
                                                usuario.setApellido2(request.getApellido2());
                                                usuario.setCorreo(request.getCorreo());
                                                usuario.setTelefono(request.getTelefono());
                                                usuario.setDireccion(request.getDireccion());
                                                usuario.setRolId(rol.getId());
                                                usuario.setCredencialesId(savedCredenciales.getId());
                                                return usuarioRepository.save(usuario)
                                                        .map(savedUsuario ->
                                                                new AuthResponseDTO("User registered successfully", "mock-token", savedUsuario.getRolId())
                                                        );
                                            });
                                })
                );
    }

}
