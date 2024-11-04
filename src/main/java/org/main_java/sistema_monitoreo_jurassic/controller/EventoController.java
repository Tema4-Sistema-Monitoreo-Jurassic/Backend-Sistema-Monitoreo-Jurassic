package org.main_java.sistema_monitoreo_jurassic.controller;

import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import org.main_java.sistema_monitoreo_jurassic.model.EventoDTO;
import org.main_java.sistema_monitoreo_jurassic.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public Flux<Evento> getAll() {
        return eventoService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Evento>> getById(@PathVariable String id) {
        return eventoService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<EventoDTO> create(@RequestBody EventoDTO eventoDTO) {
        return eventoService.create(eventoDTO);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Evento>> update(@PathVariable String id, @RequestBody Evento eventoActualizado) {
        return eventoService.update(id, eventoActualizado)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return eventoService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
