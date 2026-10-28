import { useState } from 'react';
import { useAuth } from '../../shared/context/AuthContext';
import UIFactoryProvider from '../../shared/factories/ui/UIFactoryProvider';
import './Dashboard.css';

function Dashboard() {
  const { role } = useAuth();

  const uiFactory = role
    ? UIFactoryProvider.getFactory(role)
    : null;

  const navigation = uiFactory
    ? uiFactory.createNavigation()
    : null;

  const [openMenu, setOpenMenu] = useState(null);

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

            <div>
              <strong>Usuario</strong>
              <span>{role || 'Usuario'}</span>
            </div>
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
                <strong>--</strong>
              </div>
            </article>

            <article className="dashboard-card">
              <span className="dashboard-card-icon">◉</span>

              <div>
                <p>Proveedores</p>
                <strong>--</strong>
              </div>
            </article>

            <article className="dashboard-card">
              <span className="dashboard-card-icon">▤</span>

              <div>
                <p>Inventario</p>
                <strong>--</strong>
              </div>
            </article>

            <article className="dashboard-card">
              <span className="dashboard-card-icon">□</span>

              <div>
                <p>Pedidos</p>
                <strong>--</strong>
              </div>
            </article>
          </section>

          <section className="dashboard-panels">
            <article className="dashboard-panel">
              <div className="panel-header">
                <h3>Resumen</h3>
                <span>Información general</span>
              </div>

              <div className="panel-empty">
                <p>Los datos se mostrarán cuando conectemos los módulos.</p>
              </div>
            </article>

            <article className="dashboard-panel">
              <div className="panel-header">
                <h3>Actividad reciente</h3>
                <span>Últimos movimientos</span>
              </div>

              <div className="panel-empty">
                <p>No hay actividad para mostrar.</p>
              </div>
            </article>
          </section>
        </main>
      </section>
    </div>
  );
}

export default Dashboard;