import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // <--- Esto conecta tus páginas
import './style.css'

const app = createApp(App)
app.use(router) // <--- Esto activa la navegación
app.mount('#app')