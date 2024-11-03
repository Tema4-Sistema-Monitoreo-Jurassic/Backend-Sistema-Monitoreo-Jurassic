package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.messaging.RabbitMQProducer;
import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import org.main_java.sistema_monitoreo_jurassic.model.EventoDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.EventoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class EventoService {

    // Inyectamos el repositorio de eventos y el productor de RabbitMQ
    private final EventoRepository eventoRepository;
    // Inyectamos el productor de RabbitMQ
    private final RabbitMQProducer rabbitMQProducer;

    // Creamos un pool de hilos con 50 hilos para cada tipo de operación
    private final ExecutorService executorService;
    //private final ExecutorService executorServiceTipo;
    private final ExecutorService executorServiceDelete;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceCreate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceUpdate;
    // Creamos un pool de hilos con 50 hilos
    private final ExecutorService executorServiceGetById;


    // Constructor con inyección de dependencias
    public EventoService(EventoRepository eventoRepository, RabbitMQProducer rabbitMQProducer) {
        this.eventoRepository = eventoRepository;
        this.rabbitMQProducer = rabbitMQProducer;
        this.executorService = Executors.newFixedThreadPool(50);
        this.executorServiceDelete = Executors.newFixedThreadPool(50);
        this.executorServiceCreate = Executors.newFixedThreadPool(50);
        this.executorServiceUpdate = Executors.newFixedThreadPool(50);
        this.executorServiceGetById = Executors.newFixedThreadPool(50);
    }


    // Obtener todos los eventos
    public CompletableFuture<Flux<Evento>> getAll() {
        return CompletableFuture.completedFuture(
                eventoRepository.findAll()
                        .subscribeOn(Schedulers.fromExecutor(executorService))
        );
    }


    // Obtener un evento por su ID
    public CompletableFuture<Mono<Evento>> getById(String id) {
        return CompletableFuture.completedFuture(
                eventoRepository.findById(id)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceGetById))
        );
    }


    public CompletableFuture<EventoDTO> create(EventoDTO dto) {
        return mapToEntity(dto)
                .flatMap(evento -> eventoRepository.save(evento)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .doOnSuccess(savedEvento -> rabbitMQProducer.enviarMensaje("eventos", "Nuevo evento registrado: " + savedEvento.getMensaje()))
                        .flatMap(this::mapToDTO)
                )
                .toFuture(); // Convertimos el Mono<EventoDTO> a CompletableFuture<EventoDTO>
    }


    // Actualizar un evento
    public CompletableFuture<Mono<Evento>> update(String id, Evento eventoActualizado) {
        return CompletableFuture.completedFuture(
                eventoRepository.findById(id)
                        .flatMap(eventoExistente -> {
                            eventoExistente.setMensaje(eventoActualizado.getMensaje());
                            eventoExistente.setValor(eventoActualizado.getValor());
                            eventoExistente.setDateCreated(eventoActualizado.getDateCreated());
                            return eventoRepository.save(eventoExistente);
                        })
                        .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
        );
    }


    // Eliminar un evento
    public CompletableFuture<Mono<Void>> delete(String id) {
        return CompletableFuture.completedFuture(
                Mono.fromRunnable(() -> eventoRepository.deleteById(id))
                        .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                        .then()
        );
    }


    // Enviar alerta crítica mediante RabbitMQ
    public CompletableFuture<Void> enviarAlerta(Evento evento) {
        return CompletableFuture.runAsync(() -> {
            String mensajeAlerta = "Alerta crítica: " + evento.getMensaje() + " - Valor: " + evento.getValor();
            rabbitMQProducer.enviarMensaje("alertas", mensajeAlerta);
            System.out.println("Alerta enviada a la cola de RabbitMQ: " + mensajeAlerta);
        }, executorService);
    }


    // Mapear de DTO a entidad Evento
    public Mono<Evento> mapToEntity(EventoDTO dto) {
        return Mono.fromCallable(() -> {
            Evento evento = new Evento();
            evento.setMensaje(dto.getMensaje());
            evento.setValor(dto.getValor());
            evento.setDateCreated(dto.getDateCreated());
            return evento;
        }).subscribeOn(Schedulers.boundedElastic());
    }


    // Mapear de entidad Evento a DTO
    public Mono<EventoDTO> mapToDTO(Evento evento) {
        return Mono.fromCallable(() -> {
            EventoDTO dto = new EventoDTO();
            dto.setMensaje(evento.getMensaje());
            dto.setValor(evento.getValor());
            dto.setDateCreated(evento.getDateCreated());
            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}