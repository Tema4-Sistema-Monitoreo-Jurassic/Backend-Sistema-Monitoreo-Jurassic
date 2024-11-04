package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorDTO;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorFrecuenciaCardiacaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorMovimientoDTO;
import org.main_java.sistema_monitoreo_jurassic.model.sensoresDTO.SensorTemperaturaDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.SensorRepository;
import org.main_java.sistema_monitoreo_jurassic.service.factory.SensorFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final SensorFactory sensorFactory;
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceTipo = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceDelete = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceCreate = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceUpdate = Executors.newFixedThreadPool(50);
    private final ExecutorService executorServiceGetById = Executors.newFixedThreadPool(50);

    public SensorService(SensorRepository sensorRepository, SensorFactory sensorFactory) {
        this.sensorRepository = sensorRepository;
        this.sensorFactory = sensorFactory;
    }

    // metodo para obtener todos los sensores
    public Flux<Sensor> getAll() {
        return sensorRepository.findAll()
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    // metodo general para obtener subtipos de sensores usando filtrado concurrente
    private <T extends Sensor> Flux<T> obtenerSensoresPorTipo(Class<T> tipoClase) {
        return getAll()
                .filter(sensor -> tipoClase.isInstance(sensor))
                .cast(tipoClase)
                .subscribeOn(Schedulers.fromExecutor(executorServiceTipo));
    }

    // metodo para obtener sensores de temperatura
    public Flux<SensorTemperatura> obtenerSensoresDeTemperatura() {
        return obtenerSensoresPorTipo(SensorTemperatura.class);
    }

    // metodo para obtener sensores de movimiento
    public Flux<SensorMovimiento> obtenerSensoresDeMovimiento() {
        return obtenerSensoresPorTipo(SensorMovimiento.class);
    }

    // metodo para obtener sensores de frecuencia cardiaca
    public Flux<SensorFrecuenciaCardiaca> obtenerSensoresDeFrecuenciaCardiaca() {
        return obtenerSensoresPorTipo(SensorFrecuenciaCardiaca.class);
    }

    // metodo para obtener un sensor por su id
    public Mono<Sensor> getById(String id) {
        return sensorRepository.findById(id)
                .subscribeOn(Schedulers.fromExecutor(executorServiceGetById));
    }

    // metodo para registrar un sensor
    public Mono<Sensor> create(String id, String tipo, double limiteInferior, double limiteSuperior) {
        Sensor sensor = sensorFactory.crearSensor(id, tipo, limiteInferior, limiteSuperior);
        return Mono.fromCallable(() -> sensorRepository.save(sensor))
                .subscribeOn(Schedulers.fromExecutor(executorServiceCreate))
                .flatMap(mono -> mono);
    }

    // metodo para actualizar un sensor a partir de un DTO
    public Mono<SensorDTO> update(String id, SensorDTO sensorActualizadoDTO) {
        return sensorRepository.findById(id)
                .flatMap(sensorExistente -> mapToEntity(sensorActualizadoDTO)
                        .map(sensorActualizado -> {
                            sensorExistente.setTipo(sensorActualizado.getTipo());
                            sensorExistente.setLimiteInferior(sensorActualizado.getLimiteInferior());
                            sensorExistente.setLimiteSuperior(sensorActualizado.getLimiteSuperior());
                            return sensorExistente;
                        }))
                .flatMap(sensor -> sensorRepository.save(sensor))
                .subscribeOn(Schedulers.fromExecutor(executorServiceUpdate))
                .flatMap(this::mapToDTO);
    }

    // metodo para eliminar un sensor
    public Mono<Void> delete(String id) {
        return Mono.fromRunnable(() -> sensorRepository.deleteById(id))
                .subscribeOn(Schedulers.fromExecutor(executorServiceDelete))
                .then();
    }

    // metodo para mapear una entidad a un DTO
    public Mono<SensorDTO> mapToDTO(Sensor sensor) {
        return Mono.fromCallable(() -> {
            SensorDTO dto;
            if (sensor instanceof SensorFrecuenciaCardiaca) {
                dto = new SensorFrecuenciaCardiacaDTO();
            } else if (sensor instanceof SensorTemperatura) {
                dto = new SensorTemperaturaDTO();
            } else if (sensor instanceof SensorMovimiento) {
                dto = new SensorMovimientoDTO();
            } else {
                throw new IllegalArgumentException("Unknown dinosaur type");
            }
            dto.setId(sensor.getId());
            dto.setTipo(sensor.getTipo());
            dto.setLimiteInferior(sensor.getLimiteInferior());
            dto.setLimiteSuperior(sensor.getLimiteSuperior());
            return dto;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // metodo para mapear un DTO a una entidad
    public Mono<Sensor> mapToEntity(SensorDTO dto) {
        return Mono.fromCallable(() -> {
            Sensor sensor = sensorFactory.crearSensor(dto.getId(), dto.getTipo(), dto.getLimiteInferior(), dto.getLimiteSuperior());
            sensor.setId(dto.getId());
            sensor.setTipo(dto.getTipo());
            sensor.setLimiteInferior(dto.getLimiteInferior());
            sensor.setLimiteSuperior(dto.getLimiteSuperior());
            return sensor;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
