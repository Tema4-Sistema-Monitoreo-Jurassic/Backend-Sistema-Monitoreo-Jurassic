package org.main_java.sistema_monitoreo_jurassic.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.main_java.sistema_monitoreo_jurassic.domain.Usuario;
import org.main_java.sistema_monitoreo_jurassic.domain.Rol;
import org.main_java.sistema_monitoreo_jurassic.repos.RolRepository;
import org.main_java.sistema_monitoreo_jurassic.repos.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class SecurityAspect {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Mapeo de métodos a roles permitidos
    private final Map<String, List<String>> methodRoleMap = Map.of(
            "registrarUsuario", Arrays.asList("ADMINISTRADOR"),
            "eliminarUsuario", Arrays.asList("ADMINISTRADOR"),
            "actualizarRol", Arrays.asList("ADMINISTRADOR"),
            "visualizarUsuarios", Arrays.asList("ADMINISTRADOR", "PALEONTOLOGO")
    );

    @Before("execution(* org.main_java.sistema_monitoreo_jurassic.service.UsuarioService.*(..))")
    public void checkAuthorization(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Verificación de autorización para método: " + methodName);

        obtenerUsuarioActual()
                .flatMap(usuario -> {
                    String rolId = usuario.getRolId(); // Obtener el rolId en lugar del rol directo

                    // Buscar el rol en la base de datos utilizando rolId
                    return rolRepository.findById(rolId)
                            .doOnNext(rol -> {
                                if (rol == null) {
                                    throw new AccessDeniedException("El rol del usuario no es válido.");
                                }

                                List<String> rolesPermitidos = methodRoleMap.get(methodName);

                                // Verificar si el rol del usuario está autorizado para el metodo
                                if (rolesPermitidos == null || !rolesPermitidos.contains(rol.getNombre().toUpperCase())) {
                                    throw new AccessDeniedException("No tienes permisos para ejecutar el método: " + methodName);
                                }

                                System.out.println("Autorización concedida para el usuario: " + usuario.getNombre() +
                                        " con rol: " + rol.getNombre());
                            });
                })
                .block(); // Bloquea el flujo para completar la operación síncrona en el aspecto
    }

    private Mono<Usuario> obtenerUsuarioActual() {
        // Obtener el contexto de autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Mono.error(new AccessDeniedException("Usuario no autenticado"));
        }

        // Obtener el correo o identificador del usuario autenticado (según cómo esté configurada la autenticación)
        String userEmail = authentication.getName();

        // Consultar en el repositorio para obtener el usuario completo con su rolId
        return usuarioRepository.findByCorreo(userEmail)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Usuario no encontrado en el sistema")));
    }
}
