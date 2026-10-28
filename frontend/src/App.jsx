import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';

import Login from './modules/auth/Login';
import Dashboard from './modules/dashboard/Dashboard';

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
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;