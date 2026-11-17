import TableConfigPrototype from './TableConfigPrototype';

class ProductTablePrototype extends TableConfigPrototype {
  constructor() {
    super();

    this.config = {
      ...this.config,
      columns: [
        'SKU',
        'Descripción',
        'Modelo',
        'Precio',
        'Estado',
      ],
      actions: ['Ver', 'Editar'],
    };
  }
}

export default ProductTablePrototype;