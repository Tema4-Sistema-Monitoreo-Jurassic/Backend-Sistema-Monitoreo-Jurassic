package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroVoladorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorFrecuenciaCardiacaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorMovimientoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorTemperaturaDTO;
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


    // metodo para actualizar un sensor a partir de un DTO
    public Mono<SensorDTO> update(String id, SensorDTO sensorActualizadoDTO) {
        return sensorRepository.findById(id)
                .flatMap(sensorExistente -> mapToEntity(sensorActualizadoDTO) // Convertir el DTO en la entidad
                        .map(sensorActualizado -> {
                            // Actualizar los campos necesarios
                            sensorExistente.setTipo(sensorActualizado.getTipo());
                            sensorExistente.setLimiteInferior(sensorActualizado.getLimiteInferior());
                            sensorExistente.setLimiteSuperior(sensorActualizado.getLimiteSuperior());
                            return sensorExistente;
                        })
                )
                .flatMap(sensor -> sensorRepository.save(sensor)) // Guardar los cambios en la base de datos
                .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
                .flatMap(this::mapToDTO); // Convertir la entidad guardada de nuevo en DTO
    }


    // metodo para eliminar un sensor
    public CompletableFuture<Mono<Void>> delete(String id) {
        return CompletableFuture.completedFuture(
                Mono.fromRunnable(() -> sensorRepository.deleteById(id))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                        .then()
        );
    }


    // metodo para mapear una entidad a un DTO
    public Mono<SensorDTO> mapToDTO(Sensor sensor) {
        return Mono.fromCallable(() -> {
            SensorDTO dto;
            if (sensor instanceof SensorFrecuenciaCardiaca) {
                dto = new SensorFrecuenciaCardiacaDTO();
            } else if (sensor instanceof SensorTemperatura) {
                dto = new SensorTemperaturaDTO();
            } else if (sensor instanceof SensorMovimiento) {
                dto = new SensorMovimientoDTO();
            } else {
                throw new IllegalArgumentException("Unknown dinosaur type");
            }
            dto.setId(sensor.getId());
            dto.setTipo(sensor.getTipo());
            dto.setLimiteInferior(sensor.getLimiteInferior());
            dto.setLimiteSuperior(sensor.getLimiteSuperior());
            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }


    // metodo para mapear un DTO a una entidad
    public Mono<Sensor> mapToEntity(SensorDTO dto) {
        return Mono.fromCallable(() -> {
            Sensor sensor = sensorFactory.crearSensor(dto.getId(), dto.getTipo(), dto.getLimiteInferior(), dto.getLimiteSuperior());
            sensor.setId(dto.getId());
            sensor.setTipo(dto.getTipo());
            sensor.setLimiteInferior(dto.getLimiteInferior());
            sensor.setLimiteSuperior(dto.getLimiteSuperior());
            return sensor;
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