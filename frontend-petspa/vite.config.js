import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: true, // Esto ayuda a que Vite escuche en todas las interfaces
    allowedHosts: true // En algunas versiones de Vite 6, 'true' funciona mejor que 'all'
  }
})