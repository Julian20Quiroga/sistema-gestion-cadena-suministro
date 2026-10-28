import TablePrototype from './TablePrototype';

class TableConfigPrototype extends TablePrototype {
  constructor() {
    super({
      columns: [],
      actions: [],
      pagination: true,
    });
  }

  clone() {
    return Object.assign(
      Object.create(Object.getPrototypeOf(this)),
      this
    );
  }

  withConfig(config) {
    this.config = {
      ...this.config,
      ...config,
    };

    return this;
  }
}

export default TableConfigPrototype;