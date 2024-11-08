package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.Rol;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RolRepository extends ReactiveMongoRepository<Rol, String> {
    Mono<Rol> findByNombre(String nombre);
}