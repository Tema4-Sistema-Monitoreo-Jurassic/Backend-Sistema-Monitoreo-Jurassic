package org.main_java.sistema_monitoreo_jurassic.rest;

import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorDTO;
import org.main_java.sistema_monitoreo_jurassic.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/sensores")
public class SensorResource {

    private final SensorService sensorService;

    public SensorResource(final SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public Flux<SensorDTO> getAllSensores() {
        return sensorService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<SensorDTO>> getSensor(@PathVariable final String id) {
        return sensorService.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createSensor(@RequestBody final SensorDTO sensorDTO) {
        return sensorService.create(sensorDTO)
                .map(createdId -> ResponseEntity.ok(createdId));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateSensor(@PathVariable final String id,
                                                   @RequestBody final SensorDTO sensorDTO) {
        return sensorService.update(id, sensorDTO)
                .map(updated -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSensor(@PathVariable final String id) {
        return sensorService.delete(id)
                .map(deleted -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

//Explicaion porsiacaso
/* SensorResource: Esta clase es un controlador REST de Spring Boot que gestiona entidades Sensor. Proporciona endpoints RESTful para crear, recuperar, actualizar y eliminar sensores, utilizando Mono y Flux para operaciones reactivas y no bloqueantes. */
