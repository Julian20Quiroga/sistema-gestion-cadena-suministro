import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/auth';
import { fetchProducts } from '../productos/productService';
import { createVehicle, fetchActiveVehicles, fetchLatest, fetchTrail, fetchProductTraceability, subscribeTelemetry } from './trackingService';
import './Tracking.css';

const errorText = (e) => e.response?.data?.detail || e.response?.data?.message || 'No fue posible completar la operación.';
const dateText = (value) => value ? new Date(value).toLocaleString('es-CO') : 'Sin fecha';

export default function Tracking() {
  const { token, role } = useAuth();
  const navigate = useNavigate();
  const [vehicles, setVehicles] = useState([]);
  const [chosenId, setChosenId] = useState('');
  const [latest, setLatest] = useState(null);
  const [readings, setReadings] = useState([]);
  const [loadingVehicles, setLoadingVehicles] = useState(true);
  const [loadingTrack, setLoadingTrack] = useState(true);
  const [error, setError] = useState('');
  const [trackError, setTrackError] = useState('');
  const [form, setForm] = useState({ plate: '', model: '', capacityKg: '', averageSpeedKmh: '40' });
  const [savingVehicle, setSavingVehicle] = useState(false);
  const [formError, setFormError] = useState('');
  const [vehicleCreated, setVehicleCreated] = useState('');
  const [rawApiKey, setRawApiKey] = useState('');
  const [products, setProducts] = useState([]);
  const [traceProductId, setTraceProductId] = useState('');
  const [traceEvents, setTraceEvents] = useState([]);
  const [traceLoading, setTraceLoading] = useState(false);
  const [traceError, setTraceError] = useState('');
  const canRegister = ['PRESIDENT', 'ROLE_PRESIDENT'].includes(role);

  useEffect(() => {
    let active = true;
    fetchActiveVehicles({ token }).then((data) => {
      if (active) setVehicles((data.content || []).filter((v) => v.status === 'ACTIVE'));
    }).catch((e) => {
      if (active) setError(errorText(e));
    }).finally(() => {
      if (active) setLoadingVehicles(false);
    });
    return () => { active = false; };
  }, [token]);

  useEffect(() => {
    let active = true;
    fetchProducts({ token, page: 0, size: 100 }).then((page) => {
      if (active) setProducts(page.content || []);
    }).catch(() => { if (active) setTraceError('No fue posible cargar el catálogo para consultar trazabilidad.'); });
    return () => { active = false; };
  }, [token]);

  async function loadProductTrace(event) {
    event.preventDefault();
    if (!traceProductId) return;
    setTraceLoading(true); setTraceError('');
    try { setTraceEvents(await fetchProductTraceability({ token, productId: traceProductId })); }
    catch (requestError) { setTraceError(errorText(requestError)); setTraceEvents([]); }
    finally { setTraceLoading(false); }
  }

  const vehicleId = chosenId || (vehicles[0] ? String(vehicles[0].id) : '');
  useEffect(() => {
    if (!vehicleId) return undefined;
    let active = true;
    Promise.allSettled([fetchLatest({ token, id: vehicleId }), fetchTrail({ token, id: vehicleId })]).then(([last, trail]) => {
      if (!active) return;
      setLatest(last.status === 'fulfilled' ? last.value : null);
      setReadings(trail.status === 'fulfilled' ? trail.value.readings || [] : []);
      const failed = [last, trail].filter((item) => item.status === 'rejected' && item.reason.response?.status !== 404);
      if (failed.length) setTrackError(errorText(failed[0].reason));
      else setTrackError('');
    }).finally(() => { if (active) setLoadingTrack(false); });
    return () => { active = false; };
  }, [token, vehicleId]);

  useEffect(() => {
    if (!vehicleId || !token) return undefined;
    const controller = new AbortController();
    subscribeTelemetry({ token, signal: controller.signal, onReading: (event) => {
      const eventVehicleId = Number(String(event.deviceId || '').replace('VEHICLE-', ''));
      if (eventVehicleId !== Number(vehicleId)) return;
      const reading = {
        id: 'live-' + event.timestamp,
        vehicleId: eventVehicleId,
        vehiclePlate: vehicles.find((vehicle) => Number(vehicle.id) === eventVehicleId)?.plate,
        latitude: event.latitude,
        longitude: event.longitude,
        speedKmh: event.speedKmh,
        recordedAt: event.timestamp,
      };
      setLatest(reading);
      setReadings((current) => [reading, ...current.filter((item) => item.recordedAt !== reading.recordedAt)].slice(0, 50));
      setTrackError('');
    } });
    return () => controller.abort();
  }, [token, vehicleId, vehicles]);

  async function handleVehicleSubmit(event) {
    event.preventDefault();
    setFormError('');
    setVehicleCreated('');
    setRawApiKey('');
    setSavingVehicle(true);
    try {
      const result = await createVehicle({ token, vehicle: {
        plate: form.plate.trim(),
        model: form.model.trim(),
        capacityKg: Number(form.capacityKg),
        averageSpeedKmh: Number(form.averageSpeedKmh),
      } });
      setVehicles((current) => [{ ...result, status: 'ACTIVE' }, ...current]);
      setChosenId(String(result.id));
      setLatest(null);
      setReadings([]);
      setLoadingTrack(true);
      setVehicleCreated('Vehículo registrado. La clave se muestra aquí una sola vez.');
      setRawApiKey(result.rawApiKey || '');
      setForm({ plate: '', model: '', capacityKg: '', averageSpeedKmh: '40' });
    } catch (requestError) {
      setFormError(errorText(requestError));
    } finally {
      setSavingVehicle(false);
    }
  }

  const mapUrl = latest && latest.latitude != null && latest.longitude != null
    ? 'https://www.google.com/maps?q=' + latest.latitude + ',' + latest.longitude : '';

  return <main className="tracking-page">
    <header className="tracking-header">
      <button onClick={() => navigate('/dashboard')}>← Volver al dashboard</button>
      <h1>Seguimiento</h1><p>Consulta la ubicación y las lecturas recientes de los vehículos.</p>
    </header>
    {error && <div className="tracking-error">{error}</div>}
    {canRegister && <section className="tracking-panel tracking-create">
      <h2>Registrar vehículo</h2>
      <p>Agrega un vehículo para habilitar su seguimiento.</p>
      <form className="tracking-form" onSubmit={handleVehicleSubmit}>
        <label>Placa<input required maxLength="20" value={form.plate} placeholder="Ej. ABC123" onChange={(e) => setForm({ ...form, plate: e.target.value })} /></label>
        <label>Modelo<input required maxLength="100" value={form.model} placeholder="Ej. Camioneta de reparto" onChange={(e) => setForm({ ...form, model: e.target.value })} /></label>
        <label>Capacidad (kg)<input required type="number" min="0.01" step="0.01" value={form.capacityKg} onChange={(e) => setForm({ ...form, capacityKg: e.target.value })} /></label>
        <label>Velocidad promedio (km/h)<input required type="number" min="0.01" step="0.01" value={form.averageSpeedKmh} onChange={(e) => setForm({ ...form, averageSpeedKmh: e.target.value })} /></label>
        <button type="submit" disabled={savingVehicle}>{savingVehicle ? 'Guardando…' : 'Registrar vehículo'}</button>
      </form>
      {formError && <div className="tracking-error">{formError}</div>}
      {vehicleCreated && <div className="tracking-created">{vehicleCreated}</div>}
      {rawApiKey && <div className="tracking-key"><strong>Clave API del vehículo:</strong><code>{rawApiKey}</code><span>Guárdala en un lugar seguro para configurar la telemetría; no la compartas públicamente.</span></div>}
    </section>}
    <section className="tracking-filter"><label htmlFor="tracking-vehicle">Vehículo</label>
      <select id="tracking-vehicle" value={vehicleId} disabled={loadingVehicles || !vehicles.length} onChange={(e) => { setChosenId(e.target.value); setLoadingTrack(true); setTrackError(''); }}>
        {loadingVehicles && <option value="">Cargando vehículos…</option>}
        {!loadingVehicles && !vehicles.length && <option value="">No hay vehículos activos</option>}
        {vehicles.map((v) => <option key={v.id} value={v.id}>{v.plate} · {v.model}</option>)}
      </select>
    </section>
    {loadingVehicles ? <section className="tracking-panel">Cargando vehículos…</section>
      : !vehicles.length ? <section className="tracking-panel">No hay vehículos activos registrados para consultar.</section>
      : <>
        <section className="tracking-panel"><h2>Última ubicación</h2><p>Lectura telemétrica más reciente del vehículo.</p>
          {trackError && <div className="tracking-error">{trackError}</div>}
          {loadingTrack ? <p>Cargando lectura…</p> : latest ? <div className="tracking-data">
            <div><span>Vehículo</span><strong>{latest.vehiclePlate || 'Vehículo #' + latest.vehicleId}</strong></div>
            <div><span>Latitud</span><strong>{latest.latitude}</strong></div><div><span>Longitud</span><strong>{latest.longitude}</strong></div>
            <div><span>Velocidad</span><strong>{latest.speedKmh} km/h</strong></div>
            <div><span>Fecha</span><strong>{dateText(latest.recordedAt || latest.createdAt)}</strong></div>
            {mapUrl && <a href={mapUrl} target="_blank" rel="noreferrer">Ver ubicación en el mapa</a>}
          </div> : <p>Este vehículo todavía no tiene lecturas de telemetría.</p>}
        </section>
        <section className="tracking-panel"><h2>Recorrido reciente</h2><p>Últimas posiciones recibidas para este vehículo.</p>
          {loadingTrack ? <p>Cargando recorrido…</p> : !readings.length ? <p>No hay posiciones históricas para mostrar.</p> :
          <div className="tracking-scroll"><table><thead><tr><th>Fecha</th><th>Latitud</th><th>Longitud</th><th>Velocidad</th><th>Mapa</th></tr></thead><tbody>
            {readings.map((r) => <tr key={r.id}><td>{dateText(r.recordedAt || r.createdAt)}</td><td>{r.latitude}</td><td>{r.longitude}</td><td>{r.speedKmh} km/h</td><td><a href={'https://www.google.com/maps?q=' + r.latitude + ',' + r.longitude} target="_blank" rel="noreferrer">Ver</a></td></tr>)}
          </tbody></table></div>}
        </section>
      </>}
    <section className="tracking-panel tracking-traceability">
      <h2>Trazabilidad por producto</h2>
      <p>Consulta compras, movimientos de bodega, pedidos y despachos relacionados con un producto.</p>
      <form className="tracking-trace-form" onSubmit={loadProductTrace}>
        <label htmlFor="trace-product">Producto</label>
        <select id="trace-product" value={traceProductId} onChange={(event) => setTraceProductId(event.target.value)} required>
          <option value="">Selecciona un producto</option>
          {products.map((product) => <option key={product.id} value={product.id}>{product.name} · {product.sku}</option>)}
        </select>
        <button type="submit" disabled={traceLoading || !traceProductId}>{traceLoading ? 'Consultando…' : 'Consultar recorrido'}</button>
      </form>
      {traceError && <div className="tracking-error" role="alert">{traceError}</div>}
      {traceLoading && <p>Consultando etapas de la cadena…</p>}
      {!traceLoading && traceProductId && !traceEvents.length && !traceError && <p>No hay eventos de trazabilidad para este producto.</p>}
      {traceEvents.length > 0 && <div className="tracking-scroll"><table><thead><tr><th>Fecha de registro</th><th>Etapa</th><th>Registro</th><th>Estado actual</th><th>Detalle</th><th>Ubicación</th></tr></thead><tbody>
        {traceEvents.map((event) => <tr key={event.id}><td>{dateText(event.date)}</td><td>{event.stage}</td><td>{event.title}</td><td>{event.status}</td><td>{event.detail}</td><td>{event.location?.latitude != null && event.location?.longitude != null ? <a href={'https://www.google.com/maps?q=' + event.location.latitude + ',' + event.location.longitude} target="_blank" rel="noreferrer">{event.location.label ? `Ver mapa · ${event.location.label}` : 'Ver mapa'}</a> : event.location?.label || '—'}</td></tr>)}
      </tbody></table></div>}
    </section>
  </main>;
}

