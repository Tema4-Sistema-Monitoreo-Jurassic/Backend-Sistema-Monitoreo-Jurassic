package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
import org.main_java.sistema_monitoreo_jurassic.model.UsuarioDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.UsuarioRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class UsuarioService {

    // Inyectamos el repositorio de usuarios
    private final UsuarioRepository usuarioRepository;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorService;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceDelete;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceCreate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceUpdate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceGetById;

    // Inyectamos el repositorio de usuarios y creamos un pool de hilos
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.executorService = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceDelete = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceCreate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceUpdate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceGetById = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
    }


    // metodo para obtener todos los usuarios
    public CompletableFuture<Flux<Usuario>> getAll() {
        return CompletableFuture.completedFuture(
                usuarioRepository.findAll()
                        .subscribeOn(Schedulers.fromExecutor(executorService))
        );
    }


    // metodo para obtener un usuario por su id
    public CompletableFuture<Mono<Usuario>> getById(String id) {
        return CompletableFuture.completedFuture(
                usuarioRepository.findById(id)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
        );
    }


    // metodo para crear un usuario a partir de un DTO
    public Mono<UsuarioDTO> create(UsuarioDTO usuarioDTO) {
        return mapToEntity(usuarioDTO) // Convertir el DTO a la entidad
                .flatMap(usuario -> usuarioRepository.save(usuario)) // Guardar el usuario en la base de datos
                .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                .flatMap(this::mapToDTO); // Convertir la entidad guardada de nuevo a DTO
    }


    // metodo para actualizar un usuario a partir de un DTO
    public Mono<UsuarioDTO> update(String id, UsuarioDTO usuarioActualizadoDTO) {
        return usuarioRepository.findById(id)
                .flatMap(usuarioExistente -> mapToEntity(usuarioActualizadoDTO) // Convertir el DTO a entidad
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
                .flatMap(usuario -> usuarioRepository.save(usuario)) // Guardar los cambios
                .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
                .flatMap(this::mapToDTO); // Convertir la entidad guardada de nuevo a DTO
    }


    // metodo para eliminar un usuario
    public CompletableFuture<Mono<Void>> delete(String id) {
        return CompletableFuture.completedFuture(
                Mono.fromRunnable(() -> usuarioRepository.deleteById(id))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                        .then()
        );
    }


    // metodo para mapear una entidad a un DTO
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

            // Convertir los IDs de rol y credenciales
            dto.setRolId(usuario.getRolId());
            dto.setCredencialesId(usuario.getCredencialesId());

            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }


    // metodo para mapear un DTO a una entidad
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

            // Asignar los IDs de rol y credenciales
            usuario.setRolId(dto.getRolId());
            usuario.setCredencialesId(dto.getCredencialesId());

            return usuario;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}



/* Explicación de Tecnologías
@Async: Anotación de Spring que permite ejecutar métodos de manera asíncrona.
Los métodos anotados con @Async se ejecutan en un hilo separado del hilo principal.

ExecutorService: Interfaz de Java que proporciona un mecanismo para gestionar un pool de hilos.
Permite ejecutar tareas de manera asíncrona y gestionar la concurrencia.

CompletableFuture: Clase de Java que representa un resultado de una operación asíncrona.
Permite manejar tareas asíncronas de manera más sencilla.

Mono y Flux: Tipos de Project Reactor que representan flujos reactivos.
Mono representa un flujo que emite cero o un elemento, mientras que Flux representa un flujo que puede emitir múltiples elementos.

Schedulers.fromExecutor: metodo de Project Reactor que permite especificar un ExecutorService para ejecutar tareas de manera asíncrona.*/