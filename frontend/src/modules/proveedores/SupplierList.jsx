import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/auth';
import {
  createSupplier,
  fetchSuppliers,
  updateSupplier,
} from './supplierService';
import './SupplierList.css';

const emptyForm = {
  nit: '',
  name: '',
  phone: '',
  email: '',
  status: 'ACTIVE',
};

function SupplierList() {
  const { token, role } = useAuth();
  const navigate = useNavigate();
  const [suppliers, setSuppliers] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [searchName, setSearchName] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState(null);
  const [formData, setFormData] = useState({ ...emptyForm });
  const [saving, setSaving] = useState(false);
  const [updatingSupplierId, setUpdatingSupplierId] = useState(null);
  const [formError, setFormError] = useState('');
  const [actionError, setActionError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const canManageSuppliers = role === 'PRESIDENT' || role === 'ROLE_PRESIDENT';

  useEffect(() => {
    let isCurrentRequest = true;

    async function loadSuppliers() {
      setLoading(true);
      setError('');

      try {
        const result = await fetchSuppliers({
          token,
          page,
          size: 10,
          name: searchName,
        });

        if (isCurrentRequest) {
          setSuppliers(result.content || []);
          setTotalPages(result.totalPages || 0);
        }
      } catch {
        if (isCurrentRequest) setError('No fue posible cargar los proveedores. Inténtalo de nuevo.');
      } finally {
        if (isCurrentRequest) setLoading(false);
      }
    }

    loadSuppliers();

    return () => {
      isCurrentRequest = false;
    };
  }, [token, page, searchName, refreshKey]);

  function handleSearch(event) {
    event.preventDefault();
    setPage(0);
    setSearchName(searchText.trim());
  }

  function openNewForm() {
    setEditingSupplier(null);
    setFormData({ ...emptyForm });
    setFormError('');
    setSuccessMessage('');
    setShowForm(true);
  }

  function openEditForm(supplier) {
    setEditingSupplier(supplier);
    setFormData({
      nit: supplier.nit || '',
      name: supplier.name || '',
      phone: supplier.phone || '',
      email: supplier.email || '',
      status: supplier.status || 'ACTIVE',
    });
    setFormError('');
    setSuccessMessage('');
    setShowForm(true);
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
    setFormError('');
  }

  async function handleSaveSupplier(event) {
    event.preventDefault();
    setSaving(true);
    setFormError('');

    const supplier = {
      nit: formData.nit.trim(),
      name: formData.name.trim(),
      phone: formData.phone.trim(),
      email: formData.email.trim(),
      ...(editingSupplier ? { status: editingSupplier.status } : {}),
    };

    try {
      if (editingSupplier) {
        await updateSupplier({ token, id: editingSupplier.id, supplier });
      } else {
        await createSupplier({ token, supplier });
      }
      setShowForm(false);
      setEditingSupplier(null);
      setFormData({ ...emptyForm });
      setSearchText('');
      setSearchName('');
      setPage(0);
      setRefreshKey((current) => current + 1);
      setSuccessMessage(editingSupplier ? 'Proveedor actualizado correctamente.' : 'Proveedor registrado correctamente.');
    } catch (requestError) {
      const detail = requestError.response?.data?.detail
        || requestError.response?.data?.message;
      setFormError(detail || 'No fue posible guardar el proveedor. Revisa los datos e inténtalo de nuevo.');
    } finally {
      setSaving(false);
    }
  }

  async function handleToggleSupplierStatus(supplier) {
    const activating = supplier.status !== 'ACTIVE';
    const nextStatus = activating ? 'ACTIVE' : 'DELETED';
    const action = activating ? 'activar' : 'desactivar';
    if (!activating && !window.confirm('¿Desactivar a "' + supplier.name + '"? Se conservarán su historial y sus relaciones.')) return;

    setActionError('');
    setSuccessMessage('');
    setUpdatingSupplierId(supplier.id);
    try {
      await updateSupplier({
        token,
        id: supplier.id,
        supplier: {
          nit: supplier.nit,
          name: supplier.name,
          phone: supplier.phone,
          email: supplier.email,
          status: nextStatus,
        },
      });
      setRefreshKey((current) => current + 1);
      setSuccessMessage('Proveedor ' + (activating ? 'activado' : 'desactivado') + ' correctamente.');
    } catch (requestError) {
      const detail = requestError.response?.data?.detail
        || requestError.response?.data?.message;
      setActionError(detail || 'No fue posible ' + action + ' el proveedor.');
    } finally {
      setUpdatingSupplierId(null);
    }
  }
  return (
    <main className="suppliers-page">
      <header className="suppliers-header">
        <div>
          <button
            className="suppliers-back-button"
            type="button"
            onClick={() => navigate('/dashboard')}
          >
            ← Volver al dashboard
          </button>
          <h1>Proveedores</h1>
          <p>Consulta y registra los proveedores de LogiSync.</p>
        </div>
        {canManageSuppliers && (
          <button className="suppliers-primary-button" type="button" onClick={openNewForm}>
            Nuevo proveedor
          </button>
        )}
      </header>

      {successMessage && <p className="suppliers-success" role="status">{successMessage}</p>}
      {actionError && <p className="suppliers-error" role="alert">{actionError}</p>}

      {showForm && canManageSuppliers && (
        <section className="suppliers-form-card" aria-labelledby="supplier-form-title">
          <h2 id="supplier-form-title">{editingSupplier ? 'Actualizar proveedor' : 'Registrar proveedor'}</h2>
          {formError && <p className="suppliers-error" role="alert">{formError}</p>}
          <form onSubmit={handleSaveSupplier}>
            <div className="suppliers-form-grid">
              <label>
                NIT *
                <input name="nit" value={formData.nit} onChange={handleChange} maxLength="10" required />
              </label>
              <label>
                Nombre *
                <input name="name" value={formData.name} onChange={handleChange} maxLength="100" required />
              </label>
              <label>
                Teléfono *
                <input name="phone" value={formData.phone} onChange={handleChange} maxLength="12" required />
              </label>
              <label>
                Correo electrónico *
                <input name="email" type="email" value={formData.email} onChange={handleChange} maxLength="30" required />
              </label>
            </div>
            <div className="suppliers-form-actions">
              <button className="suppliers-primary-button" type="submit" disabled={saving}>
                {saving ? 'Guardando…' : editingSupplier ? 'Guardar cambios' : 'Guardar proveedor'}
              </button>
              <button
                className="suppliers-secondary-button"
                type="button"
                onClick={() => {
                  setShowForm(false);
                  setEditingSupplier(null);
                }}
                disabled={saving}
              >
                Cancelar
              </button>
            </div>
          </form>
        </section>
      )}

      <form className="suppliers-search" onSubmit={handleSearch}>
        <label htmlFor="supplier-search">Buscar por nombre</label>
        <div className="suppliers-search-controls">
          <input
            id="supplier-search"
            type="search"
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
            placeholder="Escribe el nombre de un proveedor"
          />
          <button className="suppliers-primary-button" type="submit">Buscar</button>
        </div>
      </form>

      <section className="suppliers-table-card" aria-live="polite">
        {loading ? (
          <p className="suppliers-message">Cargando proveedores…</p>
        ) : error ? (
          <p className="suppliers-message suppliers-error">{error}</p>
        ) : suppliers.length === 0 ? (
          <p className="suppliers-message">No se encontraron proveedores.</p>
        ) : (
          <div className="suppliers-table-scroll">
            <table className="suppliers-table">
              <thead>
                <tr>
                  <th scope="col">NIT</th>
                  <th scope="col">Nombre</th>
                  <th scope="col">Teléfono</th>
                  <th scope="col">Correo</th>
                  <th scope="col">Estado</th>
                  {canManageSuppliers && <th scope="col">Acciones</th>}
                </tr>
              </thead>
              <tbody>
                {suppliers.map((supplier) => (
                  <tr key={supplier.id}>
                    <td>{supplier.nit || '—'}</td>
                    <td>{supplier.name || '—'}</td>
                    <td>{supplier.phone || '—'}</td>
                    <td>{supplier.email || '—'}</td>
                    <td>{supplier.status === 'ACTIVE' ? 'Activo' : 'Inactivo'}</td>
                    {canManageSuppliers && (
                      <td>
                        <div className="suppliers-row-actions">
                          <button type="button" onClick={() => openEditForm(supplier)}>
                            Editar
                          </button>
                          <button
                            className={supplier.status === 'ACTIVE' ? 'suppliers-danger-button' : ''}
                            type="button"
                            onClick={() => handleToggleSupplierStatus(supplier)}
                            disabled={updatingSupplierId === supplier.id}
                          >
                            {updatingSupplierId === supplier.id
                              ? 'Guardando…'
                              : supplier.status === 'ACTIVE' ? 'Desactivar' : 'Activar'}
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <footer className="suppliers-pagination">
        <span>Página {totalPages === 0 ? 0 : page + 1} de {totalPages}</span>
        <div>
          <button
            className="suppliers-primary-button"
            type="button"
            onClick={() => setPage((currentPage) => Math.max(0, currentPage - 1))}
            disabled={page === 0 || loading}
          >
            Anterior
          </button>
          <button
            className="suppliers-primary-button"
            type="button"
            onClick={() => setPage((currentPage) => currentPage + 1)}
            disabled={totalPages === 0 || page + 1 >= totalPages || loading}
          >
            Siguiente
          </button>
        </div>
      </footer>
    </main>
  );
}

export default SupplierList;





