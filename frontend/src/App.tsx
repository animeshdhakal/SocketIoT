import React, { useEffect, useState } from "react";
import axios from "axios";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import About from "./pages/About";
import Login from "./pages/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import { AuthContextInterface, UserInterface } from "./interfaces";
import Dashboard from "./components/Dashboard";
import { Devices } from "./pages/dashboard/Devices";
import BluePrints from "./pages/dashboard/BluePrints";
import BluePrint from "./pages/dashboard/BluePrint";
import NotFound from "./pages/NotFound";

export const UserContext = React.createContext<AuthContextInterface>(
  {} as AuthContextInterface
);

axios.interceptors.request.use((config) => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  if (user.token && config.headers) {
    config.headers.Authorization = `Bearer ${user.token}`;
  }
  return config;
});

function App() {
  const [user, setUser] = useState<UserInterface>({} as UserInterface);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const localUser = localStorage.getItem("user");
    if (localUser) {
      setUser(JSON.parse(localUser));
    }
    setLoading(false);
  }, []);

  if (loading) {
    return <h1>Loading</h1>;
  }

  return (
    <UserContext.Provider value={{ user, setUser }}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<h1>SocketIoT</h1>} />
          <Route path="/login" element={<Login />} />
          <Route element={<ProtectedRoute isLoggedIn={Boolean(user.email)} />}>
            <Route path="/dashboard" element={<Dashboard />}>
              <Route path="settings" element={<About />} />
              <Route path="blueprints" element={<BluePrints />} />
              <Route path="devices" element={<Devices />} />
              <Route path="blueprint" element={<BluePrint />} />
            </Route>
          </Route>
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </UserContext.Provider>
  );
}

export default App;
