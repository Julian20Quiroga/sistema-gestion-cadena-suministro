export class CollectionIterator {
  constructor(items = []) { this.items = items; this.index = 0; }
  hasNext() { return this.index < this.items.length; }
  next() { return this.hasNext() ? this.items[this.index++] : undefined; }
  static toArray(items) { const iterator = new CollectionIterator(items); const values = []; while (iterator.hasNext()) values.push(iterator.next()); return values; }
}
