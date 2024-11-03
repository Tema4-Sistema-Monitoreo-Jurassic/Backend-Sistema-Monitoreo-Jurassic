package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.Credenciales;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CredencialesRepository extends ReactiveMongoRepository<Credenciales, String> {
}