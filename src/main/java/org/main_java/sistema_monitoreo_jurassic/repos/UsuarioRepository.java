package org.main_java.sistema_monitoreo_jurassic.repos;

import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

// UsuarioRepository.java
// UsuarioRepository.java
public interface UsuarioRepository extends ReactiveMongoRepository<Usuario, String> {
    Mono<Usuario> findFirstByNombre(String nombre);
    Mono<Usuario> findFirstByCorreo(String correo);
}


