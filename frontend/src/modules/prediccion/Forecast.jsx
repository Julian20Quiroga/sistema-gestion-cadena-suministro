import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/auth';
import { fetchProducts } from '../productos/productService';
import {
  fetchForecastStrategies,
  fetchProductForecast,
  generateForecast,
} from './forecastService';
import './Forecast.css';
import { ForecastChartStrategy } from '../../shared/patterns/behavioral/ForecastChartStrategy';
import { ForecastRuleInterpreter } from '../../shared/patterns/behavioral/ForecastRuleInterpreter';
import { ForecastResultElement, ForecastSummaryVisitor } from '../../shared/patterns/behavioral/ForecastSummaryVisitor';

const defaultStrategies = [
  'MOVING_AVERAGE',
  'EXPONENTIAL_SMOOTHING',
  'LINEAR_REGRESSION',
];

const strategyLabels = {
  MOVING_AVERAGE: 'Promedio móvil',
  EXPONENTIAL_SMOOTHING: 'Suavizado exponencial',
  LINEAR_REGRESSION: 'Regresión lineal',
};

const formatNumber = (value) =>
  Number(value || 0).toLocaleString('es-CO', { maximumFractionDigits: 1 });
const forecastRuleInterpreter = new ForecastRuleInterpreter();
const forecastSummaryVisitor = new ForecastSummaryVisitor();

function ForecastChart({ forecast }) {
  const presentation = ForecastChartStrategy.for(forecast.algorithm)(forecast);
  const historical = presentation.historical.map(Number);
  const projections = presentation.projected.map(Number);
  const values = [...historical, ...projections];
  const maxValue = Math.max(1, ...values);
  const firstHistoricalMonth = new Date();
  firstHistoricalMonth.setDate(1);
  firstHistoricalMonth.setMonth(firstHistoricalMonth.getMonth() - historical.length + 1);
  const historicalLabels = historical.map((_, index) => {
    const month = new Date(firstHistoricalMonth.getFullYear(), firstHistoricalMonth.getMonth() + index, 1);
    return new Intl.DateTimeFormat('es-CO', { month: 'short', year: '2-digit' }).format(month);
  });

  if (values.length === 0) {
    return <p className="forecast-muted">No hay puntos para graficar.</p>;
  }

  return (
    <div className="forecast-chart" aria-label="Demanda histórica y proyectada">
      {values.map((value, index) => {
        const isProjection = index >= historical.length;
        const label = isProjection
          ? 'Proyección ' + (index - historical.length + 1)
          : historicalLabels[index];

        return (
          <div className="forecast-chart-column" key={label}>
            <span className="forecast-chart-value">{formatNumber(value)}</span>
            <div className="forecast-chart-track">
              <span
                className={
                  isProjection
                    ? 'forecast-chart-bar forecast-chart-bar-projection'
                    : 'forecast-chart-bar'
                }
                style={{ height: Math.max(5, (value / maxValue) * 100) + '%' }}
              />
            </div>
            <small title={label}>{label}</small>
          </div>
        );
      })}
    </div>
  );
}

function ForecastCard({ forecast }) {
  return (
    <article className="forecast-result-card">
      <header className="forecast-result-heading">
        <div>
          <h3>{forecast.productName || 'Producto #' + forecast.productId}</h3>
          <p>{forecast.sku || 'Sin SKU'} · {strategyLabels[forecast.algorithm] || forecast.algorithm}</p>
        </div>
      {forecastRuleInterpreter.interpret(forecast) && (
        <span className="forecast-supply-badge">Compra recomendada</span>
      )}
      {forecast.supplyNeedCreated && (
          <span className="forecast-supply-badge">Necesidad #{forecast.supplyNeedId}</span>
        )}
      </header>

      <div className="forecast-metrics">
        <div>
          <span>Stock disponible</span>
          <strong>{formatNumber(forecast.currentAvailableStock)}</strong>
        </div>
        <div>
          <span>Demanda proyectada</span>
          <strong>{formatNumber(forecast.totalProjectedDemand)}</strong>
        </div>
        <div>
          <span>Compra sugerida</span>
          <strong>{formatNumber(forecast.recommendedProcurementQuantity)}</strong>
        </div>
      </div>

      <div className="forecast-chart-heading">
        <h4>Demanda histórica y proyección</h4>
        <div className="forecast-chart-legend">
          <span><i /> Histórica</span>
          <span><i className="forecast-legend-projection" /> Proyectada</span>
        </div>
      </div>
      <ForecastChart forecast={forecast} />
      {forecast.historicalPoints?.length > 0 && forecast.historicalPoints.every((point) => Number(point) === 0) ? (
        <p className="forecast-muted" role="status">
          No hay pedidos entregados registrados en los últimos 12 meses; la proyección se basa en una demanda histórica de cero.
        </p>
      ) : (
        <p className="forecast-muted">
          Los meses con valor 0 no tienen pedidos entregados registrados en el período consultado.
        </p>
      )}
      <p className="forecast-muted">{ForecastChartStrategy.for(forecast.algorithm)(forecast).caption}</p>
    </article>
  );
}

