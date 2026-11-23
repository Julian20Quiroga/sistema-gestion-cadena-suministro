# LogiSync — Frontend

## Tecnologías usadas

- **Biblioteca de interfaz:** React 19
- **Herramienta de desarrollo y compilación:** Vite 8
- **Rutas:** React Router
- **Solicitudes al backend:** Axios
- **Backend:** Spring Boot, disponible localmente en `http://127.0.0.1:5050`

## Requisitos previos

- Tener instalado **Node.js** y **npm**.
- Tener el backend de LogiSync en ejecución en `http://127.0.0.1:5050` para usar las funciones que requieren conexión con el servidor.

## Cómo ejecutar el frontend

1. Abrir una terminal en la carpeta `frontend` del repositorio.
2. Instalar las dependencias:

   ```bash
   npm install

   ```
3. Iniciar el servidor de desarrollo:

   ```bash
   npm run dev
   ```

4. Abrir en el navegador la dirección que muestra Vite. En este proyecto es `http://localhost:5173/`.

## Estado del proyecto

El frontend incluye autenticación por roles, dashboard, despachos, inventario, abastecimiento, pedidos, productos, categorías, proveedores, usuarios, seguimiento, telemetría IoT, rutas, predicción de demanda y optimización de bodegas.

El módulo de seguimiento recibe lecturas del backend por Server-Sent Events (SSE). El módulo IoT ofrece un formulario de demostración; para operación automática, cualquier dispositivo capaz de realizar solicitudes HTTP puede enviar lecturas al endpoint descrito en `docs/GUIA_INTEGRACION_IOT.md`.

La trazabilidad por producto muestra eventos de abastecimiento, ubicaciones jerárquicas de almacenamiento, destinos de entrega y, cuando hay un despacho en transporte con un vehículo asignado, su última posición telemétrica.
