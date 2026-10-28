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

El frontend está en desarrollo. La autenticación y el dashboard están implementados. Los módulos de despachos, inventario, órdenes de compra, pedidos, productos, proveedores, seguimiento y usuarios están pendientes de desarrollo.