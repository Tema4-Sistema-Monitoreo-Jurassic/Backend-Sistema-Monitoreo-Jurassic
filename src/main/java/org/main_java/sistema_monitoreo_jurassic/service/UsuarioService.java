package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
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
    // Creamos un pool de hilos con 10 hilos
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
    @Async
    public CompletableFuture<Flux<Usuario>> getAll() {
        return CompletableFuture.completedFuture(
                usuarioRepository.findAll()
                        .subscribeOn(Schedulers.fromExecutor(executorService))
        );
    }


    // metodo para obtener un usuario por su id
    @Async
    public CompletableFuture<Mono<Usuario>> getById(String id) {
        return CompletableFuture.completedFuture(
                usuarioRepository.findById(id)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
        );
    }


    // metodo para actualizar un usuario
    @Async
    public CompletableFuture<Mono<Usuario>> update(String id, Usuario usuarioActualizado) {
        return CompletableFuture.completedFuture(
                usuarioRepository.findById(id)
                        .flatMap(usuarioExistente -> {
                            usuarioExistente.setNombre(usuarioActualizado.getNombre());
                            usuarioExistente.setApellido1(usuarioActualizado.getApellido1());
                            usuarioExistente.setApellido2(usuarioActualizado.getApellido2());
                            usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
                            usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
                            usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
                            usuarioExistente.setRolId(usuarioActualizado.getRolId());
                            usuarioExistente.setCredencialesId(usuarioActualizado.getCredencialesId());
                            return usuarioRepository.save(usuarioExistente);
                        })
                        .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
        );
    }


    // metodo para crear un usuario
    @Async
    public CompletableFuture<Mono<Usuario>> create(Usuario usuario) {
        return CompletableFuture.completedFuture(
                Mono.fromCallable(() -> usuarioRepository.save(usuario))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .flatMap(mono -> mono)
        );
    }


    // metodo para eliminar un usuario
    @Async
    public CompletableFuture<Mono<Void>> delete(String id) {
        return CompletableFuture.completedFuture(
                Mono.fromRunnable(() -> usuarioRepository.deleteById(id))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                        .then()
        );
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