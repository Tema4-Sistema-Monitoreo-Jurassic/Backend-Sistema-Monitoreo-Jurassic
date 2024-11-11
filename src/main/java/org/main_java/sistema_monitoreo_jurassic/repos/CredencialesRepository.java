package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.Credenciales;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CredencialesRepository extends ReactiveMongoRepository<Credenciales, String> {
    // Método para buscar credenciales por nombre o correo
    Mono<Credenciales> findByNombreOrCorreo(String nombre, String correo);
}
