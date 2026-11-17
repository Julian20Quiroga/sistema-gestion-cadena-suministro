import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import ProductTablePrototype from '../../shared/prototypes/ProductTablePrototype';
import {
  deleteProduct,
  fetchProducts,
} from './productService';
import ProductCreateForm from './ProductCreateForm';
import './ProductList.css';

const tableConfig = new ProductTablePrototype().clone().config;
const currencyFormatter = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 2,
});

function ProductList() {
  const { token, role } = useAuth();
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [searchName, setSearchName] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);
  const [successMessage, setSuccessMessage] = useState('');
  const [actionError, setActionError] = useState('');

  const canManageProducts = role === 'PRESIDENT' || role === 'ROLE_PRESIDENT';

  useEffect(() => {
    let isCurrentRequest = true;

    async function loadProducts() {
      setLoading(true);
      setError('');

      try {
        const result = await fetchProducts({
          token,
          page,
          size: 10,
          name: searchName,
        });

        if (isCurrentRequest) {
          setProducts(result.content || []);
          setTotalPages(result.totalPages || 0);
        }
      } catch {
        if (isCurrentRequest) {
          setError('No fue posible cargar los productos. Revisa la conexión con el backend e inténtalo de nuevo.');
        }
      } finally {
        if (isCurrentRequest) setLoading(false);
      }
    }

    loadProducts();

    return () => {
      isCurrentRequest = false;
    };
  }, [token, page, searchName, refreshKey]);

  function handleSearch(event) {
    event.preventDefault();
    setPage(0);
    setSearchName(searchText.trim());
  }

  function openCreateForm() {
    setSelectedProduct(null);
    setShowCreateForm(true);
    setSuccessMessage('');
    setActionError('');
  }

  function openEditForm(product) {
    setSelectedProduct(product);
    setShowCreateForm(true);
    setSuccessMessage('');
    setActionError('');
  }

  async function handleDeleteProduct(product) {
    const confirmed = window.confirm(
      '¿Eliminar definitivamente el producto "' + product.name + '"? Si tiene vínculos con inventario u órdenes, no se podrá borrar.'
    );
    if (!confirmed) return;

    setActionError('');
    setSuccessMessage('');
    try {
      await deleteProduct({ token, id: product.id });
      setRefreshKey((current) => current + 1);
      setSuccessMessage('Producto eliminado definitivamente.');
    } catch (requestError) {
      const detail = requestError.response?.data?.detail
        || requestError.response?.data?.message;
      setActionError(requestError.response?.status === 409
        ? 'No se puede borrar: el producto todavía está relacionado con inventario u órdenes.'
        : detail || 'No fue posible eliminar el producto.');
    }
  }

  function handleSaved(message) {
    setShowCreateForm(false);
    setSelectedProduct(null);
    setSearchText('');
    setSearchName('');
    setPage(0);
    setRefreshKey((current) => current + 1);
    setSuccessMessage(message);
  }

  const columnRenderers = {
    SKU: (product) => product.sku || '—',
    'Descripción': (product) => product.name || '—',
    Modelo: (product) => product.model || '—',
    Precio: (product) => currencyFormatter.format(Number(product.salePrice || 0)),
    Estado: (product) => product.status || '—',
  };

  return (
    <main className="products-page">
      <header className="products-header">
        <div>
          <button
            className="products-back-button"
            type="button"
            onClick={() => navigate('/dashboard')}
          >
            ← Volver al dashboard
          </button>
          <h1>Productos</h1>
          <p>Consulta y registra productos en LogiSync.</p>
        </div>
        {canManageProducts && (
          <button className="products-primary-button" type="button" onClick={openCreateForm}>
            Nuevo producto
          </button>
        )}
      </header>

      {successMessage && <p className="products-form-success" role="status">{successMessage}</p>}
      {actionError && <p className="products-form-error" role="alert">{actionError}</p>}

      {showCreateForm && canManageProducts && (
        <ProductCreateForm
          key={selectedProduct ? selectedProduct.id : 'new'}
          token={token}
          productToEdit={selectedProduct}
          onCancel={() => {
            setShowCreateForm(false);
            setSelectedProduct(null);
          }}
          onSaved={handleSaved}
        />
      )}

      <form className="products-search" onSubmit={handleSearch}>
        <label htmlFor="product-search">Buscar por nombre</label>
        <div className="products-search-controls">
          <input
            id="product-search"
            type="search"
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
            placeholder="Escribe el nombre de un producto"
          />
          <button type="submit">Buscar</button>
        </div>
      </form>

      <section className="products-table-card" aria-live="polite">
        {loading ? (
          <p className="products-message">Cargando productos…</p>
        ) : error ? (
          <p className="products-message products-error">{error}</p>
        ) : products.length === 0 ? (
          <p className="products-message">No se encontraron productos.</p>
        ) : (
          <div className="products-table-scroll">
            <table className="products-table">
              <thead>
                <tr>
                  {tableConfig.columns.map((column) => (
                    <th key={column} scope="col">{column}</th>
                  ))}
                  {canManageProducts && <th scope="col">Acciones</th>}
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id}>
                    {tableConfig.columns.map((column) => (
                      <td key={column}>{columnRenderers[column](product)}</td>
                    ))}
                    {canManageProducts && (
                      <td>
                        <div className="products-row-actions">
                          <button type="button" onClick={() => openEditForm(product)}>
                            Editar
                          </button>
                          <button
                            className="products-danger-button"
                            type="button"
                            onClick={() => handleDeleteProduct(product)}
                          >
                            Eliminar
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

      <footer className="products-pagination">
        <span>Página {totalPages === 0 ? 0 : page + 1} de {totalPages}</span>
        <div>
          <button
            type="button"
            onClick={() => setPage((currentPage) => Math.max(0, currentPage - 1))}
            disabled={page === 0 || loading}
          >
            Anterior
          </button>
          <button
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

export default ProductList;



