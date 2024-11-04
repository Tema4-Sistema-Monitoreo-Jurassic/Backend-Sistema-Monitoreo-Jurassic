package org.main_java.sistema_monitoreo_jurassic.controller;

import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;
import org.main_java.sistema_monitoreo_jurassic.service.IslaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/islas")
public class IslaController {

    @Autowired
    private IslaService islaService;

    @GetMapping
    public Flux<Isla> getAll() {
        return islaService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<IslaDTO>> getById(@PathVariable String id) {
        return islaService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<IslaDTO> create(@RequestBody IslaDTO islaDTO) {
        return islaService.create(islaDTO);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<IslaDTO>> update(@PathVariable String id, @RequestBody IslaDTO islaActualizada) {
        return islaService.update(id, islaActualizada)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return islaService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
