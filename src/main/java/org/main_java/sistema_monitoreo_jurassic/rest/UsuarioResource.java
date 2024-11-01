package org.main_java.sistema_monitoreo_jurassic.rest;

import org.main_java.sistema_monitoreo_jurassic.model.UsuarioDTO;
import org.main_java.sistema_monitoreo_jurassic.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/usuarios")
public class UsuarioResource {

    private final UsuarioService usuarioService;

    public UsuarioResource(final UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuario(@PathVariable final Long id) {
        UsuarioDTO usuario = usuarioService.get(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Long> createUsuario(@RequestBody final UsuarioDTO usuarioDTO) {
        Long createdId = usuarioService.create(usuarioDTO);
        return ResponseEntity.ok(createdId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUsuario(@PathVariable final Long id,
                                              @RequestBody final UsuarioDTO usuarioDTO) {
        usuarioService.update(id, usuarioDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable final Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Nueva ruta para aumentar el poder del usuario
    @PutMapping("/{id}/aumentar-poder")
    public ResponseEntity<Void> aumentarPoderUsuario(@PathVariable final Long id, @RequestParam int incremento) {
        usuarioService.aumentarPoderUsuario(id, incremento);
        return ResponseEntity.ok().build();
    }
}
