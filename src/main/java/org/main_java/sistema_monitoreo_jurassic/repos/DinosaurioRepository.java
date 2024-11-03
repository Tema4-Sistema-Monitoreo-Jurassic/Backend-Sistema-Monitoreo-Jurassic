package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.dinosaurios.Dinosaurio;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DinosaurioRepository extends ReactiveMongoRepository<Dinosaurio, String> {
}