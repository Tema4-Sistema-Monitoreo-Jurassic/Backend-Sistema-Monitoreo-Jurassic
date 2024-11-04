package org.main_java.sistema_monitoreo_jurassic.service;

import org.main_java.sistema_monitoreo_jurassic.domain.Datos;
import org.main_java.sistema_monitoreo_jurassic.domain.Evento;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Posicion;
import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorFrecuenciaCardiaca;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorMovimiento;
import org.main_java.sistema_monitoreo_jurassic.domain.sensores.SensorTemperatura;
import org.main_java.sistema_monitoreo_jurassic.model.EventoDTO;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    private final EventoService eventoService;

    public SensorService(SensorRepository sensorRepository, SensorFactory sensorFactory, EventoService eventoService) {
        this.sensorRepository = sensorRepository;
        this.sensorFactory = sensorFactory;
        this.eventoService = eventoService;
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

    public Mono<Map<String, Double>> generarValoresAleatoriosParaSensoresTempCard(Dinosaurio dino) {
        Random random = new Random();
        Map<String, Double> valoresSensores = new HashMap<>();

        return Flux.fromIterable(dino.getSensores())
                .filter(sensor -> sensor instanceof SensorTemperatura || sensor instanceof SensorFrecuenciaCardiaca)
                .flatMap(sensor -> {
                    if (sensor instanceof SensorTemperatura sensorTemp) {
                        double valorAleatorioTemperatura = generarValorAleatorio(sensorTemp.getLimiteInferior() - 10, sensorTemp.getLimiteSuperior() + 10, random);
                        valoresSensores.put("temperatura", valorAleatorioTemperatura);
                        actualizarValorSensor(sensorTemp, valorAleatorioTemperatura).subscribe();

                        // Verifica si el valor está fuera de rango y genera un evento si es necesario
                        if (valorAleatorioTemperatura < sensorTemp.getLimiteInferior() || valorAleatorioTemperatura > sensorTemp.getLimiteSuperior()) {
                            Evento evento = new Evento("Temperatura fuera de rango", valorAleatorioTemperatura);
                            eventoService.enviarAlerta(evento).subscribe();
                            eventoService.create(new EventoDTO(evento.getMensaje(), evento.getValor(), evento.getDateCreated())).subscribe();
                        }

                        Datos dato = new Datos(LocalDateTime.now(), valorAleatorioTemperatura);
                        return sensorTemp.agregarDato(dato).then(sensorRepository.save(sensorTemp)).then();
                    } else if (sensor instanceof SensorFrecuenciaCardiaca sensorFC) {
                        double valorAleatorioFrecuencia = generarValorAleatorio(sensorFC.getLimiteInferior() - 10, sensorFC.getLimiteSuperior() + 10, random);
                        valoresSensores.put("frecuenciaCardiaca", valorAleatorioFrecuencia);
                        actualizarValorSensor(sensorFC, valorAleatorioFrecuencia).subscribe();

                        // Verifica si el valor está fuera de rango y genera un evento si es necesario
                        if (valorAleatorioFrecuencia < sensorFC.getLimiteInferior() || valorAleatorioFrecuencia > sensorFC.getLimiteSuperior()) {
                            Evento evento = new Evento("Frecuencia cardíaca fuera de rango", valorAleatorioFrecuencia);
                            eventoService.enviarAlerta(evento).subscribe();
                            eventoService.create(new EventoDTO(evento.getMensaje(), evento.getValor(), evento.getDateCreated())).subscribe();
                        }

                        Datos dato = new Datos(LocalDateTime.now(), valorAleatorioFrecuencia);
                        return sensorFC.agregarDato(dato).then(sensorRepository.save(sensorFC)).then();
                    }
                    return Mono.empty();
                })
                .then(Mono.just(valoresSensores));
    }

    // Metodo auxiliar para generar un valor aleatorio dentro del rango definido
    private double generarValorAleatorio(double limiteInferior, double limiteSuperior, Random random) {
        return limiteInferior + (limiteSuperior - limiteInferior) * random.nextDouble();
    }

    // Metodo auxiliar para actualizar el valor en el sensor y guardarlo en el repositorio
    private Mono<Void> actualizarValorSensor(Sensor sensor, double nuevoValor) {
        sensor.setValor(nuevoValor); // Asume que el sensor tiene un campo valorActual que guarda el último valor
        return sensorRepository.save(sensor).then();
    }

    public Mono<Void> detectarYRegistrarMovimiento(Dinosaurio dino, Posicion posicionAnterior, Posicion nuevaPosicion) {
        // Determina el valor de movimiento basado en la dirección del desplazamiento
        double valorMovimiento = (posicionAnterior.getY() != nuevaPosicion.getY()) ? 1.0 : 0.0;

        // Busca el sensor de movimiento del dinosaurio
        return Flux.fromIterable(dino.getSensores())
                .filter(sensor -> sensor instanceof SensorMovimiento)
                .cast(SensorMovimiento.class)
                .flatMap(sensorMovimiento -> {
                    // Actualiza el valor en el sensor y guarda el dato
                    sensorMovimiento.setValor(valorMovimiento);
                    Datos nuevoDato = new Datos(LocalDateTime.now(), valorMovimiento);

                    //Generamos un evento de movimiento
                    Evento evento = new Evento("Movimiento detectado", valorMovimiento);
                    eventoService.enviarAlerta(evento).subscribe();
                    eventoService.create(new EventoDTO(evento.getMensaje(), evento.getValor(), evento.getDateCreated())).subscribe();
                    return sensorMovimiento.agregarDato(nuevoDato)
                            .then(sensorRepository.save(sensorMovimiento)) // Guarda el sensor actualizado
                            .then(generarEventoSiFueraDeRango(sensorMovimiento, valorMovimiento)); // Genera un evento si está fuera de rango
                })
                .then();
    }

    // Metodo auxiliar para generar un evento si el valor de movimiento está fuera de rango
    private Mono<Void> generarEventoSiFueraDeRango(SensorMovimiento sensorMovimiento, double valorMovimiento) {
        if (valorMovimiento < sensorMovimiento.getLimiteInferior() || valorMovimiento > sensorMovimiento.getLimiteSuperior()) {
            Evento evento = new Evento("Movimiento fuera de rango", valorMovimiento);
            System.out.println("Generando evento: " + evento.getMensaje() + " - Valor: " + valorMovimiento);

            // Enviar alerta y guardar el evento en la base de datos
            return eventoService.enviarAlerta(evento)
                    .then(eventoService.create(new EventoDTO(evento.getMensaje(), evento.getValor(), evento.getDateCreated())))
                    .then();
        }
        return Mono.empty();
    }

}