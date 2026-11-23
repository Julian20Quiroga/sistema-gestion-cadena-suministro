# Guía de integración IoT

LogiSync recibe posiciones de vehículos mediante una solicitud HTTP autenticada con una clave propia por vehículo. La lectura se guarda y se publica al instante para los clientes conectados al canal SSE del módulo Seguimiento.

## Preparar un vehículo

1. Entra al módulo Seguimiento con un usuario presidente y registra el vehículo.
2. Copia la clave API que aparece una sola vez y guárdala de forma segura en el dispositivo. No la incluyas en el repositorio ni en capturas públicas.
3. Configura el dispositivo para enviar una lectura cada cierto intervalo cuando tenga conexión a Internet.

## Enviar una lectura

Envía una solicitud `POST` a `http://<servidor>:5050/api/tracking/telemetry` con `Content-Type: application/json` y el encabezado `X-Vehicle-Api-Key`:

```http
POST /api/tracking/telemetry HTTP/1.1
Host: <servidor>:5050
Content-Type: application/json
X-Vehicle-Api-Key: <clave-del-vehiculo>

{
  "latitude": 7.125,
  "longitude": -73.119,
  "speedKmh": 25,
  "recordedAt": "2026-11-23T10:20:00"
}
```

Las coordenadas deben estar en los rangos de latitud `-90..90` y longitud `-180..180`; la velocidad no puede ser negativa. `recordedAt` usa fecha y hora local ISO, sin sufijo de zona horaria. Las lecturas repetidas del mismo vehículo dentro del mismo segundo se rechazan para evitar duplicados.

## Ver la lectura

- El módulo Seguimiento consulta la última lectura y el recorrido almacenado por vehículo.
- El canal `GET /api/tracking/stream` emite lecturas nuevas como eventos SSE. El navegador del sistema se conecta a este canal con el token de sesión.
- También se puede consultar `GET /api/tracking/vehicles/{id}/latest` o `GET /api/tracking/vehicles/{id}/trail?limit=50` usando autenticación de usuario.

La página IoT del sistema envía una lectura manual de demostración. Para verificar telemetría automática hay que usar un dispositivo o programa externo que envíe el `POST` descrito arriba. La integración de software está implementada; la conexión con hardware físico depende del equipo, su firmware y su conectividad.
