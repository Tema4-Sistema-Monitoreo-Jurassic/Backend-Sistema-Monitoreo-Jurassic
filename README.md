# Sistema de Monitoreo de Dinosaurios - Backend (Spring WebFlux)


# LINKS

FRONTEND: https://github.com/Tema-3-Programacion-Concurrente/Frontend-React-Practica_3.git

BACKEND: https://github.com/Tema-3-Programacion-Concurrente/Backend-Springboot-Aspectos.git


---

# PARTICIPANTES

- **Nombre del equipo**: Jurassic Park Monitoring Team
- **Miembros**:
  - Jaime López Díaz
  - Nicolás Jimenez
  - Marcos García Benito

---

## Tabla de Contenidos

- [Introducción](#introducción)
- [Características del Sistema](#características-del-sistema)
  - [Tipos de Dinosaurios y Sensores](#tipos-de-dinosaurios-y-sensores)
  - [Islas y Entornos](#islas-y-entornos)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Implementación Reactiva y Concurrencia](#implementación-reactiva-y-concurrencia)
  - [Gestión de Concurrencia](#gestión-de-concurrencia)
  - [Backpressure y Flujos de Datos](#backpressure-y-flujos-de-datos)
- [Servicios REST](#servicios-rest)
  - [Endpoints Principales](#endpoints-principales)
  - [Autenticación y Seguridad](#autenticación-y-seguridad)
- [Aspectos y Patrones Utilizados](#aspectos-y-patrones-utilizados)
  - [Aspectos (AOP)](#aspectos-aop)
  - [Patrones Utilizados](#patrones-utilizados)
- [Extras Importantes](#extras-importantes)
- [Contribuciones y Licencia](#contribuciones-y-licencia)

---

# Introducción

El sistema de monitoreo de Jurassic Park es un backend avanzado desarrollado en Spring WebFlux con una base de datos reactiva en MongoDB y soporte de mensajería asíncrona con RabbitMQ. Su propósito es gestionar en tiempo real la actividad y el estado de salud de dinosaurios dentro de un parque temático, utilizando sensores de frecuencia cardíaca, movimiento y temperatura. Este sistema está diseñado para ofrecer una respuesta rápida y escalable ante eventos críticos, como cambios en la salud de los dinosaurios o interacciones entre ellos. Además, el sistema incluye una mecánica de envejecimiento en tiempo real, junto con un ecosistema reactivo en el que los dinosaurios interactúan según su tipo, alimentación, y entorno.

# Características del Sistema

### Tipos de Dinosaurios y Sensores

1. **Clasificación de Dinosaurios**:

   - **Carnívoros**: Se dividen en acuáticos, terrestres y voladores, cada uno con reglas específicas de caza. Por ejemplo, un carnívoro acuático puede cazar otros dinosaurios acuáticos, mientras que un carnívoro volador tiene acceso a una gama más amplia de presas.
   - **Herbívoros**: Al igual que los carnivoros los herbívoros también pueden ser acuaticos, terrestres o voladores pero se alimenteas únicamente de planas.
   - **Omnívoros**: Tienen una dieta más flexible y pueden consumir tanto plantas como otros dinosaurios, dependiendo de su tipo.
2. **Sensores de Monitoreo**:

   - **Frecuencia Cardíaca**: Monitorea la salud cardiovascular del dinosaurio y ayuda a detectar signos de estrés o enfermedad.
   - **Movimiento**: Controla la actividad y el desplazamiento de los dinosaurios, permitiendo detectar patrones de comportamiento.
   - **Temperatura**: Registra la temperatura corporal de los dinosaurios para identificar posibles enfermedades.

### Islas y Entornos

1. **Islas Especializadas**:
   - **Isla Terrestre/Aérea**: Apta para dinosaurios terrestres y voladores, proporcionando un entorno adecuado para sus hábitos de vida.
   - **Isla Acuática**: Diseñada específicamente para especies acuáticas, con configuraciones para permitir o restringir el acceso según el tipo de dinosaurio.
   - **Enfermería**: Un espacio de recuperación temporal donde se envían dinosaurios enfermos. Aquí, los dinosaurios permanecen hasta que sus niveles de salud vuelven a la normalidad, con un tiempo máximo de estancia de 30 minutos.
2. **Criaderos**:
   - **Acuático, Terrestre y Volador**: Criaderos específicos donde se alojan dinosaurios menores de 3 años. Aquí los dinosaurios jóvenes son protegidos y monitoreados hasta alcanzar la edad de madurez, cuando serán realocados a islas de adultos.

- **Asignación y Realocación entre Islas**:
  - **Asignación Inicial**:  Los dinosaurios son asignados a criaderos específicos (acuáticos, terrestres, voladores) dependiendo de su tipo y edad. Al alcanzar la madurez, son transferidos automáticamente a las islas correspondientes (Terrestre/Aérea, Acuática, etc.), donde convivirán con otros dinosaurios de su tipo.
  - **Interacción entre Dinosaurios**: Los dinosaurios interactúan entre sí basándose en sus reglas de alimentación y subtipos. Por ejemplo, un carnívoro terrestre puede cazar herbívoros acuáticos y terrestres, pero no puede atacar a dinosaurios en la enfermería.
  - **Eliminación de Dinosaurios**: Los dinosaurios que envejecen y mueren, o que son cazados por otros dinosaurios, son eliminados automáticamente del sistema. El proceso de eliminación incluye su retiro del entorno y la liberación de su espacio en el tablero de la isla.
  - Esta estructura permite la simulación de un ecosistema en tiempo real, con mecánicas de envejecimiento, salud y relaciones entre depredadores y presas.

---

# Arquitectura del Proyecto

El proyecto está organizado en varios paquetes de acuerdo con la lógica de dominio:

1. **Dominio (domain)** - Contiene entidades como Dinosaurio, Isla, Sensor, Usuario y Rol.
2. **Servicios (service)** - Lógica de negocio (DinosaurioService, IslaService, SensorService).
3. **Controladores (controller)** - Controladores REST que exponen los servicios a través de endpoints.
4. **Repositorios (repos)** - Interfaces para MongoDB reactivo.
5. **DTOs (model)** - Objetos de Transferencia de Datos (Data Transfer Objects) para intercambiar datos entre el backend y el frontend.
6. Paquetes alternos orientados a otras tareas como **[Config, Messaging, aop, util etc...]**

# Implementación Reactiva y Concurrencia

### Gestión de Concurrencia

- **Flujos Concurrentes**: Uso de Flux y Mono para gestionar datos de sensores en tiempo real.
- **Schedulers y Locks**: Uso de Schedulers y mecanismos de bloqueo en áreas críticas para asegurar la concurrencia y consistencia de los datos.

### Backpressure y Flujos de Datos

El sistema usa `onBackpressureBuffer` para gestionar la sobrecarga de flujos de datos, permitiendo que el sistema maneje grandes volúmenes de información de los sensores sin bloquearse cuando las señales llegan a un ritmo muy rápido.

---

# Servicios REST

### Endpoints Principales


| Recurso            | Método | Endpoint                                 | Descripción                                         |
| ------------------ | ------- | ---------------------------------------- | ---------------------------------------------------- |
| **Dinosaurios**    | GET     | `/api/dinosaurios/{id}`                  | Obtiene información de un dinosaurio específico    |
|                    | POST    | `/api/dinosaurios`                       | Crea un nuevo dinosaurio en el sistema               |
|                    | PUT     | `/api/dinosaurios/{id}`                  | Actualiza los datos de un dinosaurio específico     |
|                    | DELETE  | `/api/dinosaurios/{id}`                  | Elimina un dinosaurio del sistema                    |
|                    | GET     | `/api/dinosaurios/carnivoros/terrestres` | Obtiene todos los dinosaurios carnívoros terrestres |
|                    | GET     | `/api/dinosaurios/carnivoros/voladores`  | Obtiene todos los dinosaurios carnívoros voladores  |
|                    | GET     | `/api/dinosaurios/carnivoros/acuaticos`  | Obtiene todos los dinosaurios carnívoros acuáticos |
|                    | GET     | `/api/dinosaurios/herbivoros/terrestres` | Obtiene todos los dinosaurios herbívoros terrestres |
|                    | GET     | `/api/dinosaurios/herbivoros/voladores`  | Obtiene todos los dinosaurios herbívoros voladores  |
|                    | GET     | `/api/dinosaurios/herbivoros/acuaticos`  | Obtiene todos los dinosaurios herbívoros acuáticos |
|                    | GET     | `/api/dinosaurios/omnivoros/terrestres`  | Obtiene todos los dinosaurios omnívoros terrestres  |
|                    | GET     | `/api/dinosaurios/omnivoros/voladores`   | Obtiene todos los dinosaurios omnívoros voladores   |
|                    | GET     | `/api/dinosaurios/omnivoros/acuaticos`   | Obtiene todos los dinosaurios omnívoros acuáticos  |
| **Islas**          | GET     | `/api/islas`                             | Listado de islas                                     |
|                    | POST    | `/api/islas`                             | Crea una nueva isla en el sistema                    |
|                    | GET     | `/api/islas/{id}`                        | Obtiene información de una isla específica         |
|                    | PUT     | `/api/islas/{id}`                        | Actualiza los datos de una isla específica          |
|                    | DELETE  | `/api/islas/{id}`                        | Elimina una isla del sistema                         |
|                    | GET     | `/api/islas/{id}/tablero`                | Obtiene el tablero de una isla específica           |
| **Sensores**       | GET     | `/api/sensores`                          | Listado de sensores activos                          |
|                    | POST    | `/api/sensores`                          | Crea un nuevo sensor en el sistema                   |
|                    | PUT     | `/api/sensores/{id}`                     | Actualiza el estado de un sensor específico         |
|                    | DELETE  | `/api/sensores/{id}`                     | Elimina un sensor del sistema                        |
|                    | GET     | `/api/sensores/movimiento`               | Obtiene todos los sensores de movimiento             |
|                    | GET     | `/api/sensores/temperatura`              | Obtiene todos los sensores de temperatura            |
|                    | GET     | `/api/sensores/frecuencia_cardiaca`      | Obtiene todos los sensores de frecuencia cardíaca   |
| **Eventos**        | GET     | `/api/eventos`                           | Listado de eventos                                   |
|                    | GET     | `/api/eventos/{id}`                      | Obtiene los detalles de un evento específico        |
|                    | POST    | `/api/eventos`                           | Crea un nuevo evento                                 |
|                    | PUT     | `/api/eventos/{id}`                      | Actualiza un evento específico                      |
|                    | DELETE  | `/api/eventos/{id}`                      | Elimina un evento del sistema                        |
| **Autenticación** | POST    | `/api/auth/login`                        | Permite a un usuario iniciar sesión (JWT)           |
|                    | POST    | `/api/auth/register`                     | Permite registrar un nuevo usuario                   |
| **Usuarios**       | GET     | `/api/usuarios`                          | Listado de usuarios                                  |
|                    | GET     | `/api/usuarios/{id}`                     | Obtiene la información de un usuario específico    |
|                    | POST    | `/api/usuarios`                          | Crea un nuevo usuario en el sistema                  |
|                    | PUT     | `/api/usuarios/{id}`                     | Actualiza los datos de un usuario específico        |
|                    | DELETE  | `/api/usuarios/{id}`                     | Elimina un usuario del sistema                       |
| **Home**           | GET     | `/home`                                  | Endpoint de bienvenida                               |

### Autenticación y Seguridad

- **Roles**: Existen tres roles básicos: `user`, `admin`, y `paleontólogo`, que controlan los accesos a diferentes partes del sistema.
- **Autenticación JWT**: Se usa un sistema de autenticación basado en tokens JWT para proteger los endpoints sensibles y verificar la identidad de los usuarios.

---

# Aspectos y Patrones Utilizados

### Aspectos (AOP)

1. **Seguridad**:

   - **Control de Acceso**: El aspecto `SecurityAspect` asegura que solo usuarios autorizados puedan acceder a endpoints sensibles.
   - **Validación de Permisos**: Verifica que el usuario tenga los permisos necesarios antes de ejecutar ciertos métodos.
2. **Monitoreo de Eventos**:

   - **Auditoría de Acciones**: `AuditAspect` registra todas las acciones sensibles, como la adición o eliminación de dinosaurios.
   - **Registro de Alerta de Sensores**: Intercepta métodos de sensores y activa alertas cuando algún valor está fuera de rango.
3. **Gestión de Transacciones**:

   - **Consistencia de Datos**: `TransactionAspect` asegura que las operaciones en la base de datos se completen correctamente, manteniendo la consistencia de los datos.
   - **Sincronización en Métodos de Cambio de Estado**: Asegura que operaciones críticas en el estado de dinosaurios o sensores sean consistentes y libres de conflictos.
4. **Manejo de Errores**:

   - **Aspecto de Manejo de Errores (`ErrorHandlingAspect`)**: Captura excepciones en los métodos del servicio, registrando errores para facilitar el monitoreo y diagnóstico.
5. **Notificaciones**:

   - **Aspecto de Notificación (`NotificationAspect`)**: Envía notificaciones a través de RabbitMQ cuando se registra un evento crítico en el sistema.
6. **Monitoreo de Rendimiento**:

   - **Aspecto de Monitoreo de Rendimiento (`PerformanceMonitoringAspect`)**: Mide el tiempo de ejecución de métodos críticos, ayudando a identificar y resolver cuellos de botella.
7. **Validación de Datos**:

   - **Aspecto de Validación (`ValidationAspect`)**: Valida datos de entrada en métodos clave para asegurar que cumplan con los requisitos antes de ser procesados.

## Patrones Utilizados

1. **Data Transfer Object (DTO)**: Separa la capa de presentación y simplifica la transferencia de datos.
2. **Repository Pattern**: Desacopla la lógica de acceso a datos de la lógica de negocio.
3. **Service Layer**: Organiza la lógica de negocio en servicios dedicados.
4. **Factory Pattern**: Usado en `DinosaurFactory`, `IslaFactory`, y `SensorFactory` para crear instancias de subtipos específicos.
5. **Singleton**: Asegura una única instancia en servicios críticos.

---

# Extras Importantes

- **Usuarios de Prueba**:

  - `admin@example.com` - Password: `admin123`
  - `user@example.com` - Password: `user123`
- **Uso en Frontend**: Los valores de sensores y estados de dinosaurios se reflejan en el frontend en tiempo real, permitiendo la gestión y monitoreo en un tablero visual.

---

# Contribuciones y Licencia

- **Contribuciones**: Abiertas a colaboradores interesados en monitoreo en tiempo real y sistemas reactivos.
- **Licencia**: Detalles de licencia especificando derechos de uso y modificaciones del código.
