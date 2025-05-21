import React from 'react';
import { Navigate,useLocation } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole }) => {
  const token = localStorage.getItem('token');
  const location = useLocation();

  // Step 1: No token → Redirect to login
  if (!token) {
    return <Navigate to="/" replace />;
  }

  try {
    // Step 2: Decode the JWT (Base64 decode)
    const payload = JSON.parse(atob(token.split('.')[1]));
    const userRole = payload.role;

    // Step 3: If role is required, and doesn't match → Redirect to home
    if (userRole === 'ADMIN' && !location.pathname.startsWith('/admin')) {
      return <Navigate to="/admin" replace />;
    }
    if (requiredRole && userRole !== requiredRole) {
      return <Navigate to="/home" replace />;
    }
  
    // Step 4: Everything good → render child route
    return children;
  } catch (err) {
    console.error('JWT decoding failed:', err);
    return <Navigate to="/" replace />;
  }
};

export default ProtectedRoute;
