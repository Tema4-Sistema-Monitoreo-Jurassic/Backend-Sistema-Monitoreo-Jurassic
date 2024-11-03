package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UsuarioRepository extends ReactiveMongoRepository<Usuario, String> {
    // Definimos el metodo para buscar un usuario por su correo electrónico.
    Mono<Usuario> findByCorreo(String correo);
}