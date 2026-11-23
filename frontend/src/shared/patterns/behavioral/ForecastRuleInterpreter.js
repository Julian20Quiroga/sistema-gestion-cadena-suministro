export class ForecastShortageExpression {
  interpret(context) { return Number(context.recommendedProcurementQuantity || 0) > 0 || Number(context.totalProjectedDemand || 0) > Number(context.currentAvailableStock || 0); }
}
export class ForecastRuleInterpreter {
  constructor(expression = new ForecastShortageExpression()) { this.expression = expression; }
  interpret(forecast) { return this.expression.interpret(forecast); }
}
