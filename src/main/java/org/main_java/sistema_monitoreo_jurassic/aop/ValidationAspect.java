package org.main_java.sistema_monitoreo_jurassic.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.main_java.sistema_monitoreo_jurassic.model.UsuarioDTO;
import org.main_java.sistema_monitoreo_jurassic.model.islasDTO.IslaDTO;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;
import org.main_java.sistema_monitoreo_jurassic.repos.CredencialesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class ValidationAspect {

    @Autowired
    private CredencialesRepository credencialesRepository;

    @Before("execution(* org.main_java.sistema_monitoreo_jurassic.service.IslaService.create(..)) || " +
            "execution(* org.main_java.sistema_monitoreo_jurassic.service.IslaService.delete(..)) || " +
            "execution(* org.main_java.sistema_monitoreo_jurassic.service.DinosaurioService.create(..)) || " +
            "execution(* org.main_java.sistema_monitoreo_jurassic.service.DinosaurioService.delete(..)) || " +
            "execution(* org.main_java.sistema_monitoreo_jurassic.service.UsuarioService.create())")
    public void validateInputs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                throw new IllegalArgumentException("Parámetro no válido: " + joinPoint.getSignature().getName());
            }
            if (arg instanceof IslaDTO) {
                validateIslaDTO((IslaDTO) arg);
            } else if (arg instanceof DinosaurioDTO) {
                validateDinosaurioDTO((DinosaurioDTO) arg);
            } else if (arg instanceof UsuarioDTO) {
                validateUsuarioDTO((UsuarioDTO) arg);
            } else if (arg instanceof String) {
                validateStringArg((String) arg);
            }
        }
        System.out.println("Validación completada para el método: " + joinPoint.getSignature().getName());
    }

    private void validateIslaDTO(IslaDTO islaDTO) {
        if (islaDTO.getNombre() == null || islaDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la isla no puede estar vacío.");
        }
        if (islaDTO.getCapacidadMaxima() <= 0) {
            throw new IllegalArgumentException("La capacidad máxima de la isla debe ser mayor que cero.");
        }
    }

    private void validateDinosaurioDTO(DinosaurioDTO dinosaurioDTO) {
        if (dinosaurioDTO.getNombre() == null || dinosaurioDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del dinosaurio no puede estar vacío.");
        }
        if (dinosaurioDTO.getSensores() == null || dinosaurioDTO.getSensores().isEmpty()) {
            throw new IllegalArgumentException("El dinosaurio debe tener al menos un sensor configurado.");
        }
    }

    private void validateUsuarioDTO(UsuarioDTO usuarioDTO) {
        // Validar nombre
        if (usuarioDTO.getNombre() == null || !usuarioDTO.getNombre().matches("[A-Za-záéíóúÁÉÍÓÚñÑ]+")) {
            throw new IllegalArgumentException("El nombre debe contener solo letras.");
        }

        // Validar apellidos
        if (usuarioDTO.getApellido1() == null || !usuarioDTO.getApellido1().matches("[A-Za-záéíóúÁÉÍÓÚñÑ]+")) {
            throw new IllegalArgumentException("El primer apellido debe contener solo letras.");
        }
        if (usuarioDTO.getApellido2() == null || !usuarioDTO.getApellido2().matches("[A-Za-záéíóúÁÉÍÓÚñÑ]+")) {
            throw new IllegalArgumentException("El segundo apellido debe contener solo letras.");
        }

        // Validar correo
        if (usuarioDTO.getCorreo() == null || !usuarioDTO.getCorreo().matches("^[\\w-.]+@(gmail\\.com|hotmail\\.com|yahoo\\.com)$")) {
            throw new IllegalArgumentException("El correo debe ser de una extensión válida (@gmail.com, @hotmail.com, @yahoo.com).");
        }

        // Validar teléfono
        Integer telefono = usuarioDTO.getTelefono();
        if (telefono == null || !isValidPhoneNumber(telefono)) {
            throw new IllegalArgumentException("El teléfono debe contener exactamente 9 dígitos numéricos.");
        }

        String contrasena = obtenerContrasenaDeCredenciales(usuarioDTO.getCredencialesId()).block();
        if (!isValidPassword(contrasena)) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra, un número y un carácter especial.");
        }

        // Validar rolId y credencialesId
        if (usuarioDTO.getRolId() == null || usuarioDTO.getRolId().trim().isEmpty()) {
            throw new IllegalArgumentException("El rol del usuario no puede estar vacío.");
        }
        if (usuarioDTO.getCredencialesId() == null || usuarioDTO.getCredencialesId().trim().isEmpty()) {
            throw new IllegalArgumentException("Las credenciales del usuario no pueden estar vacías.");
        }
    }

    private boolean isValidPhoneNumber(Integer phoneNumber) {
        String phoneStr = phoneNumber.toString();
        return phoneStr.length() == 9 && phoneStr.chars().allMatch(Character::isDigit);
    }

    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$");
    }

    private boolean isValidRol(String rol) {
        return "admin".equalsIgnoreCase(rol) || "user".equalsIgnoreCase(rol) || "paleontologo".equalsIgnoreCase(rol);
    }

    private Mono<String> obtenerContrasenaDeCredenciales(String credencialesId) {
        return credencialesRepository.findById(credencialesId)
                .map(credenciales -> credenciales.getPassword())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontraron credenciales con el ID especificado.")));
    }

    private void validateStringArg(String arg) {
        if (arg.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador no puede estar vacío.");
        }
    }
}
