package org.main_java.sistema_monitoreo_jurassic.rest;

import org.main_java.sistema_monitoreo_jurassic.model.UsuarioDTO;
import org.main_java.sistema_monitoreo_jurassic.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    /*private final UsuarioService usuarioService;

    public UsuarioResource(final UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public Flux<UsuarioDTO> getAllUsuarios() {
        // Retorna todos los usuarios como un flujo reactivo
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UsuarioDTO>> getUsuario(@PathVariable final String id) {
        // Busca un usuario específico por su ID y lo devuelve envuelto en un Mono de ResponseEntity
        return usuarioService.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createUsuario(@RequestBody final UsuarioDTO usuarioDTO) {
        // Crea un nuevo usuario y devuelve su ID en la respuesta
        return usuarioService.create(usuarioDTO)
                .map(createdId -> ResponseEntity.ok(createdId));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateUsuario(@PathVariable final String id,
                                                    @RequestBody final UsuarioDTO usuarioDTO) {
        // Actualiza el usuario especificado, o retorna notFound si el ID no existe
        return usuarioService.update(id, usuarioDTO)
                .map(updated -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUsuario(@PathVariable final String id) {
        // Elimina un usuario, devolviendo noContent si la operación fue exitosa
        return usuarioService.delete(id)
                .map(deleted -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }*/
}

