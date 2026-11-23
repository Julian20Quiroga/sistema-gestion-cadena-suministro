class UiEventBus {
  listeners = new Map();
  subscribe(name, listener) { const group = this.listeners.get(name) || new Set(); group.add(listener); this.listeners.set(name, group); return () => group.delete(listener); }
  publish(name, payload) { (this.listeners.get(name) || []).forEach((listener) => listener(payload)); }
}
export const uiEventBus = new UiEventBus();
