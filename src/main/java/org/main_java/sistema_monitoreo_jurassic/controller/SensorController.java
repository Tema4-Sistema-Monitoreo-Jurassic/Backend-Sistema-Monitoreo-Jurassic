package org.main_java.sistema_monitoreo_jurassic.controller;

import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorDTO;
import org.main_java.sistema_monitoreo_jurassic.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/sensores")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @GetMapping
    public Flux<Sensor> getAll() {
        return sensorService.getAll();
    }

    @GetMapping("/movimiento")
    public Flux<SensorMovimiento> getSensoresMovimiento() {
        return sensorService.obtenerSensoresDeMovimiento();
    }

    @GetMapping("/temperatura")
    public Flux<SensorTemperatura> getSensoresTemperatura() {
        return sensorService.obtenerSensoresDeTemperatura();
    }

    @GetMapping("/frecuencia_cardiaca")
    public Flux<SensorFrecuenciaCardiaca> getSensoresFrecuenciaCardiaca() {
        return sensorService.obtenerSensoresDeFrecuenciaCardiaca();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Sensor>> getById(@PathVariable String id) {
        return sensorService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<Sensor> create(@RequestParam String id, @RequestParam String tipo,
                               @RequestParam double limiteInferior, @RequestParam double limiteSuperior) {
        return sensorService.create(id, tipo, limiteInferior, limiteSuperior);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<SensorDTO>> update(@PathVariable String id, @RequestBody SensorDTO sensorActualizadoDTO) {
        return sensorService.update(id, sensorActualizadoDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return sensorService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
