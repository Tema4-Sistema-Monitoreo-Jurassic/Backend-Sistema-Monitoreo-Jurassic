package org.main_java.sistema_monitoreo_jurassic.rest;

import org.main_java.sistema_monitoreo_jurassic.model.EventoDTO;
import org.main_java.sistema_monitoreo_jurassic.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/eventos")
public class EventoResource {

    private final EventoService eventoService;

    public EventoResource(final EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public Flux<EventoDTO> getAllEventos() {
        return eventoService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EventoDTO>> getEvento(@PathVariable final String id) {
        return eventoService.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createEvento(@RequestBody final EventoDTO eventoDTO) {
        return eventoService.create(eventoDTO)
                .map(createdId -> ResponseEntity.ok(createdId));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateEvento(@PathVariable final String id,
                                                   @RequestBody final EventoDTO eventoDTO) {
        return eventoService.update(id, eventoDTO)
                .map(updated -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEvento(@PathVariable final String id) {
        return eventoService.delete(id)
                .map(deleted -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

