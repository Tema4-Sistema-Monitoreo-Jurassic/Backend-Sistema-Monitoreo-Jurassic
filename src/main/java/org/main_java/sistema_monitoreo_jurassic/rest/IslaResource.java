package org.main_java.sistema_monitoreo_jurassic.rest;

import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;
import org.main_java.sistema_monitoreo_jurassic.service.IslaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/islas")
public class IslaResource {

    private final IslaService islaService;

    public IslaResource(final IslaService islaService) {
        this.islaService = islaService;
    }

    @GetMapping
    public Flux<IslaDTO> getAllIslas() {
        return islaService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<IslaDTO>> getIsla(@PathVariable final String id) {
        return islaService.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createIsla(@RequestBody final IslaDTO islaDTO) {
        return islaService.create(islaDTO)
                .map(createdId -> ResponseEntity.ok(createdId));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateIsla(@PathVariable final String id,
                                                 @RequestBody final IslaDTO islaDTO) {
        return islaService.update(id, islaDTO)
                .map(updated -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteIsla(@PathVariable final String id) {
        return islaService.delete(id)
                .map(deleted -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

//Explicaion porsiacaso
/* IslaResource: Esta clase es un controlador REST de Spring Boot responsable de manejar solicitudes HTTP para entidades Isla. Ofrece endpoints para realizar operaciones CRUD y aprovecha los constructos de programación reactiva (Mono y Flux) para gestionar flujos de datos asíncronos. */