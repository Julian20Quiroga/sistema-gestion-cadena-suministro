# Evidencia del frontend LogiSync

## Pruebas de patrones de comportamiento

Desde `frontend/`, ejecutar:

```powershell
npm test
```

La suite nativa de Node.js cubre los 11 patrones de comportamiento del frontend con 14 casos: Iterator, State, Strategy, Interpreter, Visitor, Template Method, Command, Mediator, Memento, Chain of Responsibility y Observer/Event Bus. El resultado registrado el 23 de noviembre de 2026 fue **14 aprobadas, 0 fallidas**. El detalle está en `frontend-pattern-tests.txt`.

Estas pruebas ejercitan las clases y reglas de los patrones de forma aislada. Las capturas muestran pantallas de demostración asociadas; no son pruebas de aceptación automatizadas de toda la aplicación.

## Capturas

- `01-dashboard-presidente.png`: indicadores del dashboard.
- `02-productos.png`: catálogo de productos de demostración.
- `03-inventario-entrada-confirmada.png`: entrada de inventario visible.
- `04-optimizacion-confirmacion.png`: confirmación previa a optimizar.
- `05-prediccion-demanda.png`: resultado de predicción.
- `06-optimizacion-snapshots.png`: puntos de restauración de bodega.

Se excluyeron capturas de pantallas con datos personales de proveedores/clientes o coordenadas precisas. La telemetría IoT evidenciada en las pruebas del proyecto fue de demostración manual; no se probó un dispositivo GPS físico.
