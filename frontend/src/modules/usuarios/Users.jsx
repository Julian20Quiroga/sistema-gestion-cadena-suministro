import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/auth';
import { createUser, fetchUsers } from './usersService';
import './Users.css';
import LogoutButton from '../../shared/LogoutButton';

const errorMessage = (error) => error.response?.data?.detail || error.response?.data?.message || 'No fue posible completar la operación.';

export default function Users() {
  const { token, role } = useAuth();
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [form, setForm] = useState({ username: '', password: '', confirmPassword: '' });
  const president = ['PRESIDENT', 'ROLE_PRESIDENT'].includes(role);

  const loadUsers = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await fetchUsers({ token });
      setUsers(Array.isArray(data) ? data : []);
    } catch (requestError) {
      setError(errorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    let current = true;
    if (president) {
      fetchUsers({ token })
        .then((data) => { if (current) setUsers(Array.isArray(data) ? data : []); })
        .catch((requestError) => { if (current) setError(errorMessage(requestError)); })
        .finally(() => { if (current) setLoading(false); });
    }
    return () => { current = false; };
  }, [president, token]);

  async function submit(event) {
    event.preventDefault();
    setNotice('');
    setError('');
    if (form.password !== form.confirmPassword) {
      setError('Las contraseñas no coinciden.');
      return;
    }
    setSaving(true);
    try {
      await createUser({ token, user: { username: form.username.trim(), password: form.password, role: 'BRANCH_ADMIN' } });
      setForm({ username: '', password: '', confirmPassword: '' });
      setNotice('Cuenta de administrador creada.');
      await loadUsers();
    } catch (requestError) {
      setError(errorMessage(requestError));
    } finally {
      setSaving(false);
    }
  }

  return <main className="users-page">
    <header className="users-header">
      <div><button className="users-back" onClick={() => navigate('/dashboard')}>← Volver al dashboard</button><h1>Usuarios</h1><p>Administra las cuentas de acceso de LogiSync.</p></div>
      <div className="users-header-actions"><button onClick={loadUsers} disabled={!president || loading}>Actualizar</button><LogoutButton /></div>
    </header>
    {!president ? <section className="users-card"><p className="users-error">Este módulo está disponible únicamente para el presidente.</p></section> : <>
      {notice && <div className="users-notice">{notice}</div>}
      {error && <div className="users-error users-alert">{error}</div>}
      <section className="users-card">
        <h2>Crear administrador de sucursal</h2>
        <p className="users-muted">La contraseña se guarda de forma segura y no se muestra en el listado.</p>
        <form className="users-form" onSubmit={submit}>
          <label>Nombre de usuario<input required minLength="3" maxLength="100" autoComplete="username" value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} /></label>
          <label>Contraseña<input required minLength="8" maxLength="72" type="password" autoComplete="new-password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} /></label>
          <label>Confirmar contraseña<input required minLength="8" maxLength="72" type="password" autoComplete="new-password" value={form.confirmPassword} onChange={(event) => setForm({ ...form, confirmPassword: event.target.value })} /></label>
          <button className="users-primary" disabled={saving}>{saving ? 'Creando…' : 'Crear cuenta'}</button>
        </form>
      </section>
      <section className="users-card">
        <h2>Cuentas registradas</h2>
        {loading ? <p>Cargando usuarios…</p> : users.length === 0 ? <p className="users-muted">No hay cuentas para mostrar.</p> : <div className="users-table-wrap"><table className="users-table"><thead><tr><th>Usuario</th><th>Rol</th><th>Estado</th></tr></thead><tbody>{users.map((user) => <tr key={user.username}><td>{user.username}</td><td>{user.role === 'PRESIDENT' ? 'Presidente' : 'Administrador de sucursal'}</td><td>{user.status === 'ACTIVE' ? 'Activo' : 'Inactivo'}</td></tr>)}</tbody></table></div>}
      </section>
    </>}
  </main>;
}
