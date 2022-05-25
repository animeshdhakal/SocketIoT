import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import About from "./pages/About";
import Login from "./pages/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import { AuthContextInterface, UserInterface } from "./interfaces";
import Dashboard from "./components/Dashboard";
import { Devices } from "./pages/dashboard/Devices";
import BluePrints from "./pages/dashboard/BluePrints";
import BluePrint from "./pages/dashboard/BluePrint";
import NotFound from "./pages/NotFound";
import Register from "./pages/Register";
import Device from "./pages/dashboard/Device";
import { messaging, getToken, vapidKey } from "./firebase";
import OTA from "./pages/dashboard/OTA";
import GoogleLogin from "./pages/GoogleLogin";
import { wsClient } from "./config/WSClient";
import ResetPassword from "./pages/ResetPassword";
import axiosInstance from "./config/axiosInstance";

export const UserContext = React.createContext<AuthContextInterface>(
  {} as AuthContextInterface
);

axios.interceptors.request.use(async (config) => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");

  if (user.expires_in < Date.now()) {
    const res = await axiosInstance.post("/api/user/refresh", {
      refresh_token: user.refresh_token,
    });

    if (res.status === 200) {
      user.access_token = res.data.access_token;
      user.expires_in = res.data.expires_in + Date.now();

      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }

  if (user.access_token && config.headers) {
    config.headers.Authorization = `Bearer ${user.access_token}`;
  }

  return config;
});

function App() {
  const [user, setUser] = useState<UserInterface>({} as UserInterface);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const localUser = JSON.parse(localStorage.getItem("user") || "{}");

    if (localUser.expires_in < Date.now()) {
      axios
        .post("/api/user/refresh", { refresh_token: localUser.refresh_token })
        .then((res) => {
          localStorage.setItem(
            "user",
            JSON.stringify({
              ...localUser,
              expires_in: res.data.expires_in + Date.now(),
              access_token: res.data.access_token,
            })
          );
          setLoading(false);
        })
        .catch((err) => {
          setUser({} as UserInterface);
          setLoading(false);
        });
    }
    setUser(localUser);

    if (!localUser["access_token"] || Date.now() < localUser.expires_in) {
      setLoading(false);
    }

    wsClient.addEventListener("authfailed", () => {
      setUser({} as UserInterface);
      localStorage.clear();
    });

    const development: boolean =
      !process.env.NODE_ENV || process.env.NODE_ENV === "development";

    navigator.serviceWorker
      .register(development ? "/fsw.js" : "/static/fsw.js")
      .then((registration) => {
        getToken(messaging, {
          vapidKey,
          serviceWorkerRegistration: registration,
        })
          .then((val) => {
            console.log(val);
          })
          .catch((err) => {
            console.log(err);
          });
      });
    return () => {
      wsClient.removeEventListener("authfailed");
    };
  }, []);

  if (loading) {
    return <h1>Loading</h1>;
  }

  return (
    <UserContext.Provider value={{ user, setUser }}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/reset" element={<ResetPassword />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login/google" element={<GoogleLogin />} />
          <Route element={<ProtectedRoute isLoggedIn={Boolean(user.email)} />}>
            <Route path="/dashboard" element={<Dashboard />}>
              <Route path="settings" element={<About />} />
              <Route path="blueprints" element={<BluePrints />} />
              <Route path="devices" element={<Devices />} />
              <Route path="blueprint" element={<BluePrint />} />
              <Route path="device" element={<Device />} />
              <Route path="ota" element={<OTA />} />
            </Route>
          </Route>
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </UserContext.Provider>
  );
}

export default App;
