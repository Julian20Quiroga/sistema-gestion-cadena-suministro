export class ForecastResultElement {
  constructor(forecast) { this.forecast = forecast; }
  accept(visitor) { return visitor.visitForecastResult(this.forecast); }
}

export class ForecastSummaryVisitor {
  visit(elements) {
    return elements.reduce((summary, element) => {
      const result = element.accept(this);
      return {
        products: summary.products + result.products,
        productsWithShortage: summary.productsWithShortage + result.productsWithShortage,
        suggestedUnits: summary.suggestedUnits + result.suggestedUnits,
      };
    }, { products: 0, productsWithShortage: 0, suggestedUnits: 0 });
  }
  visitForecastResult(forecast) {
    return {
      products: 1,
      productsWithShortage: Number(forecast.recommendedProcurementQuantity || 0) > 0 ? 1 : 0,
      suggestedUnits: Number(forecast.recommendedProcurementQuantity || 0),
    };
  }
}
