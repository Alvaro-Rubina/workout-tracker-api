# Workout Tracker Project

Aplicación backend en Java/Spring Boot para gestionar rutinas de entrenamiento: ejercicios, sesiones, agendas, progreso de peso, comentarios y más. Incluye autenticación y autorización con Auth0 (JWT), almacenamiento de imágenes con Cloudinary y persistencia en MySQL.

---

## Tecnologías utilizadas
- Java 21
- Spring Boot 3.5.4 (Web, Data JPA, Validation, Security + OAuth2 Resource Server)
- MySQL 8
- MapStruct 1.6.3
- Lombok
- Cloudinary SDK
- Auth0 Java SDK
- Gradle
- Docker

---

## Características principales
- Gestión de usuarios (perfil, avatar en Cloudinary, activación, roles)
- Gestión de rutinas (creación, edición, publicación, likes/guardados, completar rutina)
- Gestión de sesiones y ejercicios (relaciones, consultas simplificadas y detalladas)
- Agenda de entrenamientos
- Seguimiento de peso
- Interacción social en rutinas públicas (comentar, dar like, guardar)
- Catálogo maestro: categorías, músculos, zonas musculares, equipamiento
- Seguridad basada en JWT (Auth0), control de acceso por roles: `USUARIO`, `ADMIN`, `PROPIETARIO`
- Inicialización automática de datos mínimos: categoría por defecto y roles (DB + Auth0)

---

## Arquitectura
- Capa de controlador (`controller`): expone endpoints REST bajo el contexto `/api`
- Capa de servicio (`service` y `service.auth0`): lógica de negocio y adaptación con Auth0/Cloudinary
- Capa de persistencia (`entity`, `repository`): entidades JPA y repositorios
- DTOs y Mappers (`persistence/dto`, `mapper`): transporte y mapeo con MapStruct
- Configuración (`config`): seguridad (Auth0/JWT), Cloudinary, CORS, carga de datos iniciales

Context path: todos los endpoints están bajo `/api` (configurado en `application.properties`).

---

## Estructura del proyecto
```
workout-tracker-project/
├─ build.gradle
├─ docker-compose.yml            # MySQL 8 con puerto expuesto 3307
├─ src/main/java/org/alvarub/workouttrackerproject/
│  ├─ WorkoutTrackerProjectApplication.java
│  ├─ config/
│  │  ├─ cloudinary/CloudinaryConfig.java
│  │  ├─ dataloader/StartupDataInitializer.java
│  │  └─ security/{SecurityConfig, AudienceValidator, Auth0Config}.java
│  ├─ controller/                # Agenda, Categoria, Comentario, Ejercicio, Equipamiento, Musculo,
│  │                             # Peso, Rol, Rutina, Sesion, SesionCompletada, Usuario, ZonaMuscular
│  ├─ exception/                 # Manejo global de errores
│  ├─ mapper/                    # MapStruct mappers
│  ├─ persistence/
│  │  ├─ dto/                    # DTOs de request/response
│  │  ├─ entity/                 # Entidades JPA
│  │  └─ repository/             # Repositorios Spring Data JPA
│  ├─ service/                   # Servicios de dominio, Auth0 y Cloudinary
│  └─ utils/Constants.java       # Nombres de roles y valores por defecto
└─ src/main/resources/
   ├─ application.properties
   └─ logback-spring.xml
```

---

## Requisitos previos
- Java 21 (JDK)
- Gradle (o usar `./gradlew` incluido)
- Docker + Docker Compose (para lanzar MySQL)
- Cuenta y aplicación en Auth0 (dominio, audiencia, issuer, etc.)
- Cuenta en Cloudinary (cloud name, API key/secret)

---

## Configuración de entorno
El proyecto usa variables externas para credenciales y orígenes CORS. Se pueden definir como variables de entorno del sistema o en un fichero `.env` y exportarlas antes de ejecutar.

Variables necesarias
- `AUTH0_DOMAIN`
- `AUTH0_CLIENT_ID`
- `AUTH0_CLIENT_SECRET`
- `AUTH0_AUDIENCE`
- `AUTH0_ISSUER_URI`
- `WEB_CORS_ALLOWED_ORIGINS` (ej. `http://localhost:5173,http://localhost:4200`)
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

Configuración de base de datos por defecto:
- URL: `jdbc:mysql://localhost:3307/workout-tracker-db` (mapeado en Docker)
- Usuario: `user`
- Password: `1234`
- DDL auto: `update`

