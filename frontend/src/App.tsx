import React, { useEffect, useState } from "react";
import axios from "axios";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import About from "./pages/About";
import Login from "./pages/Login";

let token =
  "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkaGFrYWxhbmltZXNoMzg1QGdtYWlsLmNvbSIsImV4cCI6MTY3NDM3NTg3Nn0.KaL0EMtyrFDbHTTHBqSvPSm1CPuahFQxtvEBx6IoMNYpqtONDp2pKJ9c3J4F9HntHvYyj1nAMHKWA6rql3a-Ew";

axios.interceptors.request.use((method) => {
  return method;
});

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
