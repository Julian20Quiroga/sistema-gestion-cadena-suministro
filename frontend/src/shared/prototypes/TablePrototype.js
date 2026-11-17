class TablePrototype {
  constructor(config = {}) {
    this.config = config;
  }

  clone() {
    return new TablePrototype({
      ...this.config,
    });
  }
}

export default TablePrototype;