import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/auth';
import UIFactoryProvider from '../../shared/factories/ui/UIFactoryProvider';
import './Dashboard.css';
import LogoutButton from '../../shared/LogoutButton';
import { fetchDashboardData } from './dashboardService';

function Dashboard() {
  const { role, token } = useAuth();
  const navigate = useNavigate();

  const uiFactory = role
    ? UIFactoryProvider.getFactory(role)
    : null;

  const navigation = uiFactory
    ? uiFactory.createNavigation()
    : null;

  const [openMenu, setOpenMenu] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [dashboardLoading, setDashboardLoading] = useState(true);
  const [dashboardError, setDashboardError] = useState('');

  useEffect(() => {
    let current = true;
    fetchDashboardData(token)
      .then((data) => { if (current) setDashboardData(data); })
      .catch((error) => {
        if (current) setDashboardError(error.response?.data?.detail || 'No fue posible cargar los indicadores del tablero.');
      })
      .finally(() => { if (current) setDashboardLoading(false); });
    return () => { current = false; };
  }, [token]);

  const toggleMenu = (menu) => {
    setOpenMenu((currentMenu) => (currentMenu === menu ? null : menu));
  };

  const navigationIcons = {
    Dashboard: '▦',
    Distribuidor: '◈',
    Operaciones: '▣',
    'Análisis y Optimización': '◉',
    Proveedores: '◎',
    Usuarios: '♙',
  };

  const menuKeys = {
    Distribuidor: 'distribuidor',
    Operaciones: 'operaciones',
    'Análisis y Optimización': 'analisis',
  };

  const movementDetails = (movement) => (movement.details || []).map((detail) =>
    `Producto #${detail.productId} · ${detail.quantity}`
  ).join(', ') || movement.reason || movement.reference || 'Movimiento de inventario';
  const movementDate = (value) => value ? new Date(value).toLocaleString('es-CO') : 'Fecha no disponible';

  return (
    <div className="dashboard">
      <aside className="dashboard-sidebar">
        <div className="dashboard-logo">
          <h2>
            Logi<span>Sync</span>
          </h2>
        </div>

        <nav className="dashboard-navigation">
          {navigation &&
            navigation.items.map((item) => {
              if (item.type === 'group') {
                const menuKey = menuKeys[item.label];

                return (
                  <div className="navigation-group" key={item.label}>
                    <button
                      className="navigation-item navigation-group-button"
                      onClick={() => toggleMenu(menuKey)}
                    >
                      <span className="navigation-icon">
                        {navigationIcons[item.label]}
                      </span>

                      <span className="navigation-label">
                        {item.label}
                      </span>

                      <span className="navigation-arrow">
                        {openMenu === menuKey ? '⌃' : '⌄'}
                      </span>
                    </button>

                    {openMenu === menuKey && (
                      <div className="navigation-submenu">
                        {item.children.map((child) => (
                          <button
                            className="navigation-subitem"
                            key={child}
                            onClick={() => {
                              if (child === 'Productos') navigate('/productos');
                              if (child === 'Inventario') navigate('/inventario');
                              if (child === 'Pedidos') navigate('/pedidos');
                              if (child === 'Abastecimiento') navigate('/abastecimiento');
                              if (child === 'Despachos') navigate('/despachos');
                              if (child === 'Seguimiento') navigate('/seguimiento');
                              if (child === 'IoT') navigate('/iot');
                              if (child === 'Rutas') navigate('/rutas');
                              if (child === 'Predicción') navigate('/prediccion');
                              if (child === 'Optimización') navigate('/optimizacion');
                            }}
                          >
                            {child}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                );
              }

              return (
                <button
                  className={`navigation-item ${
                    item.label === 'Dashboard' ? 'active' : ''
                  }`}
                  key={item.label}
                  onClick={() => {
                    if (item.label === 'Proveedores') navigate('/proveedores');
                    if (item.label === 'Usuarios') navigate('/usuarios');
                  }}
                >
                  <span className="navigation-icon">
                    {navigationIcons[item.label]}
                  </span>

                  <span className="navigation-label">
                    {item.label}
                  </span>
                </button>
              );
            })}
        </nav>

        <div className="dashboard-sidebar-footer">
          <span>Rol</span>
          <strong>{role || 'Usuario'}</strong>
        </div>
      </aside>

      <section className="dashboard-content">
        <header className="dashboard-header">
          <div>
            <h1>Dashboard</h1>
            <p>Resumen general del sistema</p>
          </div>

          <div className="dashboard-user">
            <div className="dashboard-user-icon">U</div>

            <div className="dashboard-user-details">
              <strong>Usuario</strong>
              <span>{role || 'Usuario'}</span>
            </div>

            <LogoutButton />
          </div>
        </header>

        <main className="dashboard-main">
          <section className="dashboard-welcome">
            <h2>Bienvenido a LogiSync</h2>

            <p>
              Consulta y gestiona la información de tu cadena de suministro
              desde este panel.
            </p>
          </section>

          <section className="dashboard-cards">
            <article className="dashboard-card">
              <span className="dashboard-card-icon">▣</span>

              <div>
                <p>Productos</p>
                <strong>{dashboardLoading ? '…' : dashboardData?.metrics.products ?? '—'}</strong>
              </div>
            </article>

            <article className="dashboard-card">
              <span className="dashboard-card-icon">◉</span>

              <div>
                <p>Proveedores</p>
                <strong>{dashboardLoading ? '…' : dashboardData?.metrics.suppliers ?? '—'}</strong>
              </div>
            </article>

            <article className="dashboard-card">
              <span className="dashboard-card-icon">▤</span>

              <div>
                <p>Unidades disponibles</p>
                <strong>{dashboardLoading ? '…' : dashboardData?.metrics.availableUnits ?? '—'}</strong>
              </div>
            </article>

            <article className="dashboard-card">
              <span className="dashboard-card-icon">□</span>

              <div>
                <p>Pedidos</p>
                <strong>{dashboardLoading ? '…' : dashboardData?.metrics.activeOrders ?? '—'}</strong>
              </div>
            </article>
            <article className="dashboard-card">
              <span className="dashboard-card-icon">↗</span>
              <div>
                <p>Demanda proyectada · 3 periodos</p>
                <strong>{dashboardLoading ? '…' : dashboardData?.metrics.projectedDemand ?? '—'}</strong>
              </div>
            </article>
            <article className="dashboard-card">
              <span className="dashboard-card-icon">▥</span>
              <div>
                <p>Ocupación de ubicaciones</p>
                <strong>{dashboardLoading ? '…' : `${dashboardData?.metrics.storageUsagePercent ?? '—'}%`}</strong>
              </div>
            </article>
          </section>

          {dashboardError && <p className="dashboard-data-error" role="alert">{dashboardError}</p>}

          <section className="dashboard-panels">
            <article className="dashboard-panel">
              <div className="panel-header">
                <h3>Resumen</h3>
                <span>Indicadores actuales de la operación</span>
              </div>

              <div className="dashboard-summary-list" aria-live="polite">
                <p><span>Bodegas activas</span><strong>{dashboardLoading ? '…' : dashboardData?.metrics.warehouses ?? '—'}</strong></p>
                <p><span>Órdenes de compra abiertas</span><strong>{dashboardLoading ? '…' : dashboardData?.metrics.openPurchaseOrders ?? '—'}</strong></p>
                <p><span>Despachos en curso</span><strong>{dashboardLoading ? '…' : dashboardData?.metrics.activeDispatches ?? '—'}</strong></p>
                <p><span>Pedidos entregados</span><strong>{dashboardLoading ? '…' : dashboardData?.ordersByStatus.ENTREGADO || 0}</strong></p>
                <p><span>Pedidos anulados</span><strong>{dashboardLoading ? '…' : dashboardData?.ordersByStatus.ANULADO || 0}</strong></p>
                <p><span>Vehículos activos</span><strong>{dashboardLoading ? '…' : dashboardData?.metrics.vehicles ?? '—'}</strong></p>
                <p><span>Pedidos listos para planear ruta</span><strong>{dashboardLoading ? '…' : dashboardData?.metrics.routeEligibleOrders ?? '—'}</strong></p>
                <p><span>Compra sugerida por la proyección</span><strong>{dashboardLoading ? '…' : dashboardData?.metrics.suggestedProcurement ?? '—'} unidades</strong></p>
              </div>
            </article>

            <article className="dashboard-panel">
              <div className="panel-header">
                <h3>Actividad reciente</h3>
                <span>Movimientos de inventario</span>
              </div>

              <div className="dashboard-activity-list" aria-live="polite">
                {dashboardLoading ? <p>Cargando actividad…</p> : dashboardData?.recentMovements.length ? dashboardData.recentMovements.map((movement) => (
                  <div className="dashboard-activity-row" key={movement.id}>
                    <div><strong>{movement.type || movement.movementType || 'Movimiento'}</strong><span>{movementDetails(movement)}</span></div>
                    <time>{movementDate(movement.date || movement.movementDate || movement.createdAt)}</time>
                  </div>
                )) : <p>No hay movimientos registrados.</p>}
              </div>
            </article>
          </section>
        </main>
      </section>
    </div>
  );
}

export default Dashboard;







