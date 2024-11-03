package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;
import org.main_java.sistema_monitoreo_jurassic.repos.SensorRepository;
import org.main_java.sistema_monitoreo_jurassic.service.factory.SensorFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class SensorService {

    // Inyectamos el repositorio de sensores
    private final SensorRepository sensorRepository;
    // Inyectamos el factory de sensores
    private final SensorFactory sensorFactory;
    // Creamos un pool de hilos con 10 hilos
    private final ExecutorService executorService;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceTipo;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceDelete;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceCreate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceUpdate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceGetById;

    // Inyectamos el repositorio de sensores y el factory de sensores y creamos un pool de hilos
    public SensorService(SensorRepository sensorRepository, SensorFactory sensorFactory) {
        this.sensorRepository = sensorRepository;
        this.sensorFactory = sensorFactory;
        this.executorService = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceTipo = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceDelete = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceCreate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceUpdate = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
        this.executorServiceGetById = Executors.newFixedThreadPool(50); // Pool de hilos con 50 hilos
    }


    // metodo para obtener todos los sensores
    public CompletableFuture<Flux<Sensor>> getAll() {
        return CompletableFuture.completedFuture(
                sensorRepository.findAll()
                        .subscribeOn(Schedulers.fromExecutor(executorService))
        );
    }

    // metodo general para obtener subtipos de sensores usando filtrado concurrente
    private <T extends Sensor> CompletableFuture<Flux<T>> obtenerSensoresPorTipo(Class<T> tipoClase) {
        return getAll().thenApply(flux ->
                flux.filter(sensor -> tipoClase.isInstance(sensor))
                        .cast(tipoClase)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceTipo))
        );
    }

    // metodo para obtener sensores de temperatura
    public CompletableFuture<Flux<SensorTemperatura>> obtenerSensoresDeTemperatura() {
        return obtenerSensoresPorTipo(SensorTemperatura.class);
    }

    // metodo para obtener sensores de movimiento
    public CompletableFuture<Flux<SensorMovimiento>> obtenerSensoresDeMovimiento() {
        return obtenerSensoresPorTipo(SensorMovimiento.class);
    }

    // metodo para obtener sensores de frecuencia cardiaca
    public CompletableFuture<Flux<SensorFrecuenciaCardiaca>> obtenerSensoresDeFrecuenciaCardiaca() {
        return obtenerSensoresPorTipo(SensorFrecuenciaCardiaca.class);
    }


    // metodo para obtener un sensor por su id
    public CompletableFuture<Mono<Sensor>> getById(String id) {
        return CompletableFuture.completedFuture(
                sensorRepository.findById(id)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
        );
    }


    // metodo para registrar un sensor
    public CompletableFuture<Mono<Sensor>> create(String id, String tipo, double limiteInferior, double limiteSuperior) {
        Sensor sensor = sensorFactory.crearSensor(id, tipo, limiteInferior, limiteSuperior);
        return CompletableFuture.completedFuture(
                Mono.fromCallable(() -> sensorRepository.save(sensor))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .flatMap(mono -> mono)
        );
    }


    // metodo para actualizar un sensor
    public CompletableFuture<Mono<Sensor>> update(String id, Sensor sensorActualizado) {
        return CompletableFuture.completedFuture(
                sensorRepository.findById(id)
                        .flatMap(sensorExistente -> {
                            sensorExistente.setTipo(sensorActualizado.getTipo());
                            sensorExistente.setLimiteInferior(sensorActualizado.getLimiteInferior());
                            sensorExistente.setLimiteSuperior(sensorActualizado.getLimiteSuperior());
                            return sensorRepository.save(sensorExistente);
                        })
                        .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
        );
    }


    // metodo para eliminar un sensor
    public CompletableFuture<Mono<Void>> delete(String id) {
        return CompletableFuture.completedFuture(
                Mono.fromRunnable(() -> sensorRepository.deleteById(id))
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