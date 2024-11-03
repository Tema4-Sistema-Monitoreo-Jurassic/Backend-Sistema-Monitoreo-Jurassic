package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.Rol;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RolRepository extends ReactiveMongoRepository<Rol, String> {
}
