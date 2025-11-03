# Percentage Calculator Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)

REST API en Spring Boot que suma dos números y aplica un porcentaje dinámico obtenido de un servicio externo, con caché en memoria y registro asíncrono del historial de llamadas.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Diseño de Arquitectura](#-diseño-de-arquitectura)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Uso de la API](#-uso-de-la-api)
- [Testing](#-testing)
- [Documentación](#-documentación)
- [Monitoreo](#-monitoreo)
- [Decisiones de Diseño](#-decisiones-de-diseño)

## ✨ Características

### Funcionalidades Principales

1. **Cálculo con Porcentaje Dinámico**
   - Endpoint que recibe `num1` y `num2`, los suma y aplica un porcentaje
   - Porcentaje obtenido de servicio externo (simulado con mock)
   - Cálculo: `resultado = (num1 + num2) + ((num1 + num2) * porcentaje/100)`

2. **Caché del Porcentaje**
   - Almacenamiento en memoria usando Caffeine Cache
   - TTL de 30 minutos
   - Fallback: si el servicio externo falla, usa el último valor cacheado
   - Si no hay valor cacheado, devuelve error HTTP 503

3. **Historial de Llamadas**
   - Registro asíncrono de todas las llamadas (no bloquea la respuesta)
   - Almacena: fecha, endpoint, parámetros, respuesta/error
   - Endpoint paginado para consultar historial
   - Persistencia en PostgreSQL

### Características Técnicas

- **Arquitectura Hexagonal (Ports & Adapters)**
  - Separación clara entre dominio, aplicación e infraestructura
  - Dominio independiente de frameworks
  - Fácilmente testeable y mantenible

- **Patrones de Resiliencia**
  - Circuit Breaker con Resilience4j
  - Retry con backoff exponencial
  - Cache fallback strategy

- **Event-Driven**
  - Publicación asíncrona de eventos para historial
  - Desacoplamiento entre cálculo y persistencia

- **Observabilidad**
  - Métricas con Micrometer y Prometheus
  - Health checks con Actuator
  - Logging estructurado

## 🏗️ Diseño de Arquitectura

### Diagramas de Arquitectura

Para una comprensión completa del diseño del sistema, consulta los siguientes diagramas detallados en la carpeta `docs/`:

- **[Diagrama de Arquitectura de Despliegue](docs/architecture-diagram.md)**: Muestra la arquitectura de contenedores Docker, componentes del sistema (Spring Boot, PostgreSQL, Caffeine Cache) y las interacciones entre servicios externos.

- **[Diagrama de Clases](docs/class-diagram.md)**: Representa la estructura de clases del sistema siguiendo la arquitectura hexagonal, incluyendo controladores, casos de uso, servicios de dominio, value objects, entidades y adaptadores.

- **[Diagrama de Base de Datos](docs/database-diagram.md)**: Detalla el esquema de la tabla `call_history` con sus campos, tipos de datos, índices (B-Tree y GIN) y optimizaciones para consultas.

### Estructura de Capas

```
percentage-calculator-service
├── domain                          # Capa de dominio (lógica de negocio pura)
│   ├── model                       # Agregados (CallHistory)
│   ├── valueobject                 # Value Objects inmutables
│   ├── port
│   │   ├── in                      # Casos de uso (interfaces)
│   │   └── out                     # Puertos de salida (interfaces)
│   ├── service                     # Servicios de dominio
│   └── exception                   # Excepciones de dominio
│
├── application                     # Capa de aplicación (orquestación)
│   ├── usecase                     # Implementación de casos de uso
│   └── service                     # Servicios de aplicación (eventos)
│
└── infrastructure                  # Capa de infraestructura (adaptadores)
    ├── adapter
    │   ├── in.rest                 # Controladores REST
    │   └── out
    │       ├── persistence         # Adaptador JPA
    │       ├── cache               # Adaptador Caffeine
    │       └── external            # Mock servicio externo
    ├── config                      # Configuraciones Spring
    └── exception                   # Manejador global de excepciones
```

### Flujo de Ejecución

```
[Cliente]
    ↓ HTTP POST /api/v1/calculate
[CalculationController] (REST Adapter)
    ↓ DTO → Domain Request
[CalculateWithPercentageUseCase] (Application)
    ↓
[PercentageResilienceService] (Domain)
    ├─→ [PercentageServicePort] → [MockPercentageServiceAdapter]
    └─→ [CachePort] → [CaffeineCacheAdapter]
    ↓
[CalculationDomainService] (Domain)
    ↓ Resultado
[CallHistoryEventPublisher] (Application) ──async──→ [PostgreSQL]
    ↓
[Cliente] ← HTTP 200 + Resultado
```

## 🛠️ Tecnologías

| Categoría | Tecnología | Versión | Propósito |
|-----------|------------|---------|-----------|
| **Framework** | Spring Boot | 3.5.7 | Framework base |
| **Lenguaje** | Java | 21 | Lenguaje de programación |
| **Base de Datos** | PostgreSQL | 16 | Persistencia de historial |
| **Cache** | Caffeine | - | Caché en memoria |
| **Resiliencia** | Resilience4j | - | Circuit Breaker y Retry |
| **Documentación** | SpringDoc OpenAPI | 2.3.0 | Swagger UI |
| **Monitoreo** | Micrometer + Prometheus | - | Métricas |
| **Migración BD** | Flyway | - | Versionado de esquema |
| **Testing** | JUnit 5 + Mockito + Testcontainers | - | Tests unitarios e integración |
| **Contenedores** | Docker + Docker Compose | - | Despliegue |

## 📦 Requisitos Previos

- **Java 21** o superior
- **Maven 3.9+**
- **Docker** y **Docker Compose** (para ejecución containerizada)
- **PostgreSQL 16** (si se ejecuta localmente sin Docker)

## ⚙️ Configuración

Este proyecto utiliza `@ConfigurationProperties` con validación para una configuración robusta y externa.

### Variables de Entorno Principales

El servicio puede configurarse mediante variables de entorno. Consulta el archivo **[`.env.example`](.env.example)** para ver todas las opciones disponibles.

#### Cache Configuration
```bash
CACHE_NAME=percentageCache              # Nombre del caché
CACHE_EXPIRATION_MINUTES=30             # Expiración en minutos (mínimo 1)
CACHE_MAXIMUM_SIZE=100                  # Tamaño máximo (mínimo 1)
CACHE_RECORD_STATS=true                 # Habilitar estadísticas
```

#### Percentage Service Configuration
```bash
PERCENTAGE_DEFAULT=15.0                 # Porcentaje por defecto (0.0-100.0)
PERCENTAGE_FAILURE_RATE=0.3             # Tasa de fallos simulados (0.0-1.0)
```

#### Database Configuration
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=percentage_db
DB_USER=postgres
DB_PASSWORD=10100101
```

### Archivos de Configuración

- **[`.env.example`](.env.example)**: Plantilla de variables de entorno

### Validación Automática

Todas las propiedades son validadas al inicio de la aplicación:
- **Cache expiration**: Debe ser al menos 1 minuto
- **Default percentage**: Entre 0.0 y 100.0
- **Failure rate**: Entre 0.0 y 1.0

Si alguna validación falla, la aplicación mostrará un mensaje descriptivo y no iniciará.

## 🚀 Instalación y Ejecución

### Opción 1: Ejecución con Docker Compose (Recomendado)

Este método levanta automáticamente PostgreSQL y la aplicación:

```bash
# Construir y levantar servicios
docker-compose up --build

# En segundo plano
docker-compose up -d --build

# Ver logs
docker-compose logs -f app

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

La aplicación estará disponible en: **http://localhost:8080**

### Opción 2: Ejecución Local (Development)

#### 1. Configurar Variables de Entorno (Opcional)

```bash
# Copiar el archivo de ejemplo
cp .env.example .env

# Editar .env con tus configuraciones
# Las variables en .env se cargarán automáticamente
```

#### 2. Levantar PostgreSQL

```bash
docker run -d \
  --name postgres-percentage \
  -e POSTGRES_DB=percentage_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

#### 3. Compilar y Ejecutar la Aplicación

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/percentage-calculator-service-0.0.1-SNAPSHOT.jar

# O directamente con Maven
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

## 📡 Uso de la API

### Endpoints Principales

#### 1. Calcular con Porcentaje

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "num1": 10,
    "num2": 20
  }'
```

**Response (200 OK):**
```json
{
  "result": 34.50,
  "originalSum": 30.00,
  "appliedPercentage": 15.0,
  "timestamp": "2025-01-15T10:30:45"
}
```

**Errores Posibles:**
- `400 Bad Request`: Entrada inválida (num1 o num2 nulos)
- `503 Service Unavailable`: Servicio externo fallido y sin caché

#### 2. Consultar Historial

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/history?page=0&size=10&sortBy=timestamp&sortDirection=DESC"
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "timestamp": "2025-01-15T10:30:45",
      "endpoint": "/api/v1/calculate",
      "method": "POST",
      "requestParams": "{\"num1\":10,\"num2\":20}",
      "response": "{\"result\":34.50}",
      "errorMessage": null,
      "success": true
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**Parámetros de Query:**
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 10)
- `sortBy`: Campo de ordenamiento (default: timestamp)
- `sortDirection`: ASC o DESC (default: DESC)

#### 3. Health Check

```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

### Ejemplos con HTTPie

```bash
# Cálculo básico
http POST :8080/api/v1/calculate num1:=10 num2:=20

# Con decimales
http POST :8080/api/v1/calculate num1:=15.75 num2:=24.25

# Números negativos (válido)
http POST :8080/api/v1/calculate num1:=-10 num2:=30

# Historial
http GET :8080/api/v1/history page==0 size==5
```

## 🧪 Testing

### Ejecutar Todos los Tests

```bash
# Tests unitarios e integración
mvn test

# Solo tests unitarios (sin Testcontainers)
mvn test -Dtest=*Test

# Con cobertura
mvn test jacoco:report
```

### Tipos de Tests Implementados

1. **Tests Unitarios de Dominio**
   - `CalculationDomainServiceTest`: Lógica de cálculo pura
   - `PercentageResilienceServiceTest`: Estrategia de fallback
   - `PercentageTest`: Validación de Value Objects

2. **Tests de Casos de Uso**
   - `CalculateWithPercentageUseCaseTest`: Orquestación con mocks
   - Verificación de publicación de eventos
   - Verificación de métricas

3. **Tests de Integración**
   - `CalculationControllerIntegrationTest`: Tests end-to-end
   - `CallHistoryJpaAdapterIntegrationTest`: Persistencia real
   - Usan Testcontainers para PostgreSQL

### Cobertura Esperada

- **Dominio**: 100%
- **Aplicación**: 100%
- **Infraestructura**: 80%+

### Ejecutar un Test Específico

```bash
mvn test -Dtest=CalculationDomainServiceTest
```

## 📚 Documentación

### Swagger UI

Una vez levantada la aplicación, acceder a:

**http://localhost:8080/swagger-ui.html**

Permite:
- Explorar todos los endpoints
- Probar requests interactivamente
- Ver esquemas de DTOs

### OpenAPI JSON

**http://localhost:8080/api-docs**

Especificación OpenAPI 3.0 en formato JSON.

## 📊 Monitoreo

### Actuator Endpoints

| Endpoint | Descripción |
|----------|-------------|
| `/actuator/health` | Estado de salud |
| `/actuator/metrics` | Métricas disponibles |
| `/actuator/prometheus` | Métricas en formato Prometheus |
| `/actuator/info` | Información de la app |

### Métricas Disponibles

```bash
# Métricas de cálculo
curl http://localhost:8080/actuator/metrics/calculation.success
curl http://localhost:8080/actuator/metrics/calculation.failure

# Métricas de JVM
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Métricas de base de datos
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```

### Distributed Tracing con Zipkin

El servicio implementa distributed tracing usando **Micrometer Tracing** y **Zipkin** para rastrear requests completos desde el HTTP hasta eventos asíncronos y **queries de base de datos**.

#### Componentes de Tracing

1. **HTTP Tracing**: Requests y responses REST
2. **Async Tracing**: Eventos asíncronos (@Async)
3. **JDBC Tracing**: Queries SQL a PostgreSQL (SELECT, INSERT, etc.)
4. **Custom Spans**: Operaciones de negocio

#### Iniciar Zipkin

**Opción 1: Docker Compose (recomendado)**
```bash
docker-compose up -d zipkin
```

**Opción 2: Zipkin standalone**
```bash
docker run -d -p 9411:9411 openzipkin/zipkin:latest
```

#### Acceder a Zipkin UI

Una vez iniciado Zipkin, acceder a:

**http://localhost:9411**

#### Ver Trazas

1. **Buscar trazas**: En Zipkin UI, click en "Run Query"
2. **Filtrar por servicio**: Seleccionar `percentage-calculator-service`
3. **Ver detalle**: Click en una traza para ver el flujo completo:
   - **HTTP request** (CalculationController) - ~245ms
   - **Use case execution** (CalculateWithPercentageUseCase) - ~180ms
   - **JDBC queries** (SELECT/INSERT) - ~15ms cada una
   - **Async event handling** (CallHistoryEventListener) - ~50ms
   - **Database operations** (CallHistoryJpaAdapter) - Spans individuales por query

#### JDBC Tracing (Queries SQL Visibles)

Con la integración de **datasource-micrometer-spring-boot**, cada query SQL aparece como un span en Zipkin:

**Ejemplo de spans visibles:**
```
├─ POST /api/v1/calculate (245ms)
│  ├─ CalculateWithPercentageUseCase (180ms)
│  │  └─ getPercentageWithFallback (120ms)
│  └─ CallHistoryEventListener (50ms)
│     └─ CallHistoryJpaAdapter.save (15ms)
│        ├─ SELECT nextval('call_history_seq') (2ms)
│        └─ INSERT INTO call_history (...) (13ms)
```

**Beneficios:**
- ✅ Ver texto completo de cada query SQL
- ✅ Identificar queries lentas (N+1 problem)
- ✅ Medir latencia de operaciones de DB
- ✅ Ver parámetros de queries (dev only)

#### Trace IDs en Logs

Todos los logs incluyen `traceId` y `spanId` para correlación:

```bash
2025-10-31 10:30:45 [507f1f77bcf86cd799439011/abc123def456] - Starting calculation for request: CalculationRequest{num1=10, num2=20}
2025-10-31 10:30:45 [507f1f77bcf86cd799439011/def789ghi012] - Executing: SELECT nextval('call_history_seq')
2025-10-31 10:30:45 [507f1f77bcf86cd799439011/xyz789ghi012] - Call history saved successfully
```

**Buscar logs por trace ID:**
```bash
grep "507f1f77bcf86cd799439011" logs/application.log
```

#### Configuración por Ambiente

**Development** (`application-dev.yml`):
- Sampling: 100% (todas las requests)
- JDBC Tracing: Habilitado con parámetros
- Endpoint: `http://localhost:9411`

**Production** (`application-prod.yml`):
- Sampling: 10% (optimizado para rendimiento)
- JDBC Tracing: Habilitado sin parámetros (seguridad)
- Endpoint: `http://zipkin:9411`

**Cambiar sampling rate:**
```yaml
management:
  tracing:
    sampling:
      probability: 0.5  # 50% de las requests
```

#### Ejemplos de Uso

**Ejemplo 1: Rastrear request específico**
```bash
# Hacer request y obtener trace ID del header
curl -v http://localhost:8080/api/v1/calculate \
  -H "Content-Type: application/json" \
  -d '{"num1": 10, "num2": 20}'

# Buscar en Zipkin UI por el traceId del header X-B3-TraceId
```

**Ejemplo 2: Analizar latencia**
En Zipkin UI, cada span muestra:
- Duración total del request
- Tiempo en cada componente (controller → use case → repository)
- Identificar cuellos de botella

**Ejemplo 3: Debug errores**
```bash
# En Zipkin UI, filtrar por:
# - Error tag: "error=true"
# - Buscar spans con exceptions
# - Ver stack trace completo
```

#### Beneficios del Distributed Tracing

- ✅ **Observabilidad completa**: Ver flujo end-to-end de cada request
- ✅ **Correlación de logs**: Trace IDs en todos los logs relacionados
- ✅ **Análisis de latencia**: Identificar componentes lentos
- ✅ **Debug asíncrono**: Rastrear eventos a través de boundaries
- ✅ **Producción-ready**: Sampling configurable para bajo overhead

### Prometheus Integration

Agregar a `prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'percentage-calculator'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

## 🎯 Decisiones de Diseño

### ¿Por qué Hexagonal Architecture?

- **Testabilidad**: Dominio 100% testeable sin Spring
- **Independencia**: Fácil cambiar PostgreSQL por MongoDB
- **Claridad**: Separación explícita de responsabilidades

### ¿Por qué Caffeine sobre Redis?

- **Simplicidad**: No requiere infraestructura adicional
- **Performance**: Más rápido para caché local
- **Escalabilidad**: Fácil migrar a Redis después

### ¿Por qué Async para Historial?

- **Performance**: No bloquea la respuesta del cálculo
- **Resiliencia**: Fallos en BD no afectan al cliente
- **Desacoplamiento**: Calculation no conoce History

### ¿Por qué Mock del Servicio Externo?

- **Testing**: Simula fallos para probar resiliencia
- **Configuración**: `failure-rate: 0.3` (30% de fallos)
- **Producción**: Reemplazar con HTTP client real

### Configuración del Mock

En `application.yml`:

```yaml
percentage:
  service:
    mock:
      enabled: true
      default-percentage: 15.0
      failure-rate: 0.3  # 30% de fallos
```

## 🔧 Configuración Avanzada

### Usando ConfigurationProperties

Este proyecto utiliza `@ConfigurationProperties` para una configuración type-safe con validación automática.

**Clases de configuración:**
- `CacheProperties`: Configuración del caché Caffeine
- `PercentageServiceProperties`: Configuración del servicio de porcentajes

**Ejemplo de uso:**
```java
@Component
public class MyService {
    private final CacheProperties cacheProperties;
    
    public MyService(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }
    
    public void doSomething() {
        int expirationMinutes = cacheProperties.getExpirationMinutes();
        // ...
    }
}
```

### Variables de Entorno por Perfil

**Development:**
```bash
export SPRING_PROFILES_ACTIVE=dev
export CACHE_EXPIRATION_MINUTES=30
export PERCENTAGE_FAILURE_RATE=0.3  # 30% fallos para testing
```

**Production:**
```bash
export SPRING_PROFILES_ACTIVE=prod
export CACHE_EXPIRATION_MINUTES=60
export CACHE_MAXIMUM_SIZE=1000
export PERCENTAGE_FAILURE_RATE=0.0  # Sin simulación de fallos
```

### Verificar Configuración Cargada

```bash
# Ver todas las propiedades de configuración
curl http://localhost:8080/actuator/configprops

# Ver solo cache properties
curl http://localhost:8080/actuator/configprops | jq '.contexts.application.beans.cacheProperties'
```

### Profiles de Spring

```bash
# Development
java -jar app.jar --spring.profiles.active=dev

# Production
java -jar app.jar --spring.profiles.active=prod

# Docker
java -jar app.jar --spring.profiles.active=docker
```

## 🐛 Troubleshooting

### La aplicación no inicia

```bash
# Verificar que PostgreSQL esté corriendo
docker ps | grep postgres

# Ver logs de la aplicación
docker-compose logs app

# Verificar conectividad a BD
docker exec -it postgres-percentage psql -U postgres -d percentage_db
```

### Tests fallan

```bash
# Limpiar y reconstruir
mvn clean install

# Verificar Docker para Testcontainers
docker ps

# Ejecutar con más logs
mvn test -X
```

### Cache no funciona

```bash
# Verificar configuración en application.yml
# Ver métricas de cache
curl http://localhost:8080/actuator/metrics/cache.gets
```

## 📄 Licencia

Este proyecto fue desarrollado como parte del Challenge Backend de Tenpo.

## 👤 Autor

Desarrollado por Stiwart Jherikof Carrillo Ramirez

GitHub: [StiwartJherikof](https://github.com/stiwardjherikofcr)
LinkedIn: [Stiwart Jherikof Carrillo Ramirez](https://www.linkedin.com/in/stiward-jherikof-carrillo-ram%C3%ADrez-10b6b31a4/)

---
