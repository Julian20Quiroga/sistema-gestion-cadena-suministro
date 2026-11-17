import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import {
  fetchRouteOrders,
  fetchRouteVehicles,
  fetchRouteWarehouses,
  optimizeRoute,
  updateWarehouseCoordinates,
} from './routeService';
import './Routes.css';

const apiError = (error) => error.response?.data?.detail || error.response?.data?.message || 'No fue posible completar la operación.';
const distanceFormat = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 2 });
const kgFormat = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 2 });

function mapUrl(stops) {
  if (!stops || stops.length < 2) return '';
  const points = stops.map((stop) => stop.latitude + ',' + stop.longitude);
  const waypoints = points.slice(1, -1);
  return 'https://www.google.com/maps/dir/?api=1&origin=' + encodeURIComponent(points[0])
    + '&destination=' + encodeURIComponent(points[points.length - 1])
    + (waypoints.length ? '&waypoints=' + encodeURIComponent(waypoints.join('|')) : '');
}

export default function RoutesPage() {
  const { token, role } = useAuth();
  const navigate = useNavigate();
  const [vehicles, setVehicles] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [orders, setOrders] = useState([]);
  const [vehicleId, setVehicleId] = useState('');
  const [warehouseId, setWarehouseId] = useState('');
  const [orderIds, setOrderIds] = useState([]);
  const [latitude, setLatitude] = useState('');
  const [longitude, setLongitude] = useState('');
  const [loading, setLoading] = useState(true);
  const [savingCoordinates, setSavingCoordinates] = useState(false);
  const [calculating, setCalculating] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [routeResult, setRouteResult] = useState(null);

  useEffect(() => {
    let active = true;
    Promise.all([
      fetchRouteVehicles({ token }),
      fetchRouteWarehouses({ token }),
      fetchRouteOrders({ token }),
    ]).then(([vehiclePage, warehousePage, orderPage]) => {
      if (!active) return;
      const activeVehicles = (vehiclePage.content || []).filter((item) => item.status === 'ACTIVE');
      const activeWarehouses = (warehousePage.content || []).filter((item) => item.status === 'ACTIVE');
      const eligibleOrders = (orderPage.content || []).filter((order) =>
        ['REGISTRADO', 'PREPARACION'].includes(order.status)
      );
      setVehicles(activeVehicles);
      setWarehouses(activeWarehouses);
      setOrders(eligibleOrders);
      if (activeVehicles.length) setVehicleId(String(activeVehicles[0].id));
      if (activeWarehouses.length) {
        setWarehouseId(String(activeWarehouses[0].id));
        setLatitude(activeWarehouses[0].latitude == null ? '' : String(activeWarehouses[0].latitude));
        setLongitude(activeWarehouses[0].longitude == null ? '' : String(activeWarehouses[0].longitude));
      }
    }).catch((requestError) => {
      if (active) setError(apiError(requestError));
    }).finally(() => {
      if (active) setLoading(false);
    });
    return () => { active = false; };
  }, [token]);

  const selectedWarehouse = useMemo(
    () => warehouses.find((item) => String(item.id) === String(warehouseId)),
    [warehouses, warehouseId]
  );
  const eligibleOrders = useMemo(
    () => orders.filter((order) => order.deliveryLatitude != null && order.deliveryLongitude != null),
    [orders]
  );
  const ordersWithoutCoordinates = orders.length - eligibleOrders.length;
  const canUpdateCoordinates = ['PRESIDENT', 'ROLE_PRESIDENT'].includes(role);

  function chooseWarehouse(id) {
    setWarehouseId(id);
    const warehouse = warehouses.find((item) => String(item.id) === String(id));
    setLatitude(warehouse?.latitude == null ? '' : String(warehouse.latitude));
    setLongitude(warehouse?.longitude == null ? '' : String(warehouse.longitude));
    setRouteResult(null);
    setError('');
  }

  function toggleOrder(id) {
    setOrderIds((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id]);
    setRouteResult(null);
  }

  async function handleCoordinatesSubmit(event) {
    event.preventDefault();
    if (!selectedWarehouse) return;
    if (latitude.trim() === '' || longitude.trim() === '') {
      setError('Escribe la latitud y la longitud antes de guardar.');
      return;
    }
    const latitudeNumber = Number(latitude);
    const longitudeNumber = Number(longitude);
    if (!Number.isFinite(latitudeNumber) || latitudeNumber < -90 || latitudeNumber > 90
      || !Number.isFinite(longitudeNumber) || longitudeNumber < -180 || longitudeNumber > 180) {
      setError('La latitud debe estar entre -90 y 90 y la longitud entre -180 y 180.');
      return;
    }
    setSavingCoordinates(true);
    setError('');
    setMessage('');
    try {
      const updated = await updateWarehouseCoordinates({ token, warehouse: selectedWarehouse, latitude, longitude });
      setWarehouses((current) => current.map((item) => item.id === updated.id ? updated : item));
      setMessage('Coordenadas de la bodega guardadas.');
    } catch (requestError) {
      setError(apiError(requestError));
    } finally {
      setSavingCoordinates(false);
    }
  }

  async function handleOptimize(event) {
    event.preventDefault();
    setError('');
    setMessage('');
    setRouteResult(null);
    if (!selectedWarehouse || selectedWarehouse.latitude == null || selectedWarehouse.longitude == null) {
      setError('Guarda primero las coordenadas de la bodega seleccionada.');
      return;
    }
    if (!vehicleId || !orderIds.length) {
      setError('Selecciona un vehículo y al menos un pedido.');
      return;
    }
    setCalculating(true);
    try {
      const result = await optimizeRoute({ token, warehouseId, vehicleId, orderIds });
      setRouteResult(result);
      setMessage('Ruta optimizada correctamente.');
    } catch (requestError) {
      setError(apiError(requestError));
    } finally {
      setCalculating(false);
    }
  }

  return <main className="routes-page">
    <header className="routes-header">
      <div><button className="routes-back" onClick={() => navigate('/dashboard')}>← Volver al dashboard</button><h1>Rutas</h1><p>Calcula el orden de entrega de pedidos desde una bodega.</p></div>
    </header>

    {error && <div className="routes-error" role="alert">{error}</div>}
    {message && <div className="routes-success" role="status">{message}</div>}

    {loading ? <section className="routes-card">Cargando vehículos, bodegas y pedidos…</section>
      : <><section className="routes-card">
        <h2>Datos de la ruta</h2>
        <form className="routes-form" onSubmit={handleOptimize}>
          <label>Bodega de salida
            <select required value={warehouseId} onChange={(event) => chooseWarehouse(event.target.value)}>
              {warehouses.map((warehouse) => <option key={warehouse.id} value={warehouse.id}>{warehouse.name}</option>)}
            </select>
          </label>
          <label>Vehículo
            <select required value={vehicleId} onChange={(event) => setVehicleId(event.target.value)}>
              {vehicles.map((vehicle) => <option key={vehicle.id} value={vehicle.id}>{vehicle.plate} · {vehicle.model} · capacidad {vehicle.capacityKg} kg</option>)}
            </select>
          </label>

          {selectedWarehouse && canUpdateCoordinates && <section className="routes-coordinate-box">
            <h3>Coordenadas de la bodega</h3>
            <p>El optimizador usa esta ubicación como punto de salida. Puedes revisar o actualizar la latitud y longitud.</p>
            {selectedWarehouse.latitude === 0 && selectedWarehouse.longitude === 0 && <p className="routes-coordinate-warning">La ubicación figura como 0, 0. Si no es la ubicación real de la bodega, corrígela aquí.</p>}
            <div className="routes-coordinate-fields">
              <label>Latitud<input required type="number" min="-90" max="90" step="0.000001" value={latitude} onChange={(event) => setLatitude(event.target.value)} /></label>
              <label>Longitud<input required type="number" min="-180" max="180" step="0.000001" value={longitude} onChange={(event) => setLongitude(event.target.value)} /></label>
              <button type="button" disabled={savingCoordinates || latitude.trim() === '' || longitude.trim() === ''} onClick={handleCoordinatesSubmit}>{savingCoordinates ? 'Guardando…' : 'Guardar coordenadas'}</button>
            </div>
          </section>}
          {selectedWarehouse && selectedWarehouse.latitude != null && selectedWarehouse.longitude != null && <p className="routes-hint">Salida: {selectedWarehouse.latitude}, {selectedWarehouse.longitude}</p>}

          <fieldset className="routes-orders">
            <legend>Pedidos a incluir</legend>
            {eligibleOrders.length ? eligibleOrders.map((order) => <label key={order.id} className="routes-order-option">
              <input type="checkbox" checked={orderIds.includes(Number(order.id))} onChange={() => toggleOrder(Number(order.id))} />
              <span><strong>{order.orderReference}</strong> · {order.customerName} · {order.deliveryCity}</span>
            </label>) : <p>No hay pedidos en preparación o registrados con coordenadas de entrega.</p>}
            {ordersWithoutCoordinates > 0 && <p className="routes-hint">{ordersWithoutCoordinates} pedido(s) no se muestran porque no tienen coordenadas de entrega.</p>}
          </fieldset>
          <button className="routes-primary" type="submit" disabled={calculating || !vehicles.length || !eligibleOrders.length}>{calculating ? 'Calculando…' : 'Optimizar ruta'}</button>
        </form>
        {(!vehicles.length || !warehouses.length) && <p className="routes-hint">{!vehicles.length && 'No hay vehículos activos. '}{!warehouses.length && 'No hay bodegas activas.'}</p>}
      </section>

      {routeResult && <section className="routes-card routes-result">
        <div className="routes-result-heading"><div><h2>Ruta optimizada</h2><p>Estrategia: {routeResult.strategyUsed}</p></div>{mapUrl(routeResult.stops) && <a href={mapUrl(routeResult.stops)} target="_blank" rel="noreferrer">Abrir recorrido en Google Maps</a>}</div>
        <div className="routes-metrics">
          <div><span>Distancia total</span><strong>{distanceFormat.format(routeResult.totalDistanceKm)} km</strong></div>
          <div><span>Duración estimada</span><strong>{routeResult.estimatedDurationMinutes} min</strong></div>
          <div><span>Carga estimada</span><strong>{kgFormat.format(Number(routeResult.totalPayloadKg || 0))} kg</strong></div>
        </div>
        <div className="routes-scroll"><table><thead><tr><th>Parada</th><th>Destino</th><th>Coordenadas</th><th>Distancia desde anterior</th></tr></thead><tbody>
          {(routeResult.stops || []).map((stop) => <tr key={stop.sequence}><td>{stop.sequence}</td><td>{stop.name}</td><td>{stop.latitude}, {stop.longitude}</td><td>{distanceFormat.format(stop.distanceFromPreviousKm)} km</td></tr>)}
        </tbody></table></div>
      </section>}</>}
  </main>;
}


