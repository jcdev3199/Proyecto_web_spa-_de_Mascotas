import { reactive } from 'vue';

export const authState = reactive({
  user: JSON.parse(localStorage.getItem('user')) || null,
  token: localStorage.getItem('token') || null,

  setAuth(data) {
    this.user = {
      id_usuario: data.id_usuario,
      email: data.email,
      username: data.username,
      nombre: data.nombre, // Aseguramos capturar el nombre para el Dashboard
      rol: data.rol
    };
    this.token = data.token;
    
    localStorage.setItem('user', JSON.stringify(this.user));
    localStorage.setItem('token', this.token);
    console.log(">>> [DEBUG STORE] Sesión persistida para:", this.user.email || this.user.nombre);
  },

  // Cambiado de clearAuth a logout para coincidir con el Dashboard
  logout() {
    console.log(">>> [DEBUG STORE] Ejecutando cierre de sesión...");
    this.user = null;
    this.token = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.clear(); 
    console.log(">>> [DEBUG STORE] Almacenamiento local limpiado completamente.");
  }
});