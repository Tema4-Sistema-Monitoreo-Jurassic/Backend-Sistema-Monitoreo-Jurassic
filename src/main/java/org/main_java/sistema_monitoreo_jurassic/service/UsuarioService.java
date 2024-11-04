package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
import org.main_java.sistema_monitoreo_jurassic.model.loginDTO.AuthResponseDTO;
import org.main_java.sistema_monitoreo_jurassic.model.loginDTO.LoginRequestDTO;
import org.main_java.sistema_monitoreo_jurassic.model.UsuarioDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.CredencialesRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.RolRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.UsuarioRepository;
import org.main_java.sistema_monitoreo_jurassic.domain.Rol;
import org.main_java.sistema_monitoreo_jurassic.domain.Credenciales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private CredencialesRepository credencialesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Creamos pools de hilos para tareas específicas
    private final ExecutorService executorService;
    private final ExecutorService executorServiceDelete;
    private final ExecutorService executorServiceCreate;
    private final ExecutorService executorServiceUpdate;
    private final ExecutorService executorServiceGetById;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.executorService = Executors.newFixedThreadPool(50);
        this.executorServiceDelete = Executors.newFixedThreadPool(50);
        this.executorServiceCreate = Executors.newFixedThreadPool(50);
        this.executorServiceUpdate = Executors.newFixedThreadPool(50);
        this.executorServiceGetById = Executors.newFixedThreadPool(50);
    }

    // Metodo para obtener todos los usuarios
    public Flux<UsuarioDTO> getAll() {
        return usuarioRepository.findAll()
                .subscribeOn(Schedulers.fromExecutor(executorService))
                .flatMap(this::mapToDTO);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private Mono<Credenciales> crearCredenciales(UsuarioDTO usuarioDTO) {
        Credenciales credenciales = new Credenciales();
        credenciales.setUsername(usuarioDTO.getCorreo());
        credenciales.setPassword(passwordEncoder.encode(usuarioDTO.getContrasena()));
        return credencialesRepository.save(credenciales);
    }


    private Mono<Rol> validarYCrearRol(String rolNombre) {
        return rolRepository.findByNombre(rolNombre)
                .switchIfEmpty(Mono.defer(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre(rolNombre);
                    return rolRepository.save(nuevoRol);}));}

    // Metodo para obtener un usuario por su id
    public Mono<UsuarioDTO> getById(String id) {
        return usuarioRepository.findById(id)
                .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
                .flatMap(this::mapToDTO);
    }

    // Metodo para crear un usuario a partir de un DTO
    public Mono<UsuarioDTO> create(UsuarioDTO usuarioDTO) {
        return mapToEntity(usuarioDTO)
                .flatMap(usuario -> {
                    usuario.setDateCreated(OffsetDateTime.now()); // Aseguramos que la fecha de creación se asigne
                    return usuarioRepository.save(usuario);
                })
                .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                .flatMap(this::mapToDTO);
    }

    // Metodo para actualizar un usuario a partir de un DTO
    public Mono<UsuarioDTO> update(String id, UsuarioDTO usuarioActualizadoDTO) {
        return usuarioRepository.findById(id)
                .flatMap(usuarioExistente -> mapToEntity(usuarioActualizadoDTO)
                        .map(usuarioActualizado -> {
                            // Actualizar los campos necesarios
                            usuarioExistente.setNombre(usuarioActualizado.getNombre());
                            usuarioExistente.setApellido1(usuarioActualizado.getApellido1());
                            usuarioExistente.setApellido2(usuarioActualizado.getApellido2());
                            usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
                            usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
                            usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
                            usuarioExistente.setRolId(usuarioActualizado.getRolId());
                            usuarioExistente.setCredencialesId(usuarioActualizado.getCredencialesId());
                            return usuarioExistente;
                        })
                )
                .flatMap(usuario -> usuarioRepository.save(usuario))
                .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
                .flatMap(this::mapToDTO);
    }

    // Metodo para eliminar un usuario
    public Mono<Void> delete(String id) {
        return usuarioRepository.deleteById(id)
                .subscribeOn(Schedulers.fromExecutor(executorServiceDelete));
    }

    // Metodo para mapear una entidad a un DTO
    public Mono<UsuarioDTO> mapToDTO(Usuario usuario) {
        return Mono.fromCallable(() -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setApellido1(usuario.getApellido1());
            dto.setApellido2(usuario.getApellido2());
            dto.setCorreo(usuario.getCorreo());
            dto.setTelefono(usuario.getTelefono());
            dto.setDireccion(usuario.getDireccion());
            dto.setRolId(usuario.getRolId());
            dto.setCredencialesId(usuario.getCredencialesId());
            dto.setDateCreated(usuario.getDateCreated());
            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Metodo para mapear un DTO a una entidad
    public Mono<Usuario> mapToEntity(UsuarioDTO dto) {
        return Mono.fromCallable(() -> {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getId());
            usuario.setNombre(dto.getNombre());
            usuario.setApellido1(dto.getApellido1());
            usuario.setApellido2(dto.getApellido2());
            usuario.setCorreo(dto.getCorreo());
            usuario.setTelefono(dto.getTelefono());
            usuario.setDireccion(dto.getDireccion());
            usuario.setRolId(dto.getRolId());
            usuario.setCredencialesId(dto.getCredencialesId());
            usuario.setDateCreated(dto.getDateCreated());
            return usuario;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Nueva funcionalidad: login
    public Mono<AuthResponseDTO> login(LoginRequestDTO loginRequest) {
        return usuarioRepository.findByCorreo(loginRequest.getCorreo())
                .flatMap(usuario -> {
                    if (passwordEncoder.matches(loginRequest.getContrasena(), usuario.getCredencialesId())) {
                        String rol = usuario.getRolId();
                        String userId = usuario.getId();

                        // Crear el objeto AuthResponseDTO
                        AuthResponseDTO authResponse = new AuthResponseDTO(
                                "Login exitoso",
                                "FAKE_JWT_TOKEN", // Sustituir por un token real en producción
                                rol,
                                userId,
                                mapToDTO(usuario).block() // Asignamos un UsuarioDTO
                        );
                        return Mono.just(authResponse);
                    } else {
                        return Mono.just(new AuthResponseDTO("Credenciales incorrectas", null, null, null, null));
                    }
                })
                .switchIfEmpty(Mono.just(new AuthResponseDTO("Usuario no encontrado", null, null, null, null)));
    }

    // Nueva funcionalidad: register
    public Mono<AuthResponseDTO> register(UsuarioDTO registerRequest, String rolNombre) {
        return usuarioRepository.findByCorreo(registerRequest.getCorreo())
                .flatMap(existingUser -> Mono.just(new AuthResponseDTO("El usuario ya existe", null, null, null, null)))
                .switchIfEmpty(
                        validarYCrearRol(rolNombre)
                                .flatMap(rol -> crearCredenciales(registerRequest)
                                        .flatMap(credenciales -> {
                                            Usuario nuevoUsuario = new Usuario();
                                            nuevoUsuario.setNombre(registerRequest.getNombre());
                                            nuevoUsuario.setApellido1(registerRequest.getApellido1());
                                            nuevoUsuario.setApellido2(registerRequest.getApellido2());
                                            nuevoUsuario.setCorreo(registerRequest.getCorreo());
                                            nuevoUsuario.setTelefono(registerRequest.getTelefono());
                                            nuevoUsuario.setDireccion(registerRequest.getDireccion());
                                            nuevoUsuario.setRolId(rol.getId()); // Asignamos el ID del rol
                                            nuevoUsuario.setCredencialesId(credenciales.getId()); // Asignamos el ID de las credenciales
                                            nuevoUsuario.setDateCreated(OffsetDateTime.now());
                                            return usuarioRepository.save(nuevoUsuario);
                                        })
                                        .flatMap(usuarioGuardado -> Mono.just(new AuthResponseDTO(
                                                        "Usuario registrado con éxito",
                                                        null, // No se genera un token en el registro
                                                        rolNombre,
                                                        usuarioGuardado.getId(),
                                                        mapToDTO(usuarioGuardado).block() // Asignamos un UsuarioDTO
                                                ))
                                        )
                                ));
    }}


