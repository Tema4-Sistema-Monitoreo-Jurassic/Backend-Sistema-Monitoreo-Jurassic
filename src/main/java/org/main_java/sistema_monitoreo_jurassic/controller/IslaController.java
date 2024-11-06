// IslaController.java
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
@RequestMapping("/islas")
public class IslaController {

    @Autowired
    private IslaService islaService;

    // Obtener todas las islas
    @GetMapping
    public Flux<IslaDTO> getAll() {
        return islaService.getAll()
                .flatMap(islaService::mapToDTO);
    }

    // Obtener una isla por su ID
    @GetMapping("/{id}")
    public Mono<ResponseEntity<IslaDTO>> getById(@PathVariable String id) {
        return islaService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Crear una nueva isla
    @PostMapping
    public Mono<ResponseEntity<IslaDTO>> create(@RequestBody IslaDTO islaDTO) {
        return islaService.create(islaDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    // Actualizar una isla existente
    @PutMapping("/{id}")
    public Mono<ResponseEntity<IslaDTO>> update(@PathVariable String id, @RequestBody IslaDTO islaActualizada) {
        return islaService.update(id, islaActualizada)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Eliminar una isla por su ID
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return islaService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Obtener el tablero de una isla por su ID
    @GetMapping("/{id}/tablero")
    public Mono<ResponseEntity<int[][]>> getTableroByIslaId(@PathVariable String id) {
        return islaService.getById(id)
                .map(IslaDTO::getTablero)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
