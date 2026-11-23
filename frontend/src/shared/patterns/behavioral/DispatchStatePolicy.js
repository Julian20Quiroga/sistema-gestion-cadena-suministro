class DispatchStatusState {
  actions() { return []; }
}
class PendingDispatchState extends DispatchStatusState {
  actions() { return [{ action: 'prepare', label: 'Iniciar alistamiento' }, { action: 'cancel', label: 'Cancelar', danger: true }]; }
}
class PreparingDispatchState extends DispatchStatusState {
  actions() { return [{ action: 'ship', label: 'Despachar' }, { action: 'cancel', label: 'Cancelar', danger: true }]; }
}
class InTransitDispatchState extends DispatchStatusState {
  actions() { return [{ action: 'deliver', label: 'Confirmar entrega' }]; }
}
class TerminalDispatchState extends DispatchStatusState {}

export class DispatchWorkflow {
  constructor(status) {
    const states = { PENDIENTE: PendingDispatchState, PREPARANDO: PreparingDispatchState, EN_TRANSPORTE: InTransitDispatchState, ENTREGADO: TerminalDispatchState, CANCELADO: TerminalDispatchState };
    const State = states[status] || TerminalDispatchState;
    this.state = new State();
  }
  availableActions() { return this.state.actions(); }
}

export class DispatchStatePolicy {
  actionsFor(status) { return new DispatchWorkflow(status).availableActions(); }
}