function Forecast() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [strategies, setStrategies] = useState(defaultStrategies);
  const [productId, setProductId] = useState('');
  const [algorithm, setAlgorithm] = useState('MOVING_AVERAGE');
  const [periodsAhead, setPeriodsAhead] = useState(3);
  const [seasonalMultiplier, setSeasonalMultiplier] = useState(1);
  const [safetyStockBufferPercentage, setSafetyStockBufferPercentage] = useState(0);
  const [autoCreateSupplyNeeds, setAutoCreateSupplyNeeds] = useState(false);
  const [forecasts, setForecasts] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loadingProducts, setLoadingProducts] = useState(false);
  const [loadingForecast, setLoadingForecast] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const forecastInsights = forecastSummaryVisitor.visit(forecasts.map((forecast) => new ForecastResultElement(forecast)));

  useEffect(() => {
    let current = true;

    async function loadProducts() {
      setLoadingProducts(true);
      try {
        const result = await fetchProducts({ token, page: 0, size: 100 });
        if (current) setProducts(result.content || []);
      } catch (requestError) {
        if (current) {
          setError(
            requestError.response?.data?.detail ||
              'No fue posible cargar los productos para la predicción.'
          );
        }
      } finally {
        if (current) setLoadingProducts(false);
      }
    }

    async function loadStrategies() {
      try {
        const result = await fetchForecastStrategies({ token });
        if (current && Array.isArray(result.strategies) && result.strategies.length) {
          setStrategies(result.strategies);
        }
      } catch {
        // Se conservan las estrategias conocidas si falla la consulta del catálogo.
      }
    }

    if (token) {
      loadProducts();
      loadStrategies();
    }

    return () => {
      current = false;
    };
  }, [token]);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setNotice('');
    setForecasts([]);
    setSummary(null);
    setLoadingForecast(true);

    try {
      if (productId) {
        const result = await fetchProductForecast({
          token,
          productId: Number(productId),
          algorithm,
          periodsAhead: Number(periodsAhead),
        });
        setForecasts([result]);
        setSummary({ productsProcessed: 1, supplyNeedsCreated: 0 });
        setNotice('Predicción calculada para el producto seleccionado.');
      } else {
        if (
          autoCreateSupplyNeeds &&
          !window.confirm(
            'Esta acción puede crear necesidades de abastecimiento para varios productos. ¿Deseas continuar?'
          )
        ) {
          return;
        }

        const result = await generateForecast({
          token,
          request: {
            productId: null,
            algorithm,
            periodsAhead: Number(periodsAhead),
            seasonalMultiplier: Number(seasonalMultiplier),
            safetyStockBufferPercentage: Number(safetyStockBufferPercentage),
            autoCreateSupplyNeeds,
          },
        });
        setForecasts(result.forecasts || []);
        setSummary({
          productsProcessed: result.productsProcessed || 0,
          supplyNeedsCreated: result.supplyNeedsCreated || 0,
        });
        setNotice('Proyección generada para el catálogo.');
      }
    } catch (requestError) {
      setError(
        requestError.response?.data?.detail ||
          requestError.response?.data?.message ||
          'No fue posible calcular la predicción. Revisa la conexión con el backend.'
      );
    } finally {
      setLoadingForecast(false);
    }
  }

  return (
    <main className="forecast-page">
      <header className="forecast-page-header">
        <div>
          <button
            className="forecast-back-button"
            type="button"
            onClick={() => navigate('/dashboard')}
          >
            ← Volver al dashboard
          </button>
          <h1>Predicción de demanda</h1>
          <p>Calcula la demanda futura con las estrategias disponibles.</p>
        </div>
      </header>

      {notice && <p className="forecast-notice" role="status">{notice}</p>}
      {error && <p className="forecast-error" role="alert">{error}</p>}

      <section className="forecast-panel">
        <h2>Parámetros de predicción</h2>
        <form onSubmit={handleSubmit}>
          <div className="forecast-form-grid">
            <label>
              Producto
              <select
                value={productId}
                onChange={(event) => setProductId(event.target.value)}
                disabled={loadingProducts}
              >
                <option value="">
                  {loadingProducts ? 'Cargando productos…' : 'Todo el catálogo'}
                </option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} · {product.sku}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Estrategia
              <select value={algorithm} onChange={(event) => setAlgorithm(event.target.value)}>
                {strategies.map((strategy) => (
                  <option key={strategy} value={strategy}>
                    {strategyLabels[strategy] || strategy}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Periodos a proyectar
              <input
                type="number"
                min="1"
                max="12"
                value={periodsAhead}
                onChange={(event) => setPeriodsAhead(event.target.value)}
                required
              />
            </label>

            <label>
              Multiplicador estacional
              <input
                type="number"
                min="0.1"
                step="0.1"
                value={seasonalMultiplier}
                onChange={(event) => setSeasonalMultiplier(event.target.value)}
                disabled={Boolean(productId)}
                required
              />
            </label>

            <label>
              Colchón de inventario (%)
              <input
                type="number"
                min="0"
                step="1"
                value={safetyStockBufferPercentage}
                onChange={(event) => setSafetyStockBufferPercentage(event.target.value)}
                disabled={Boolean(productId)}
                required
              />
            </label>
          </div>

          <label className="forecast-checkbox">
            <input
              type="checkbox"
              checked={autoCreateSupplyNeeds}
              onChange={(event) => setAutoCreateSupplyNeeds(event.target.checked)}
              disabled={Boolean(productId)}
            />
            Crear necesidades de abastecimiento automáticamente
          </label>
          <p className="forecast-help">
            La generación para todo el catálogo puede crear necesidades si activas esta opción.
            Para un producto individual solo se consulta la proyección.
          </p>

          <div className="forecast-form-actions">
            <button className="forecast-primary-button" type="submit" disabled={loadingForecast || loadingProducts}>
              {loadingForecast
                ? 'Calculando…'
                : productId
                  ? 'Calcular producto'
                  : 'Generar predicción del catálogo'}
            </button>
          </div>
        </form>
      </section>

      {summary && (
        <section className="forecast-summary" aria-live="polite">
          <article>
            <span>Productos procesados</span>
            <strong>{summary.productsProcessed}</strong>
          </article>
          <article>
            <span>Necesidades creadas</span>
            <strong>{summary.supplyNeedsCreated}</strong>
          </article>
        </section>
      )}

      {forecasts.length > 0 && (
        <section className="forecast-summary" aria-label="Resumen de recomendaciones">
          <article><span>Productos con compra sugerida</span><strong>{forecastInsights.productsWithShortage}</strong></article>
          <article><span>Unidades sugeridas en total</span><strong>{formatNumber(forecastInsights.suggestedUnits)}</strong></article>
        </section>
      )}

      <section className="forecast-results">
        <h2>Resultados</h2>
        {loadingForecast ? (
          <p className="forecast-empty">Calculando la proyección…</p>
        ) : forecasts.length === 0 ? (
          <p className="forecast-empty">Selecciona los parámetros y genera una predicción.</p>
        ) : (
          forecasts.map((forecast) => (
            <ForecastCard key={forecast.productId} forecast={forecast} />
          ))
        )}
      </section>
    </main>
  );
}

export default Forecast;
