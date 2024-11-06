package org.main_java.sistema_monitoreo_jurassic.controller;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.*;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.carnivoro.CarnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.herbivoro.HerbivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroAcuatico;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroTerrestre;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.omnivoro.OmnivoroVolador;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;
import org.main_java.sistema_monitoreo_jurassic.service.DinosaurioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dinosaurios")
public class DinosaurioController {

    @Autowired
    private DinosaurioService dinosaurioService;

    @GetMapping
    public Flux<Dinosaurio> getAll() {
        return dinosaurioService.getAll();
    }

    @GetMapping("/carnivoros/terrestres")
    public Flux<CarnivoroTerrestre> getCarnivorosTerrestres() {
        return dinosaurioService.obtenerCarnivorosTerrestres();
    }

    @GetMapping("/carnivoros/voladores")
    public Flux<CarnivoroVolador> getCarnivorosVoladores() {
        return dinosaurioService.obtenerCarnivorosVoladores();
    }

    @GetMapping("/carnivoros/acuaticos")
    public Flux<CarnivoroAcuatico> getCarnivorosAcuaticos() {
        return dinosaurioService.obtenerCarnivorosAcuaticos();
    }

    @GetMapping("/herbivoros/terrestres")
    public Flux<HerbivoroTerrestre> getHerbivorosTerrestres() {
        return dinosaurioService.obtenerHerbivorosTerrestres();
    }

    @GetMapping("/herbivoros/voladores")
    public Flux<HerbivoroVolador> getHerbivorosVoladores() {
        return dinosaurioService.obtenerHerbivorosVoladores();
    }

    @GetMapping("/herbivoros/acuaticos")
    public Flux<HerbivoroAcuatico> getHerbivorosAcuaticos() {
        return dinosaurioService.obtenerHerbivorosAcuaticos();
    }

    @GetMapping("/omnivoros/terrestres")
    public Flux<OmnivoroTerrestre> getOmnivorosTerrestres() {
        return dinosaurioService.obtenerOmnivorosTerrestres();
    }

    @GetMapping("/omnivoros/voladores")
    public Flux<OmnivoroVolador> getOmnivorosVoladores() {
        return dinosaurioService.obtenerOmnivorosVoladores();
    }

    @GetMapping("/omnivoros/acuaticos")
    public Flux<OmnivoroAcuatico> getOmnivorosAcuaticos() {
        return dinosaurioService.obtenerOmnivorosAcuaticos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DinosaurioDTO>> getById(@PathVariable String id) {
        return dinosaurioService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<DinosaurioDTO> create(@RequestBody DinosaurioDTO dinosaurioDTO) {
        return dinosaurioService.create(dinosaurioDTO);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<DinosaurioDTO>> update(@PathVariable String id, @RequestBody DinosaurioDTO dinosaurioDTO) {
        return dinosaurioService.update(id, dinosaurioDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return dinosaurioService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
