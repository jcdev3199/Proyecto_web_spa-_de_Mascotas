import axios from 'axios';
import { authState } from '../store';

// --- LÓGICA DEL SWITCH AUTOMÁTICO ---
const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';

// ACTUALIZADO CON TU NUEVO LINK DE BACKEND
const backendURL = isLocal 
    ? 'http://localhost:8080' 
    : 'https://cord-assured-artists-believed.trycloudflare.com';

axios.defaults.baseURL = backendURL;
axios.defaults.withCredentials = true; 

// --- INTERCEPTORES ---
axios.interceptors.request.use((config) => {  
  const token = authState.token || authState.user?.token || localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default axios;