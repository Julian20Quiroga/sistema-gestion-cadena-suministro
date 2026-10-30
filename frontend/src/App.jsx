import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';

import Login from './modules/auth/Login';
import Dashboard from './modules/dashboard/Dashboard';
import ProductList from './modules/productos/ProductList';
import SupplierList from './modules/proveedores/SupplierList';
import Inventory from './modules/inventario/Inventory';
import OrderList from './modules/pedidos/OrderList';

import { AuthProvider } from './shared/context/AuthContext';
import ProtectedRoute from './shared/ProtectedRoute';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />

          <Route path="/login" element={<Login />} />

          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/productos"
            element={
              <ProtectedRoute>
                <ProductList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/proveedores"
            element={
              <ProtectedRoute>
                <SupplierList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/inventario"
            element={
              <ProtectedRoute>
                <Inventory />
              </ProtectedRoute>
            }
          />
          <Route
            path="/pedidos"
            element={
              <ProtectedRoute>
                <OrderList />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
