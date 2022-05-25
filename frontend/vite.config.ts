import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:4444",
      },
      "/appws": {
        target: "ws://localhost:4444",
        ws: true,
        changeOrigin: true,
      },
    },
  },
  publicDir: "public",
  build: {
    outDir: "build",
    assetsDir: "static",
  },
  plugins: [react()],
});
