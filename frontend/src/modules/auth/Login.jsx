import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import { loginRequest } from './authService';
import '../../App.css';

function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [credentials, setCredentials] = useState({
    username: '',
    password: '',
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setCredentials((previousCredentials) => ({
      ...previousCredentials,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError('');

    if (!credentials.username || !credentials.password) {
      setError('Ingrese usuario y contraseña.');
      return;
    }

    try {
      setLoading(true);

      const response = await loginRequest(credentials);

      login(response);

      navigate('/dashboard');
    } catch (error) {
      console.error('Error completo del login:', error);

      if (error.response) {
        console.error('Status:', error.response.status);
        console.error('Respuesta del backend:', error.response.data);
      } else if (error.request) {
        console.error('El backend no respondió:', error.request);
      } else {
        console.error('Error:', error.message);
      }

      setError('No fue posible iniciar sesión.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-brand">
        <div className="brand-content">
          <div className="brand-logo">
            <div className="cube-top"></div>
            <div className="cube-left"></div>
            <div className="cube-right"></div>
          </div>

          <h1>
            Logi<span>Sync</span>
          </h1>

          <div className="brand-line"></div>

          <p>Sistema de gestión de</p>
          <p>cadena de suministro</p>
        </div>

        <div className="brand-decoration">
          <div className="decoration-shape shape-one"></div>
          <div className="decoration-shape shape-two"></div>
          <div className="decoration-shape shape-three"></div>
        </div>
      </section>

      <section className="login-form-section">
        <div className="login-card">
          <div className="login-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <circle cx="12" cy="8" r="3.5" />
              <path d="M5.5 20c.8-3.2 3-5 6.5-5s5.7 1.8 6.5 5" />
            </svg>
          </div>

          <h2>Iniciar sesión</h2>

          <p className="login-description">
            Ingresa tus credenciales para acceder
            <br />
            al sistema.
          </p>

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="username">Usuario</label>

              <div className="input-container">
                <svg
                  className="input-icon"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <circle cx="12" cy="8" r="3.5" />
                  <path d="M5.5 20c.8-3.2 3-5 6.5-5s5.7 1.8 6.5 5" />
                </svg>

                <input
                  id="username"
                  name="username"
                  type="text"
                  placeholder="Ingresa tu usuario"
                  value={credentials.username}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="password">Contraseña</label>

              <div className="input-container">
                <svg
                  className="input-icon"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <rect x="5" y="10" width="14" height="10" rx="2" />
                  <path d="M8 10V7a4 4 0 0 1 8 0v3" />
                  <circle cx="12" cy="15" r="1" />
                </svg>

                <input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="Ingresa tu contraseña"
                  value={credentials.password}
                  onChange={handleChange}
                />
              </div>
            </div>

            {error && <p className="login-error">{error}</p>}

            <button
              type="submit"
              className="login-button"
              disabled={loading}
            >
              {loading ? 'Ingresando...' : 'Iniciar sesión'}
            </button>
          </form>
        </div>
      </section>
    </main>
  );
}

export default Login;