Ejemplo de `.env` (a modo ilustrativo):
```
AUTH0_DOMAIN=ejemplo-de-dominio.auth0.com
AUTH0_CLIENT_ID=...
AUTH0_CLIENT_SECRET=...
AUTH0_AUDIENCE=https://api.ejemplo-workout-tracker
AUTH0_ISSUER_URI=https://ejemplo-de-dominio.auth0.com/
WEB_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:4200
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
```

---

## Puesta en marcha local
1) Levantar MySQL con Docker
```
docker compose up -d
```
El `docker-compose.yml` levanta MySQL 8 en `localhost:3307` con:
- DB: `workout-tracker-db`
- Usuario: `user`
- Password: `1234`

2) Exportar variables de entorno (según SO)
- Windows PowerShell: `setx NOMBRE_VALOR "valor"` (reinicia la terminal) o usa un gestor de entornos.
- Alternativa: ejecutar el IDE con variables configuradas en la run configuration.

3) Ejecutar la aplicación
- Con Gradle wrapper:
```
./gradlew bootRun   # Linux/Mac
./gradlew.bat bootRun  # Windows
```
- O empaquetar y ejecutar:
```
./gradlew clean build
java -jar build/libs/workout-tracker-project-0.0.1-SNAPSHOT.jar
```

4) URL base de la API
- `http://localhost:8080/api`

---

## Seguridad y roles
- Autenticación: Resource Server con JWT emitidos por Auth0.
- Autorización: basada en roles (`USUARIO`, `ADMIN`, `PROPIETARIO`).
- El claim de roles esperado está en `"<audience>/roles"` (ver `SecurityConfig.jwtAuthenticationConverter`).
- Rutas públicas: `/*/public/**` y `/public/**`.
- Rutas admin: `/*/admin/**` y `/admin/**` (requiere `ADMIN` o `PROPIETARIO`).
- Alta de administradores: `/admin/signup` (requiere `PROPIETARIO`).
- El resto de endpoints requieren autenticación.

CORS: los orígenes permitidos se definen en `WEB_CORS_ALLOWED_ORIGINS` (separados por comas).

Inicialización de datos de seguridad:
- `StartupDataInitializer` crea/valida:
  - Categoría por defecto: `General`.
  - Roles por defecto en DB y Auth0 a partir de `Constants.ROLES`: `PROPIETARIO`, `ADMIN`, `USUARIO`.

---

## Endpoints principales
Prefijo global: `/api`

### Controladores y áreas funcionales
- UsuarioController (`/users`): perfil del usuario autenticado, registro interno, administración de usuarios y estadísticas; actualización de perfil y gestión de estado activo; alta de administradores.
- RutinaController (`/routines`): CRUD de rutinas, publicar/privatizar, likes/guardados, listados (usuario, público, admin) con modo simple/detallado.
- SesionController (`/sessions`): consulta de sesiones individuales (visibilidad por usuario) con modo simple/detallado.
- ComentarioController (`/comments`): crear, listar por usuario/rutina, likes, edición de contenido y borrado (propio o admin).
- AgendaController (`/schedules`): crear, consultar, actualizar y eliminar agendas del usuario; listado de agendas del usuario.
- PesoController (`/body-weights`): registrar, consultar (último y pesos historicos) y actualizar el último peso corporal del usuario.
- CategoriaController (`/categories`): gestión de categorías (crear, listar, obtener, actualizar, activar/desactivar, eliminar).
- EjercicioController (`/exercises`): gestión de ejercicios (crear, listar, obtener, actualizar, activar/desactivar) con respuestas simple/detallada.
- EquipamientoController (`/equipment`): gestión de equipamiento con soporte de imagen (crear/actualizar), listar/obtener, activar/desactivar y eliminar.
- MusculoController (`/muscles`): gestión de músculos (crear/actualizar), listar/obtener, activar/desactivar y eliminación de imagen.
- ZonaMuscularController (`/muscle-groups`): gestión de grupos musculares con imagen (crear/actualizar multipart), listar/obtener, activar/desactivar y eliminar.
- RolController (`/roles/admin`): administración de roles (crear, obtener por id/nombre, actualizar, listar).

Nota: muchos listados aceptan parámetro `relations` para alternar respuesta simple/detallada. Revisar los DTOs en `persistence/dto/*` para estructuras de entrada/salida.

---





## Roadmap
- Paginación y filtrado avanzado en listados públicos
- Rate limiting y cache selectivo
- Documentación OpenAPI/Swagger
- Contenerización completa del backend

---

