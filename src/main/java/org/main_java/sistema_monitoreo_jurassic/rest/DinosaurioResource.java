package org.main_java.sistema_monitoreo_jurassic.rest;

import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;
import org.main_java.sistema_monitoreo_jurassic.service.DinosaurioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dinosaurios")
public class DinosaurioResource {

    private final DinosaurioService dinosaurioService;

    public DinosaurioResource(final DinosaurioService dinosaurioService) {
        this.dinosaurioService = dinosaurioService;
    }

    @GetMapping
    public Flux<DinosaurioDTO> getAllDinosaurios() {
        return dinosaurioService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DinosaurioDTO>> getDinosaurio(@PathVariable final String id) {
        return dinosaurioService.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createDinosaurio(@RequestBody final DinosaurioDTO dinosaurioDTO) {
        return dinosaurioService.create(dinosaurioDTO)
                .map(createdId -> ResponseEntity.ok(createdId));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateDinosaurio(@PathVariable final String id,
                                                       @RequestBody final DinosaurioDTO dinosaurioDTO) {
        return dinosaurioService.update(id, dinosaurioDTO)
                .map(updated -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDinosaurio(@PathVariable final String id) {
        return dinosaurioService.delete(id)
                .map(deleted -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

//Explicaion porsiacaso
/* DinosaurioResource: Esta clase es un controlador REST de Spring Boot para gestionar entidades Dinosaurio. Incluye endpoints para operaciones CRUD (crear, leer, actualizar, eliminar) y utiliza tipos reactivos Mono y Flux para soportar el manejo de solicitudes asíncronas y no bloqueantes. */