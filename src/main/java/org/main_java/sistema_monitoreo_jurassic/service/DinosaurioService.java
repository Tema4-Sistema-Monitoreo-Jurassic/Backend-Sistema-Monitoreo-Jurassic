package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.messaging.RabbitMQProducer;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.PosicionDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro.CarnivoroVoladorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro.HerbivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro.HerbivoroTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro.HerbivoroVoladorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroAcuaticoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroTerrestreDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro.OmnivoroVoladorDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.DinosaurioRepository;
import org.main_java.sistema_monitoreo_jurassic.service.factory.DinosaurioFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class DinosaurioService {

    // Inyectamos el productor de RabbitMQ
    private final RabbitMQProducer rabbitMQProducer;
    // Inyectamos el repositorio de dinosaurios
    private final DinosaurioRepository dinosaurioRepository;
    // Inyectamos el factory de dinosaurios
    private final DinosaurioFactory dinosaurioFactory;
    // Creamos un pool de hilos con 50 hilos
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


    // Inyectamos el repositorio de dinosaurios, el factory de dinosaurios y el productor de RabbitMQ
    @Autowired
    public DinosaurioService(DinosaurioRepository dinosaurioRepository,
                             DinosaurioFactory dinosaurioFactory,
                             RabbitMQProducer rabbitMQProducer) {
        this.dinosaurioRepository = dinosaurioRepository;
        this.dinosaurioFactory = dinosaurioFactory;
        this.rabbitMQProducer = rabbitMQProducer;
        this.executorService = Executors.newFixedThreadPool(50);
        this.executorServiceTipo = Executors.newFixedThreadPool(50);
        this.executorServiceDelete = Executors.newFixedThreadPool(50);
        this.executorServiceCreate = Executors.newFixedThreadPool(50);
        this.executorServiceUpdate = Executors.newFixedThreadPool(50);
        this.executorServiceGetById = Executors.newFixedThreadPool(50);
    }


    // metodo para obtener todos los dinosaurios
    public CompletableFuture<Flux<Dinosaurio>> getAll() {
        return CompletableFuture.completedFuture(
                dinosaurioRepository.findAll()
                        .subscribeOn(Schedulers.fromExecutor(executorService))
        );
    }


    // metodo general para obtener subtipos de dinosaurios usando filtrado concurrente
    private <T extends Dinosaurio> CompletableFuture<Flux<T>> obtenerDinosauriosPorTipo(Class<T> tipoClase) {
        return getAll().thenApply(flux ->
                flux.filter(dino -> tipoClase.isInstance(dino))
                        .cast(tipoClase)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceTipo))
        );
    }


    // Funciones específicas para obtener cada subtipo de dinosaurio

    public CompletableFuture<Flux<CarnivoroTerrestre>> obtenerCarnivorosTerrestres() {
        return obtenerDinosauriosPorTipo(CarnivoroTerrestre.class);
    }

    // metodo para obtener todos los dinosaurios carnivoros voladores
    public CompletableFuture<Flux<CarnivoroVolador>> obtenerCarnivorosVoladores() {
        return obtenerDinosauriosPorTipo(CarnivoroVolador.class);
    }

    // metodo para obtener todos los dinosaurios carnivoros acuaticos
    public CompletableFuture<Flux<CarnivoroAcuatico>> obtenerCarnivorosAcuaticos() {
        return obtenerDinosauriosPorTipo(CarnivoroAcuatico.class);
    }

    // metodo para obtener todos los dinosaurios herbivoros terrestres
    public CompletableFuture<Flux<HerbivoroTerrestre>> obtenerHerbivorosTerrestres() {
        return obtenerDinosauriosPorTipo(HerbivoroTerrestre.class);
    }

    // metodo para obtener todos los dinosaurios herbivoros voladores
    public CompletableFuture<Flux<HerbivoroVolador>> obtenerHerbivorosVoladores() {
        return obtenerDinosauriosPorTipo(HerbivoroVolador.class);
    }

    // metodo para obtener todos los dinosaurios herbivoros acuaticos
    public CompletableFuture<Flux<HerbivoroAcuatico>> obtenerHerbivorosAcuaticos() {
        return obtenerDinosauriosPorTipo(HerbivoroAcuatico.class);
    }

    // metodo para obtener todos los dinosaurios omnivoros terrestres
    public CompletableFuture<Flux<OmnivoroTerrestre>> obtenerOmnivorosTerrestres() {
        return obtenerDinosauriosPorTipo(OmnivoroTerrestre.class);
    }

    // metodo para obtener todos los dinosaurios omnivoros voladores
    public CompletableFuture<Flux<OmnivoroVolador>> obtenerOmnivorosVoladores() {
        return obtenerDinosauriosPorTipo(OmnivoroVolador.class);
    }

    // metodo para obtener todos los dinosaurios omnivoros acuaticos
    public CompletableFuture<Flux<OmnivoroAcuatico>> obtenerOmnivorosAcuaticos() {
        return obtenerDinosauriosPorTipo(OmnivoroAcuatico.class);
    }



    // metodo para obtener un dinosaurio por su ID
    public CompletableFuture<Mono<DinosaurioDTO>> getById(String id) {
        return CompletableFuture.completedFuture(
                dinosaurioRepository.findById(id)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
                        .flatMap(this::mapToDTO)
                        .doOnSuccess(dto -> rabbitMQProducer.enviarMensaje("dinosaurios", "Consultado dinosaurio con ID: " + dto.getId()))
        );
    }


    // metodo para crear un dinosaurio
    public CompletableFuture<DinosaurioDTO> create(DinosaurioDTO dto) {
        return mapToEntity(dto)
                .flatMap(dino -> dinosaurioRepository.save(dino)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .doOnSuccess(savedDino -> rabbitMQProducer.enviarMensaje("dinosaurios", "Nuevo dinosaurio creado: " + savedDino.getNombre()))
                        .flatMap(this::mapToDTO)
                )
                .toFuture();
    }


    // metodo para actualizar un dinosaurio
    public CompletableFuture<Mono<DinosaurioDTO>> update(String id, DinosaurioDTO dto) {
        return CompletableFuture.completedFuture(
                dinosaurioRepository.findById(id)
                        .flatMap(existingDino -> mapToEntity(dto)
                                .map(updatedDino -> {
                                    existingDino.setNombre(updatedDino.getNombre());
                                    existingDino.setEdad(updatedDino.getEdad());
                                    existingDino.setHabitat(updatedDino.getHabitat());
                                    existingDino.setSensores(updatedDino.getSensores());
                                    existingDino.setPosicion(updatedDino.getPosicion());
                                    return existingDino;
                                })
                        )
                        .flatMap(dinosaurioRepository::save)
                        .flatMap(this::mapToDTO)
                        .doOnSuccess(updatedDto -> rabbitMQProducer.enviarMensaje("dinosaurios", "Dinosaurio actualizado: " + updatedDto.getNombre()))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
        );
    }


     // metoddo para enviar una alerta crítica sobre un dinosaurio.
     // Envía un mensaje de alerta a través de RabbitMQ usando la cola específica.*
    public Mono<Void> enviarAlerta(String mensaje) {// Mensaje detallado de alerta para envío a RabbitMQ
        String alerta = "ALERTA CRÍTICA: " + mensaje;

        // Enviar el mensaje a través de RabbitMQ y manejar presión de flujo
        return rabbitMQProducer.aplicarBackpressureOnBuffer("alertas", alerta)
                .doOnSubscribe(subscription -> System.out.println("Enviando alerta a RabbitMQ: " + alerta))
                .doOnError(error -> System.err.println("Error al enviar alerta: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Alerta enviada correctamente"))
                .then();
    }


    // metodo para eliminar un dinosaurio
    public CompletableFuture<Mono<Void>> delete(String id) {
        return CompletableFuture.completedFuture(
                dinosaurioRepository.findById(id)
                        .flatMap(dino -> dinosaurioRepository.deleteById(id)
                                .doOnSuccess(v -> rabbitMQProducer.enviarMensaje("dinosaurios", "Dinosaurio eliminado con ID: " + id))
                        )
                        .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
        );
    }


    // metodo para mapear de entidad a DTO
    public Mono<DinosaurioDTO> mapToDTO(Dinosaurio dinosaurio) {
        return Mono.fromCallable(() -> {
            DinosaurioDTO dto;
            if (dinosaurio instanceof CarnivoroTerrestre) {
                dto = new CarnivoroTerrestreDTO();
            } else if (dinosaurio instanceof CarnivoroAcuatico) {
                dto = new CarnivoroAcuaticoDTO();
            } else if (dinosaurio instanceof CarnivoroVolador) {
                dto = new CarnivoroVoladorDTO();
            } else if (dinosaurio instanceof HerbivoroTerrestre) {
                dto = new HerbivoroTerrestreDTO();
            } else if (dinosaurio instanceof HerbivoroAcuatico) {
                dto = new HerbivoroAcuaticoDTO();
            } else if (dinosaurio instanceof HerbivoroVolador) {
                dto = new HerbivoroVoladorDTO();
            } else if (dinosaurio instanceof OmnivoroTerrestre) {
                dto = new OmnivoroTerrestreDTO();
            } else if (dinosaurio instanceof OmnivoroAcuatico) {
                dto = new OmnivoroAcuaticoDTO();
            } else if (dinosaurio instanceof OmnivoroVolador) {
                dto = new OmnivoroVoladorDTO();
            } else {
                throw new IllegalArgumentException("Unknown dinosaur type");
            }
            dto.setId(dinosaurio.getId());
            dto.setNombre(dinosaurio.getNombre());
            dto.setEdad(dinosaurio.getEdad());
            dto.setHabitat(dinosaurio.getHabitat());
            dto.setPosicion(dinosaurio.getPosicion() != null ? new PosicionDTO(dinosaurio.getPosicion().getX(), dinosaurio.getPosicion().getY(), dinosaurio.getPosicion().getZona()) : null);
            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // metodo para mapear de DTO a entidad
    public Mono<Dinosaurio> mapToEntity(DinosaurioDTO dto) {
        return Mono.fromCallable(() -> {
            Dinosaurio dinosaurio = dinosaurioFactory.crearDinosaurio(dto.getNombre(), dto.getHabitat());
            dinosaurio.setId(dto.getId());
            dinosaurio.setEdad(dto.getEdad());
            dinosaurio.setPosicion(dto.getPosicion() != null ? new Posicion(dto.getPosicion().getX(), dto.getPosicion().getY(), dto.getPosicion().getZona()) : null);
            return dinosaurio;
        }).subscribeOn(Schedulers.boundedElastic());
    }

}

//para mapear de model a domnio y de dominio a model necesitamos mapToDTO y

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