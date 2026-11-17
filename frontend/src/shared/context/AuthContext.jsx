import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

function getRoleFromToken(token) {
  if (!token) {
    return null;
  }

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));

    return payload.role || null;
  } catch (error) {
    console.error('No fue posible obtener el rol del token:', error);
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [role, setRole] = useState(null);

  const login = (sessionData) => {
    const newToken = sessionData.token;
    const newRole = getRoleFromToken(newToken);

    setToken(newToken);
    setRole(newRole);
  };

  const logout = () => {
    setToken(null);
    setRole(null);
  };

  const isAuthenticated = Boolean(token);

  return (
    <AuthContext.Provider
      value={{
        token,
        role,
        isAuthenticated,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}