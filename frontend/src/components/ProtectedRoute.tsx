import React from "react";
import { Navigate, Outlet } from "react-router-dom";

const ProtectedRoute: React.FC<{ isLoggedIn: boolean }> = ({ isLoggedIn }) => {
  return !isLoggedIn ? <Navigate to="/login" /> : <Outlet />;
};

export default ProtectedRoute;
