//Ojo que va a tocar hacer triangulo entre el servicce de los dinos este y los metodos de la clase isla en el domain para retroalientacion de metoddos por tablero
//Para entender mejor me lo leo mas detenidamente de dicha clase domain




/*
package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.repos.IslaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class IslaService {

    // Inyectamos el repositorio de islas
    private final IslaRepository islaRepository;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorService;
    // Creamos un pool de hilos con 50 hilos
    //private final ExecutorService executorServiceTipo;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceDelete;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceCreate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceUpdate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceGetById;


    // Inyectamos el repositorio de islas y creamos un pool de hilos
    public IslaService(IslaRepository islaRepository) {
        this.islaRepository = islaRepository;
        this.executorService = Executors.newFixedThreadPool(50); // Pool de hilos con 10 hilos
        //this.executorServiceTipo = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceDelete = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceCreate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceUpdate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceGetById = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
    }


    // metodo para obtener todas las islas
    @Async
    public CompletableFuture<Flux<Isla>> getAll() {
        return CompletableFuture.completedFuture(
                islaRepository.findAll()
                        .subscribeOn(Schedulers.fromExecutor(executorService))
        );
    }


    // metodo para obtener una isla por su id
    @Async
    public CompletableFuture<Mono<Isla>> getById(String id) {
        return CompletableFuture.completedFuture(
                islaRepository.findById(id)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
        );
    }


    // metodo para crear una isla
    @Async
    public CompletableFuture<Mono<Isla>> create(Isla isla) {
        return CompletableFuture.completedFuture(
                Mono.fromCallable(() -> islaRepository.save(isla))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .flatMap(mono -> mono)
        );
    }


    // metodo para actualizar una isla
    @Async
    public CompletableFuture<Mono<Isla>> update(String id, Isla islaActualizada) {
        return CompletableFuture.completedFuture(
                islaRepository.findById(id)
                        .flatMap(islaExistente -> {
                            islaExistente.setNombre(islaActualizada.getNombre());
                            islaExistente.setCapacidadMaxima(islaActualizada.getCapacidadMaxima());
                            islaExistente.setDinosaurios(islaActualizada.getDinosaurios());
                            return islaRepository.save(islaExistente);
                        })
                        .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
        );
    }


    // metodo para eliminar una isla
    @Async
    public CompletableFuture<Mono<Void>> delete(String id) {
        return CompletableFuture.completedFuture(
                Mono.fromRunnable(() -> islaRepository.deleteById(id))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                        .then()
        );
    }
}


*/
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