package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.messaging.RabbitMQProducer;
import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import org.main_java.sistema_monitoreo_jurassic.model.EventoDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.EventoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceDelete = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceCreate = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceUpdate = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceGetById = Executors.newFixedThreadPool(50);

    public EventoService(EventoRepository eventoRepository, RabbitMQProducer rabbitMQProducer) {
        this.eventoRepository = eventoRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    // Obtener todos los eventos
    public Flux<Evento> getAll() {
        return eventoRepository.findAll()
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    // Obtener un evento por su ID
    public Mono<Evento> getById(String id) {
        return eventoRepository.findById(id)
                .subscribeOn(Schedulers.fromExecutor(executorServiceGetById));
    }

    // Crear un nuevo evento
    public Mono<EventoDTO> create(EventoDTO dto) {
        return mapToEntity(dto)
                .flatMap(evento -> eventoRepository.save(evento)
                        .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                        .doOnSuccess(savedEvento -> rabbitMQProducer.enviarMensaje("eventos", "Nuevo evento registrado: " + savedEvento.getMensaje()))
                        .flatMap(this::mapToDTO)
                );
    }

    // Actualizar un evento
    public Mono<Evento> update(String id, Evento eventoActualizado) {
        return eventoRepository.findById(id)
                .flatMap(eventoExistente -> {
                    eventoExistente.setMensaje(eventoActualizado.getMensaje());
                    eventoExistente.setValor(eventoActualizado.getValor());
                    eventoExistente.setDateCreated(eventoActualizado.getDateCreated());
                    return eventoRepository.save(eventoExistente);
                })
                .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate));
    }

    // Eliminar un evento
    public Mono<Void> delete(String id) {
        return Mono.fromRunnable(() -> eventoRepository.deleteById(id))
                .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                .then();
    }

    // Enviar alerta crítica mediante RabbitMQ de forma reactiva
    public Mono<Void> enviarAlerta(Evento evento) {
        String mensajeAlerta = "Alerta crítica: " + evento.getMensaje() + " - Valor: " + evento.getValor();

        return Mono.fromRunnable(() -> {
                    rabbitMQProducer.enviarMensaje("alertas", mensajeAlerta);
                    System.out.println("Alerta enviada a la cola de RabbitMQ: " + mensajeAlerta);
                })
                .subscribeOn(Schedulers.fromExecutor(executorService))
                .then();
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