import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { wsClient } from "../config/WSClient";

const ProtectedRoute: React.FC<{ isLoggedIn: boolean }> = ({ isLoggedIn }) => {
  if (isLoggedIn) {
    wsClient.init({
      token: JSON.parse(localStorage.getItem("user") || "{}").token as any,
      uri: window.location.origin.replace("http", "ws") + "/appws",
    });
  }
  return !isLoggedIn ? <Navigate to="/login" /> : <Outlet />;
};

export default ProtectedRoute;
