package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.islas.Isla;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IslaRepository extends ReactiveMongoRepository<Isla, String> {
}