import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import { fetchIotVehicles, sendTelemetry } from './iotService';
import './IoT.css';

const sampleLocation = { latitude: '7.1193', longitude: '-73.1227' };
const localDateTime = () => {
  const now = new Date();
  return new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 19);
};
const errorText = (error) => error.response?.data?.detail || error.response?.data?.message || 'No fue posible enviar la lectura.';

export default function IoT() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [vehicles, setVehicles] = useState([]);
  const [vehicleId, setVehicleId] = useState('');
  const [apiKey, setApiKey] = useState('');
  const [latitude, setLatitude] = useState(sampleLocation.latitude);
  const [longitude, setLongitude] = useState(sampleLocation.longitude);
  const [speedKmh, setSpeedKmh] = useState('25');
  const [loadingVehicles, setLoadingVehicles] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    let active = true;
    fetchIotVehicles({ token }).then((result) => {
      if (active) {
        const activeVehicles = (result.content || []).filter((vehicle) => vehicle.status === 'ACTIVE');
        setVehicles(activeVehicles);
        if (activeVehicles.length) setVehicleId(String(activeVehicles[0].id));
      }
    }).catch((requestError) => {
      if (active) setError(errorText(requestError));
    }).finally(() => {
      if (active) setLoadingVehicles(false);
    });
    return () => { active = false; };
  }, [token]);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setSuccess('');
    setSending(true);
    try {
      const reading = await sendTelemetry({
        token,
        apiKey,
        reading: {
          latitude: Number(latitude),
          longitude: Number(longitude),
          speedKmh: Number(speedKmh),
          recordedAt: localDateTime(),
        },
      });
      setSuccess('Lectura enviada para ' + (reading.vehiclePlate || 'el vehículo') + '. Ya puedes verla en Seguimiento.');
    } catch (requestError) {
      setError(errorText(requestError));
    } finally {
      setSending(false);
    }
  }

  return <main className="iot-page">
    <header className="iot-header">
      <div>
        <button className="iot-back" onClick={() => navigate('/dashboard')}>← Volver al dashboard</button>
        <h1>IoT</h1>
        <p>Envía una lectura de ubicación de prueba para un vehículo.</p>
      </div>
      <button className="iot-secondary" onClick={() => navigate('/seguimiento')}>Ver seguimiento</button>
    </header>

    <section className="iot-card">
      <h2>Registrar lectura de telemetría</h2>
      <p className="iot-description">Esta pantalla envía una lectura manual de demostración. Para datos reales, el dispositivo IoT debe enviar su ubicación y usar la clave API del vehículo.</p>

      {error && <div className="iot-error" role="alert">{error}</div>}
      {success && <div className="iot-success" role="status">{success}</div>}

      {!loadingVehicles && vehicles.length === 0 ? <p className="iot-empty">No hay vehículos activos. Registra uno primero desde Seguimiento.</p> : <form onSubmit={handleSubmit}>
        <label>Vehículo
          <select required value={vehicleId} disabled={loadingVehicles || !vehicles.length} onChange={(event) => setVehicleId(event.target.value)}>
            {loadingVehicles && <option value="">Cargando vehículos…</option>}
            {!loadingVehicles && vehicles.map((vehicle) => <option key={vehicle.id} value={vehicle.id}>{vehicle.plate} · {vehicle.model}</option>)}
          </select>
        </label>
        <label>Clave API del vehículo
          <input required type="password" autoComplete="off" value={apiKey} onChange={(event) => setApiKey(event.target.value)} placeholder="Pega la clave que apareció al registrar el vehículo" />
        </label>
        <div className="iot-grid">
          <label>Latitud
            <input required type="number" min="-90" max="90" step="0.000001" value={latitude} onChange={(event) => setLatitude(event.target.value)} />
          </label>
          <label>Longitud
            <input required type="number" min="-180" max="180" step="0.000001" value={longitude} onChange={(event) => setLongitude(event.target.value)} />
          </label>
          <label>Velocidad (km/h)
            <input required type="number" min="0" step="0.01" value={speedKmh} onChange={(event) => setSpeedKmh(event.target.value)} />
          </label>
        </div>
        <p className="iot-hint">Los valores iniciales representan una ubicación de demostración cerca de Bucaramanga. La hora se registra automáticamente.</p>
        <button className="iot-primary" type="submit" disabled={sending || loadingVehicles || !vehicleId}>{sending ? 'Enviando…' : 'Enviar lectura'}</button>
      </form>}
    </section>
  </main>;
}
