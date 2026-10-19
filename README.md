# LogiSync — Sistema de Gestión de Cadena de Suministro (SCM)

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%28Ports%20%26%20Adapters%29-blue.svg)](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
[![Design Patterns](https://img.shields.io/badge/GoF%20Patterns-19%20Applied-purple.svg)](docs/PATTERNS.md)
[![Build Status](https://img.shields.io/badge/Tests-79%2F79%20Passing-success.svg)]()

**LogiSync** es un backend empresarial de alto rendimiento para la gestión integral de cadenas de suministro (Supply Chain Management - SCM). Desarrollado para la materia *Patrones de Diseño de Software*, el sistema implementa una **Arquitectura Hexagonal limpia**, sigue estrictamente los principios **SOLID** y aplica **19 patrones de diseño Gang of Four (GoF)** (Creacionales, Estructurales y de Comportamiento) que resuelven problemas reales del dominio logístico.

---

## Características y Módulos del Sistema (100% Alcance)

1. **Gestión de Proveedores y Aprovisionamiento:**
   - Registro y evaluación de proveedores con calificación de fiabilidad.
   - Generación de necesidades de abastecimiento (`SupplyNeed`) por punto de reorden o pronóstico.
   - Flujo de ciclo de vida de Órdenes de Compra (`PurchaseOrder`) gobernado por **State Pattern** y **Mediator Pattern**.
   - Matriz de aprobación de compras con **Chain of Responsibility**.

2. **Catálogo de Productos y Categorías:**
   - Catálogo jerárquico de productos con SKU único, códigos de barras, pesos, volúmenes y condiciones de almacenamiento.
   - Búsqueda avanzada y filtrado dinámico mediante JPA Specifications.

3. **Gestión de Almacenes y Jerarquía Espacial:**
   - Estructuración multinivel de almacenes (Zonas, Pasillos, Racks, Bahías) modelada con **Composite Pattern**.
   - Recorrido desacoplado mediante **Iterator Pattern** y auditorías volumétricas con **Visitor Pattern**.
   - Duplicación ágil de bahías de almacenamiento con **Prototype Pattern**.

4. **Inventario y Control de Stock en Tiempo Real:**
   - Movimientos atómicos de entrada (*Inbound*), salida (*Outbound*), transferencia y ajuste mediante **Command Pattern** y **Template Method Pattern**.
   - Control de concurrencia optimista (`@Version`) para evitar lecturas sucias y sobreventas.
   - Consumo eficiente de unidades de medida con **Flyweight Pattern**.
   - Alertas automáticas de stock mínimo y auditoría mediante **Observer Pattern**.

5. **Órdenes de Venta y Despachos:**
   - Pipeline de validación de pedidos de clientes (límite de crédito, disponibilidad de stock, aprobación gerencial) con **Chain of Responsibility**.
   - Máquina de estados de pedidos (`OrderState`) y cálculo de precios/recargos con **Decorator Pattern**.
   - Orquestación y reserva de inventario para despachos con `DispatchFacade` y **State Pattern**.

6. **Flotas, Vehículos y Enrutamiento Inteligente:**
   - Registro de flota vehicular con autenticación segura por API Key (SHA-256).
   - Optimización de rutas de reparto basada en **Strategy Pattern** (Distancia mínima Haversine, Tiempo rápido, Balanceado).
   - Servicio de cálculo de rutas acelerado por caché con **Proxy Pattern**.

7. **Telemetría IoT y Rastreo en Tiempo Real:**
   - Ingesta de telemetría GPS, velocidad y estado vehicular con validación encadenada.
   - Transmisión en tiempo real a clientes web/móviles mediante Server-Sent Events (SSE: `GET /api/tracking/stream`).
   - Generación de rutas históricas en formato GeoJSON (`LineString`).
   - Desacoplamiento de alertas críticas vs. estándar con **Bridge Pattern** y exportación de métricas con **Adapter Pattern**.

8. **Pronóstico Inteligente de Demanda:**
   - Algoritmos predictivos de demanda basados en **Strategy Pattern** (Promedio Móvil, Suavizado Exponencial, Regresión Lineal).
   - Ajustes dinámicos de estacionalidad e inventario de seguridad mediante **Decorator Pattern**.
   - Generación automática de órdenes de reabastecimiento basada en pronósticos.

9. **Optimización de Distribución en Almacén (Layout ABC):**
   - Análisis de rotación ABC de productos y cubicaje ejecutado mediante **Visitor Pattern** (`RelocationProposalVisitor`).
   - Captura de snapshots inmutables del almacén y rollback histórico ante errores operativos con **Memento Pattern**.

10. **Métricas, Observabilidad y Actuator:**
    - Métricas operativas en tiempo real expuestas en `/actuator/prometheus` mediante `micrometer-registry-prometheus`.
    - Health checks y metadatos del sistema en `/actuator/health` e `/actuator/info`.

---

## Arquitectura del Software

El sistema sigue la **Arquitectura Hexagonal (Puertos y Adaptadores)**, garantizando que la lógica de negocio y las entidades de dominio sean 100% independientes de frameworks, bases de datos o interfaces web.

```
com.logisync.sistema_cadena_suministro
│
├── domain                        <-- Núcleo de Dominio (Puro Java, sin dependencias externas)
│   ├── models                    <-- Agregados, Entidades y Objetos de Valor
│   ├── ports
│   │   ├── in                    <-- Puertos de Entrada (Casos de Uso / Interfaces de Servicios)
│   │   └── out                   <-- Puertos de Salida (Contratos de Repositorios, Notificaciones)
│   ├── factory                   <-- Fábricas Abstractas (Abstract Factory)
│   ├── commands                  <-- Comandos de Movimiento (Command Pattern)
│   ├── strategy                  <-- Estrategias de Rutas y Demanda (Strategy Pattern)
│   ├── visitor                   <-- Visitantes de Almacén (Visitor Pattern)
│   ├── memento                   <-- Mementos y Originador de Layout (Memento Pattern)
│   └── pricing                   <-- Decoradores de Tarifación (Decorator Pattern)
│
├── application                   <-- Capa de Aplicación (Orquestación y Servicios de Casos de Uso)
│   ├── services                  <-- Implementaciones de casos de uso (Products, Orders, Inventory...)
│   └── facade                    <-- Fachadas de alto nivel (Facade Pattern)
│
└── infrastructure                <-- Adaptadores de Infraestructura
    ├── persistence               <-- Adaptadores JPA / Hibernate, Entidades SQL y Repositorios
    ├── rest                      <-- Controladores Spring MVC REST (@RestController)
    ├── security                  <-- Spring Security, Filtros JWT (JJWT) y Hashers
    ├── bridge                    <-- Implementación Bridge y Adaptador Micrometer/Prometheus
    └── proxy                     <-- Proxies de enrutamiento con caché (Proxy Pattern)
```

---

## Documentación de Patrones GoF

Para consultar la explicación detallada de cada uno de los 19 patrones aplicados (diagramas UML, clases participantes, problema resuelto y cómo probarlos):

- 📄 [Catálogo Completo de Patrones GoF (Markdown)](docs/PATTERNS.md)
- 📘 [Guía de Patrones Creacionales y Estructurales (Word DOCX)](docs/Guia_Patrones_Creacionales_y_Estructurales_LogiSync.docx)

---

## Requisitos Previos

- **Java Development Kit (JDK):** Versión 21 o superior.
- **Apache Maven:** 3.9+ (o utilizar el wrapper incluido).
- **Base de Datos:** PostgreSQL 15+ (producción) o H2 In-Memory (desarrollo y pruebas locales automáticas).

---

## Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone https://github.com/Julian20Quiroga/sistema-gestion-cadena-suministro.git
cd sistema-gestion-cadena-suministro
```

### 2. Compilar el proyecto y ejecutar las pruebas
Para verificar la compilación y pasar los 79 tests de integración y arquitectura:
```bash
cd backend/sistema_cadena_suministro
mvn clean verify
```

### 3. Iniciar la aplicación en modo desarrollo
```bash
mvn spring-boot:run
```
La aplicación iniciará en `http://localhost:8080`.

---

## Endpoints Principales

| Módulo | Método | Endpoint | Descripción |
|---|---|---|---|
| **Auth** | `POST` | `/api/auth/login` | Autenticación y obtención de JWT Bearer Token |
| **Auth** | `POST` | `/api/auth/register` | Registro de usuarios en la plataforma |
| **Products** | `GET` | `/api/products` | Catálogo de productos con paginación y filtros |
| **Warehouses** | `GET` | `/api/warehouses/{id}/tree` | Estructura jerárquica en árbol (Composite) |
| **Inventory** | `POST` | `/api/inventory/movements` | Ejecución de movimientos de stock (Command) |
| **Orders** | `POST` | `/api/orders` | Creación de pedidos de clientes |
| **Dispatch** | `POST` | `/api/dispatches` | Creación y despacho de mercancías (Facade) |
| **Routes** | `POST` | `/api/routes/optimize` | Optimización de rutas vehiculares (Strategy) |
| **IoT Telemetry**| `POST` | `/api/tracking/telemetry` | Ingesta de coordenadas y velocidad de vehículo |
| **IoT Stream** | `GET` | `/api/tracking/stream` | Canal en vivo Server-Sent Events (SSE) |
| **Forecast** | `POST` | `/api/demand/forecast` | Cálculo estadístico de previsión de demanda |
| **Layout Opt.** | `POST` | `/api/warehouses/{id}/optimize` | Análisis ABC y sugerencias de reubicación |
| **Memento** | `POST` | `/api/warehouses/{id}/snapshots` | Captura de instantánea de distribución física |
| **Memento** | `POST` | `/api/warehouses/{id}/rollback` | Restauración a un snapshot previo |
| **Actuator** | `GET` | `/actuator/prometheus` | Métricas operativas para Prometheus y Grafana |
| **Health** | `GET` | `/actuator/health` | Estado de salud del servicio |
| **Swagger UI** | `GET` | `/swagger-ui.html` | Documentación interactiva de la API OpenAPI 3 |

---

## Autenticación

Todas las rutas bajo `/api/**` (excepto `/api/auth/**`, `/api/tracking/stream` y `/actuator/**`) requieren autenticación mediante encabezado HTTP:

```http
Authorization: Bearer <tu_token_jwt>
```

Los vehículos IoT envían su API Key en el encabezado:
```http
X-Vehicle-Api-Key: <api_key_del_vehiculo>
```

---

## Licencia

Este proyecto fue desarrollado con fines académicos para la asignatura de **Patrones de Diseño de Software** (Universidad / Ingeniería de Sistemas). Todos los derechos reservados.
