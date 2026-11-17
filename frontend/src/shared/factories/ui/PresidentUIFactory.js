import UIFactory from './UIFactory';

class PresidentUIFactory extends UIFactory {
  createNavigation() {
    return {
      role: 'PRESIDENT',
      items: [
        {
          label: 'Dashboard',
          type: 'item',
        },
        {
          label: 'Distribuidor',
          type: 'group',
          children: [
            'Despachos',
            'Rutas',
            'Seguimiento',
            'IoT',
          ],
        },
        {
          label: 'Operaciones',
          type: 'group',
          children: [
            'Productos',
            'Abastecimiento',
            'Pedidos',
            'Inventario',
          ],
        },
        {
          label: 'Análisis y Optimización',
          type: 'group',
          children: [
            'Predicción',
            'Optimización',
          ],
        },
        {
          label: 'Proveedores',
          type: 'item',
        },
        {
          label: 'Usuarios',
          type: 'item',
        },
      ],
    };
  }
}

export default PresidentUIFactory;