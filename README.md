# Sistema de Monitoreo de Dinosaurios - Backend (Spring WebFlux)

# LINKS

FRONTEND:

BACKEND:

---

# PARTICIPANTES

- **Nombre del equipo**: Jurassic Park Monitoring Team
- **Miembros**:
  - Jaime López Díaz
  - Nicolás Jimenez
  - Marcos García Benito
  - Juan Manuel Rodrigez

---

## Tabla de Contenidos

- [Introducción](#introducción)
- [Especificaciones Importantes](#especificaciones-importantes)
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
- [Contribuciones y Licencia](#contribuciones-y-licencia)

---

# Introducción

El sistema de monitoreo de Jurassic Park es un backend avanzado desarrollado en Spring WebFlux con una base de datos reactiva en MongoDB y soporte de mensajería asíncrona con RabbitMQ. Su propósito es gestionar en tiempo real la actividad y el estado de salud de dinosaurios dentro de un parque temático, utilizando sensores de frecuencia cardíaca, movimiento y temperatura. Este sistema está diseñado para ofrecer una respuesta rápida y escalable ante eventos críticos, como cambios en la salud de los dinosaurios o interacciones entre ellos. Además, el sistema incluye una mecánica de envejecimiento en tiempo real, junto con un ecosistema reactivo en el que los dinosaurios interactúan según su tipo, alimentación, y entorno.


# Especificaciones Importantes

### -Manejo de Errores en el Ciclo de Crecimiento de Dinosaurios

Cuando un dinosaurio se come a otro dinosaurio, si inmediatamente se ejecuta una acción sobre el ciclo de crecimiento del dinosaurio que ha sido comido, da error porque ese dinosaurio se lo han comido y ha sido eliminado del tablero y de la base de datos. Este error lo hemos capturado y controlado, y no interfiere en el desarrollo de la ejecución del programa, ya que a posteriori ya no se representa ni en la base de datos ni en el tablero. Se para su ciclo de crecimiento cuando se vuelva a ejecutar la función que monitorea el crecimiento del dinosaurio fallecido.

### -Generación de Usuarios

- Se generan 3 usuarios, uno de cada tipo, con:
  - **Correo**
  - **Nombre de usuario**
  - **Contraseña**

### -Despliegue de Dinosaurios

- Especifica los dinosaurios que se despliegan, en qué isla lo hacen y qué tipo son.

### -Tiempo de Crecimiento

- Cada mes pasa en 2 minutos de tiempo real.
- Tienen que pasar 12 meses para que crezca un dinosaurio.
- Por cada vez que crecen, se ejecutan 10 movimientos y 2 intentos de comer.

### -Manejo de Sensores y Enfermería

- Si los sensores se salen de su rango estimado, el dinosaurio es colocado en la posición (0,0) del tablero (si esta está ocupada, lo hace en la (0,1) y así sucesivamente).
- Cuando es llevado a esta posición (la entrada de las islas), el dinosaurio es llevado a la enfermería. Una vez allí, inicia una etapa de recuperación y luego es llevado a su isla correspondiente de nuevo.
- Una vez que llega a la isla, regresa a la misma posición de donde salió y, si esta está ocupada, se reubica.
- La enfermería tiene capacidad máxima para 1 dinosaurio, por lo que se tendrán que sincronizar para el acceso a la misma.

### -Cambio de Criadero a Isla

- Cuando los dinosaurios llegan a una determinada edad, se cambian del criadero a su isla correspondiente.

### -Función de Mortalidad

- A partir de los 20 años de edad, por cada año que pasa, se ejecuta una función que puede "matar" al dinosaurio si el resultado es `true`. Esta posibilidad aumenta en 0.02 con cada año que pasa.

### -Identificacion por defecto a nivel de uso:

- **Usuarios de Prueba**:

  - `admin@gmail.com` - Password: `a12345_67`
  - `usuario@gmail.com` - Password: `a12345_679`
  - `paleontologist@gmail.com` - Password: `a12345_678`
- **Uso en Frontend**: Los valores de sensores y estados de dinosaurios se reflejan en el frontend en tiempo real, permitiendo la gestión y monitoreo en un tablero visual.

# Características del Sistema

### Tipos de Dinosaurios y Sensores

1. **Clasificación de Dinosaurios**:

   - **Carnívoros**: Se dividen en acuáticos, terrestres y voladores, cada uno con reglas específicas de caza. Por ejemplo, un carnívoro acuático puede cazar otros dinosaurios acuáticos, mientras que un carnívoro volador tiene acceso a una gama más amplia de presas.
   - **Herbívoros**: Al igual que los carnivoros los herbívoros también pueden ser acuaticos, terrestres o voladores pero se alimenteas únicamente de planas.
   - **Omnívoros**: Misma mecanica de sub-tipado (acuáticos, terrestres y voladores). Tienen una dieta más flexible y pueden consumir tanto plantas como otros dinosaurios, dependiendo de su tipo.
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

# Servicios Principales

- **AuthService**:

  - **Login de Usuarios**: Utiliza `Mono` para realizar la autenticación reactiva del usuario, validando sus credenciales almacenadas de manera asíncrona. Si las credenciales son válidas, responde con un JWT simulado; en caso contrario, responde con un estado de error.
  - **Registro de Usuarios**: Este método usa `Mono` para verificar si el correo ya está registrado, y en caso de que no lo esté, guarda de forma reactiva el usuario con las credenciales codificadas. La inserción en MongoDB se maneja de forma no bloqueante, permitiendo una experiencia de registro fluida.
- **DinosaurioService**:

  - **Obtener Dinosaurios por Tipo**: Cada método específico para obtener subtipos de dinosaurios (`CarnivoroTerrestre`, `HerbivoroVolador`, etc.) utiliza `Flux` para filtrar la colección en MongoDB de forma reactiva y concurrente, permitiendo consultas eficientes para cada tipo de dinosaurio.
  - **Simulación de Crecimiento de Dinosaurios**: Este método inicia un flujo `Flux` periódico para incrementar la edad de cada dinosaurio de forma continua y asíncrona. Además, utiliza `lock` y `AtomicBoolean` para sincronizar el crecimiento y verificar la probabilidad de muerte en dinosaurios de mayor edad, permitiendo la reactividad sin bloquear el flujo.
  - **Monitoreo de Salud en Enfermería**: Este flujo reactivo en `Flux.interval` permite monitorear la salud de cada dinosaurio. En caso de detectar signos de enfermedad, el dinosaurio es trasladado a la enfermería mediante `Mono` y se maneja de manera reactiva la interacción con la base de datos para actualizar su posición y estado.
  - **Movimiento de Dinosaurios**: Implementado con `Flux.interval`, este método permite que los dinosaurios se desplacen de forma periódica y reactiva en la isla. El sistema utiliza `AtomicBoolean` para manejar la cancelación de simulaciones activas, garantizando que cada movimiento sea no bloqueante y respetuoso con los recursos.
- **IslaService**:

  - **Agregar Dinosaurio a la Isla**: Utiliza `Mono` para mapear y guardar el dinosaurio en la base de datos de MongoDB. Esto permite que el proceso de asignación de posición en la isla sea completamente reactivo y sin bloqueos, mejorando el rendimiento y manteniendo una experiencia fluida en la aplicación.
  - **Simulación de Movimiento en Isla**: Un flujo reactivo basado en `Flux.interval` permite simular el movimiento continuo de los dinosaurios en la isla. Utiliza `AtomicBoolean` para la cancelación de simulación, evitando sobrecargar el sistema y permitiendo manejar múltiples simulaciones en paralelo de forma controlada.
- **EventoService**:

  - **Creación de Eventos**: A través de `Mono`, este método registra eventos de manera asíncrona, enviando un mensaje a RabbitMQ una vez completado el registro en MongoDB. Esto asegura una experiencia de registro de eventos sin bloqueos y permite el procesamiento continuo de eventos críticos en el sistema.
  - **Enviar Alertas Críticas**: Utiliza `Mono` para generar y enviar alertas de eventos críticos a RabbitMQ. La implementación reactiva permite que los mensajes de alerta sean enviados sin interferir en otros flujos de datos o sobrecargar el sistema.

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

## 1. **Data Transfer Object (DTO)**: Separa la capa de presentación y simplifica la transferencia de datos.

2. **Repository Pattern**: Desacopla la lógica de acceso a datos de la lógica de negocio.
3. **Service Layer**: Organiza la lógica de negocio en servicios dedicados.
4. **Factory Pattern**: Usado en `DinosaurFactory`, `IslaFactory`, y `SensorFactory` para crear instancias de subtipos específicos.
5. **Singleton**: Asegura una única instancia en servicios críticos.
6. **Builder:** Se utiliza para generar un Builder pattern en las clases donde lo aplicamos. Su utilidad radica en crear instancias de objetos complejos con varios campos. Notación`@Builder`

---

# Contribuciones y Licencia

- **Contribuciones**: Abiertas a colaboradores interesados en monitoreo en tiempo real y sistemas reactivos.
- **Licencia**: Detalles de licencia especificando derechos de uso y modificaciones del código.
