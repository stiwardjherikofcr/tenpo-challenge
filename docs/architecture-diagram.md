# Diagrama de Arquitectura (Despliegue)

```mermaid
architecture-beta
    group cloud(cloud)[Entorno de Despliegue]
    
    group container_env(server)[Docker Environment] in cloud
    
    service app(server)[Spring Boot Application] in container_env
    service postgres(database)[PostgreSQL Database] in container_env
    service cache(disk)[Caffeine Cache] in container_env
    
    group external(internet)[Servicios Externos] in cloud
    service percentage_api(internet)[Percentage Service API] in external
    
    app:R --> L:postgres
    app:T --> B:cache
    app:B --> T:percentage_api
```

## Descripción de Componentes

### Spring Boot Application
- **Puerto**: 8080
- **Tecnologías**: Spring Boot, Spring Data JPA, Spring Events
- **Funcionalidades**:
  - API REST para cálculos con porcentajes
  - API REST para consulta de historial
  - Gestión de eventos asíncronos
  - Validación de entrada
  - Manejo de excepciones global

### PostgreSQL Database
- **Puerto**: 5432
- **Esquema**: call_history table
- **Características**:
  - Soporte JSONB para datos flexibles
  - Índices optimizados para consultas
  - Versionado optimista con campo version

### Caffeine Cache
- **Tipo**: In-memory cache
- **Propósito**: Almacenar porcentajes del servicio externo
- **TTL**: Configurable
- **Estrategia**: Fallback cuando el servicio externo falla

### Percentage Service API
- **Tipo**: Mock/External Service
- **Propósito**: Proveer porcentajes dinámicos
- **Resiliencia**: Circuit breaker pattern con cache fallback

## Flujo de Datos

1. Cliente HTTP → Spring Boot Application (POST /api/v1/calculate)
2. Application → Percentage Service API (obtener porcentaje)
3. Application → Caffeine Cache (guardar/recuperar porcentaje)
4. Application → Domain Service (ejecutar cálculo)
5. Application → Event Publisher (publicar evento)
6. Event Listener → PostgreSQL (guardar historial asíncronamente)
7. Application → Cliente HTTP (respuesta JSON)

## Patrones de Arquitectura

- **Hexagonal Architecture (Ports & Adapters)**
- **Domain-Driven Design (DDD)**
- **Event-Driven Architecture**
- **Repository Pattern**
- **Cache-Aside Pattern**
