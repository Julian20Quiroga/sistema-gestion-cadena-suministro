const strategies = {
  MOVING_AVERAGE: (f) => ({ historical: f.historicalPoints || [], projected: f.projections || [], caption: 'El promedio reciente se extiende a los periodos siguientes.' }),
  EXPONENTIAL_SMOOTHING: (f) => ({ historical: f.historicalPoints || [], projected: f.projections || [], caption: 'La proyeccion da mas peso a los datos recientes.' }),
  LINEAR_REGRESSION: (f) => ({ historical: f.historicalPoints || [], projected: f.projections || [], caption: 'La proyeccion sigue la tendencia observada.' }),
};
export class ForecastChartStrategy {
  static for(algorithm) { return strategies[algorithm] || ((f) => ({ historical: f.historicalPoints || [], projected: f.projections || [], caption: '' })); }
}
