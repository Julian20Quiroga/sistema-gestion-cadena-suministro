# Guía Práctica de Uso y Pruebas — LogiSync SCM

Esta guía explica paso a paso cómo probar y utilizar cada una de las funcionalidades implementadas en el backend de **LogiSync**, utilizando herramientas estándar como **cURL**, **Postman** o la consola de comandos de **PowerShell**.

---

## Índice de Contenidos

1. [Configuración Inicial y Autenticación](#1-configuración-inicial-y-autenticación)
2. [Gestión de Proveedores](#2-gestión-de-proveedores)
3. [Catálogo de Productos y Categorías](#3-catálogo-de-productos-y-categorías)
4. [Almacenes y Espacios de Almacenamiento (Composite y Prototipado)](#4-almacenes-y-espacios-de-almacenamiento)
5. [Movimientos de Inventario y Control de Stock (Command y Observer)](#5-movimientos-de-inventario-y-control-de-stock)
6. [Aprovisionamiento y Órdenes de Compra (State, Mediator y Chain of Resp.)](#6-aprovisionamiento-y-órdenes-de-compra)
7. [Pedidos de Clientes y Tarifación Dinámica (Decorator y State)](#7-pedidos-de-clientes-y-tarifación-dinámica)
8. [Despachos y Entrega de Mercancía (Facade y State)](#8-despachos-y-entrega-de-mercancía)
9. [Flotas, Enrutamiento Óptimo y Proxy de Caché](#9-flotas-enrutamiento-óptimo-y-proxy-de-caché)
10. [Telemetría IoT en Vivo y Stream SSE (Bridge y Chain of Resp.)](#10-telemetría-iot-en-vivo-y-stream-sse)
11. [Pronóstico de Demanda (Strategy y Decorator)](#11-pronóstico-de-demanda)
12. [Optimización de Almacén (Visitor) y Snapshots/Rollback (Memento)](#12-optimización-de-almacén-y-snapshotsrollback)
13. [Métricas de Prometheus y Salud del Sistema (Actuator)](#13-métricas-de-prometheus-y-salud-del-sistema)
14. [Ejecución Automatizada de Pruebas Unitarias y de Integración](#14-ejecución-automatizada-de-pruebas)

---

## 1. Configuración Inicial y Autenticación

Todas las rutas operativas del sistema (a excepción de login, registro, el stream SSE público y actuator) requieren el encabezado HTTP:
```http
Authorization: Bearer <TU_TOKEN_JWT>
```

### 1.1 Registrar un Usuario Administrador / Operador
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/auth/register`
- **Body JSON:**
```json
{
  "username": "admin_logisync",
  "email": "admin@logisync.com",
  "password": "Password123!",
  "fullName": "Administrador Principal",
  "role": "ADMIN"
}
```

**Comando cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin_logisync","email":"admin@logisync.com","password":"Password123!","fullName":"Administrador Principal","role":"ADMIN"}'
```

---

### 1.2 Iniciar Sesión para Obtener el Token JWT
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/auth/login`
- **Body JSON:**
```json
{
  "username": "admin_logisync",
  "password": "Password123!"
}
```

**Respuesta Esperada (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin_logisync",
  "role": "ADMIN"
}
```
> **Nota:** Guarda el valor del `"token"` para incluirlo en las siguientes peticiones como variable `TOKEN`.

---

## 2. Gestión de Proveedores

Permite registrar y administrar las empresas que abastecen a la cadena de suministro.

### 2.1 Crear un Proveedor
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/suppliers`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "name": "Distribuciones Andinas S.A.S.",
  "contactEmail": "contacto@andinas.com",
  "contactPhone": "+57 300 123 4567",
  "reliabilityScore": 9.5,
  "leadTimeDays": 3
}
```

### 2.2 Listar Proveedores
- **Método:** `GET`
- **URL:** `http://localhost:8080/api/suppliers?page=1&size=10`
- **Headers:** `Authorization: Bearer <TOKEN>`

---

## 3. Catálogo de Productos y Categorías

### 3.1 Crear una Categoría
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/categories`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "name": "Electrónicos y Periféricos",
  "description": "Componentes de computación y accesorios"
}
```

### 3.2 Crear un Producto con SKU Único
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/products`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "sku": "PROD-ELEC-001",
  "name": "Sensor IoT GPS Industrial",
  "categoryId": 1,
  "price": 120.50,
  "weightKg": 0.45,
  "volumeM3": 0.002,
  "reorderPoint": 15,
  "unitOfMeasure": "UNIDAD"
}
```

### 3.3 Buscar Productos con Filtros Dinámicos (JPA Specification)
- **Método:** `GET`
- **URL:** `http://localhost:8080/api/products?search=Sensor&categoryId=1&page=1&size=10`
- **Headers:** `Authorization: Bearer <TOKEN>`

---

## 4. Almacenes y Espacios de Almacenamiento

Implementa el **Composite Pattern** para jerarquías y **Prototype Pattern** para clonar bahías.

### 4.1 Crear un Almacén
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/warehouses`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "name": "Centro de Distribución Norte",
  "address": "Calle 80 # 45-12, Bogotá",
  "phone": "+57 601 888 9999"
}
```

### 4.2 Crear Espacios de Almacenamiento Jerárquicos
- **Zona (Nivel 1):**
  ```bash
  curl -X POST http://localhost:8080/api/storage-spaces \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name":"Zona A - Picking Rápido","warehouseId":1,"type":"ZONE","parentId":null}'
  ```
- **Rack (Nivel 2, dentro de Zona A con ID 1):**
  ```bash
  curl -X POST http://localhost:8080/api/storage-spaces \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name":"Rack 01","warehouseId":1,"type":"RACK","parentId":1}'
  ```
- **Ubicación/Bahía (Nivel 3, dentro de Rack 01 con ID 2):**
  ```bash
  curl -X POST http://localhost:8080/api/storage-spaces \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name":"Bahía 101","warehouseId":1,"type":"LOCATION","capacity":500,"parentId":2}'
  ```

### 4.3 Consultar la Estructura en Árbol del Almacén (Composite)
- **Método:** `GET`
- **URL:** `http://localhost:8080/api/warehouses/1/tree`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Respuesta:** Retorna el árbol anidado con cálculo recursivo de volumen y capacidad disponible.

---

## 5. Movimientos de Inventario y Control de Stock

Implementa **Command Pattern** (encapsula acciones de bodega), **Template Method** (validaciones estándar) y **Observer** (disparo de alertas si cae bajo el punto de reorden).

### 5.1 Entrada de Mercancía (*Inbound*)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/inventory/movements`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "movementType": "INBOUND",
  "productId": 1,
  "warehouseId": 1,
  "targetSpaceId": 3,
  "quantity": 100,
  "unitOfMeasure": "UNIDAD",
  "notes": "Recepción de lote inicial de fábrica"
}
```

### 5.2 Salida de Mercancía (*Outbound*)
- **Body JSON:**
```json
{
  "movementType": "OUTBOUND",
  "productId": 1,
  "warehouseId": 1,
  "sourceSpaceId": 3,
  "quantity": 90,
  "unitOfMeasure": "UNIDAD",
  "notes": "Despacho a cliente mayorista"
}
```
> **Comprobación del Observer Pattern:** Dado que el stock bajó a 10 unidades y el punto de reorden era 15, el observador `LowStockAlertObserver` crea automáticamente una necesidad de abastecimiento (`SupplyNeed`) abierta en compras.

### 5.3 Transferencia Interna entre Bahías (*Transfer*)
- **Body JSON:**
```json
{
  "movementType": "TRANSFER",
  "productId": 1,
  "warehouseId": 1,
  "sourceSpaceId": 3,
  "targetSpaceId": 4,
  "quantity": 5,
  "unitOfMeasure": "UNIDAD",
  "notes": "Reubicación de stock a zona frontal"
}
```

---

## 6. Aprovisionamiento y Órdenes de Compra

Maneja el ciclo de vida de compras con **State Pattern**, **Chain of Responsibility** (aprobación) y **Mediator Pattern**.

### 6.1 Crear Orden de Compra (Estado Inicial: `DRAFT`)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/procurement/orders`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "supplierId": 1,
  "warehouseId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 50,
      "unitPrice": 85.00
    }
  ]
}
```

### 6.2 Enviar a Aprobación y Aprobar la Orden (Transición de Estado)
- **Solicitar Aprobación:**
  ```bash
  curl -X POST http://localhost:8080/api/procurement/orders/1/submit-approval \
    -H "Authorization: Bearer $TOKEN"
  ```
- **Aprobar Orden:**
  ```bash
  curl -X POST http://localhost:8080/api/procurement/orders/1/approve \
    -H "Authorization: Bearer $TOKEN"
  ```
- **Recibir Mercancía (Transición a `RECEIVED` y mediación de ingreso a stock):**
  ```bash
  curl -X POST http://localhost:8080/api/procurement/orders/1/receive \
    -H "Authorization: Bearer $TOKEN"
  ```

---

## 7. Pedidos de Clientes y Tarifación Dinámica

Implementa **Decorator Pattern** para recargos en cascada (entrega urgente, descuentos por volumen) y **Chain of Responsibility** para validación crediticia y de inventario.

### 7.1 Crear un Pedido con Decoradores de Precio
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/orders`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "customerName": "Empresa Logística del Caribe",
  "customerTaxId": "900123456-1",
  "deliveryAddress": "Av. Circunvalar # 10-20, Barranquilla",
  "urgentDelivery": true,
  "items": [
    {
      "productId": 1,
      "quantity": 10,
      "unitPrice": 120.00
    }
  ]
}
```
> **Efecto del Decorador:** Si `"urgentDelivery": true`, `UrgentDeliveryPricingDecorator` calcula un recargo porcentual automático sobre el total base.

---

## 8. Despachos y Entrega de Mercancía

Orquestado por **DispatchFacade** y controlado por **DispatchState**.

### 8.1 Crear un Despacho a partir de una Orden Aprobada
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/dispatches`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "orderId": 1,
  "warehouseId": 1,
  "vehicleId": 1
}
```

### 8.2 Avanzar en la Máquina de Estados del Despacho
1. **Empacar Mercancía (`PENDING` -> `PACKED`):**
   ```bash
   curl -X POST http://localhost:8080/api/dispatches/1/pack -H "Authorization: Bearer $TOKEN"
   ```
2. **Iniciar Tránsito / Despacho (`PACKED` -> `IN_TRANSIT`):**
   ```bash
   curl -X POST http://localhost:8080/api/dispatches/1/ship -H "Authorization: Bearer $TOKEN"
   ```
3. **Confirmar Entrega Final (`IN_TRANSIT` -> `DELIVERED`):**
   ```bash
   curl -X POST http://localhost:8080/api/dispatches/1/deliver -H "Authorization: Bearer $TOKEN"
   ```

---

## 9. Flotas, Enrutamiento Óptimo y Proxy de Caché

### 9.1 Registrar un Vehículo con API Key Segura
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/vehicles`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "plate": "LOG-999",
  "model": "Camión Fotón 4.5T",
  "capacityKg": 4500,
  "rawApiKey": "veh_secret_key_12345"
}
```
> **Nota de Seguridad:** La API Key se almacena con hash **SHA-256**. El vehículo debe usar `veh_secret_key_12345` en el encabezado `X-Vehicle-Api-Key` para enviar telemetría.

### 9.2 Optimización de Ruta con Strategy Pattern
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/routes/optimize`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "strategy": "SHORTEST_DISTANCE",
  "stops": [
    {"stopId": 1, "latitude": 4.6097, "longitude": -74.0817, "address": "Punto Origen"},
    {"stopId": 2, "latitude": 4.6533, "longitude": -74.0836, "address": "Punto Entrega A"},
    {"stopId": 3, "latitude": 4.6781, "longitude": -74.0582, "address": "Punto Entrega B"}
  ]
}
```
> **Estrategias disponibles:** `SHORTEST_DISTANCE` (fórmula Haversine), `FASTEST_TIME`, `BALANCED`.
> Las consultas sucesivas con las mismas paradas son devueltas inmediatamente desde memoria gracias a `CachedRouteCalculationProxy` (**Proxy Pattern**).

---

## 10. Telemetría IoT en Vivo y Stream SSE

Demuestra el **Bridge Pattern** (publicación desacoplada del sink) y **Chain of Responsibility** (validación de datos GPS).

### 10.1 Enviar Coordenadas de Rastreo (Dispositivo GPS en Vehículo)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/tracking/telemetry`
- **Headers:**
  - `Content-Type: application/json`
  - `X-Vehicle-Api-Key: veh_secret_key_12345`
- **Body JSON:**
```json
{
  "latitude": 4.609710,
  "longitude": -74.081750,
  "speedKmh": 45.5,
  "recordedAt": "2026-10-18T20:30:00"
}
```

### 10.2 Conectarse al Canal en Vivo de Telemetría (Server-Sent Events)
- **Método:** `GET`
- **URL:** `http://localhost:8080/api/tracking/stream`
- **Comando en Terminal:**
```bash
curl -N http://localhost:8080/api/tracking/stream
```
> Mantén esta terminal abierta. Cada vez que el vehículo envíe telemetría en el punto 10.1, recibirás el evento JSON en tiempo real sin recargar la página.

### 10.3 Consultar la Trayectoria Histórica en GeoJSON
- **Método:** `GET`
- **URL:** `http://localhost:8080/api/tracking/vehicles/1/trail?limit=50`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Respuesta:** Retorna el objeto estándar `LineString` GeoJSON listo para ser dibujado en mapas como Leaflet o Google Maps.

---

## 11. Pronóstico de Demanda

Aplica **Strategy Pattern** (algoritmos matemáticos) y **Decorator Pattern** (colchón de seguridad y estacionalidad).

### 11.1 Generar Previsión de Demanda y Reabastecimiento Automático
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/demand/forecast`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "productId": 1,
  "algorithm": "EXPONENTIAL_SMOOTHING",
  "periodsAhead": 3,
  "seasonalMultiplier": 1.15,
  "safetyStockBufferPercentage": 0.10,
  "autoCreateSupplyNeeds": true
}
```
> **Algoritmos disponibles:** `MOVING_AVERAGE`, `EXPONENTIAL_SMOOTHING`, `LINEAR_REGRESSION`.
> Si el inventario disponible no cubre la proyección calculada, el sistema creará automáticamente la necesidad de abastecimiento (`SupplyNeed`) con origen `FORECAST`.

---

## 12. Optimización de Almacén y Snapshots/Rollback

Implementa el **Visitor Pattern** (`RelocationProposalVisitor` para análisis ABC) y el **Memento Pattern** (instantáneas y reversión histórica del diseño del almacén).

### 12.1 Analizar Almacén y Obtener Sugerencias de Reubicación ABC (Visitor)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/warehouses/1/optimize?autoCreateSnapshot=true`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Respuesta:** Analiza las bahías de almacenamiento y genera propuestas de traslado para productos Clase A situados en ubicaciones lejanas, creando previamente un snapshot de seguridad.

### 12.2 Crear un Snapshot Manual del Layout (Memento Pattern)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/warehouses/1/snapshots`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Body JSON:**
```json
{
  "warehouseId": 1,
  "snapshotName": "Configuracion_Previo_Temporada_Alta"
}
```

### 12.3 Revertir/Restaurar a un Snapshot Anterior (Rollback)
- **Método:** `POST`
- **URL:** `http://localhost:8080/api/warehouses/1/rollback?snapshotId=1`
- **Headers:** `Authorization: Bearer <TOKEN>`
- **Respuesta:** Restaura íntegramente la estructura física de espacios, nombres y capacidades a la versión almacenada en el snapshot.

---

## 13. Métricas de Prometheus y Salud del Sistema

### 13.1 Métricas para Prometheus / Grafana (Adapter Pattern)
- **Método:** `GET`
- **URL:** `http://localhost:8080/actuator/prometheus`
- **Acceso:** Público / Monitoreo
- **Resultado:** Expone métricas estándar de Spring Boot y métricas personalizadas de LogiSync como `logisync_telemetry_readings_total` y `logisync_vehicle_speed_kmh`.

### 13.2 Chequeo de Salud de la Aplicación
- **Método:** `GET`
- **URL:** `http://localhost:8080/actuator/health`

---

## 14. Ejecución Automatizada de Pruebas

El sistema cuenta con una suite completa de **79 pruebas automatizadas** que validan la arquitectura hexagonal, los casos de uso y cada uno de los patrones de diseño GoF.

Para ejecutar todas las pruebas en tu máquina local:

```bash
cd backend/sistema_cadena_suministro
mvn clean test
```

Para ejecutar la verificación completa (incluyendo empaquetado de artefactos):

```bash
mvn clean verify
```

**Resultado esperado:**
```text
[INFO] Tests run: 79, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
