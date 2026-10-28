import ProductTablePrototype from './ProductTablePrototype';

const productTablePrototype = new ProductTablePrototype();

const productTableConfig = productTablePrototype.clone();

console.log(
  'Tipo del prototipo original:',
  productTablePrototype.constructor.name
);

console.log(
  'Tipo del clon:',
  productTableConfig.constructor.name
);

export default productTableConfig;