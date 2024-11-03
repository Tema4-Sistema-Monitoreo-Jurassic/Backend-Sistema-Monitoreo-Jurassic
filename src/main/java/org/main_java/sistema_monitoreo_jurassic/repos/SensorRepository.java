package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.sensores.Sensor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SensorRepository extends ReactiveMongoRepository<Sensor, String> {
}