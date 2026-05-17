<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-4xl mx-auto">
      <header class="mb-8 flex justify-between items-center">
        <div>
          <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">Gestión de Personal</h2>
          <p class="text-xs text-gray-500 font-bold">Panel exclusivo para el Administrador</p>
        </div>
        <router-link to="/dashboard" class="bg-white px-4 py-2 rounded-xl shadow-sm border border-gray-200 text-xs font-bold text-blue-600 hover:bg-blue-50 transition-all">
          ← Volver al Panel
        </router-link>
      </header>

      <div class="bg-white p-8 rounded-3xl shadow-xl border border-gray-100">
        <h3 class="text-sm font-black text-red-500 mb-6 uppercase tracking-widest">Registrar Nueva Cuenta de Personal</h3>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Datos Comunes: Correo y Rol -->
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Email Institucional *</label>
            <input v-model="staff.email" type="email" placeholder="empleado@petspa.com" 
              class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all" />
          </div>

          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Rol Asignado *</label>
            <select v-model="staff.rol" 
              class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
              <option value="Recepción">Recepción</option>
              <option value="Groomer">Groomer</option>
            </select>
          </div>

          <!-- Datos Específicos para Personal -->
          <div v-if="staff.rol === 'Groomer'">
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Especialidad del Groomer *</label>
            <input v-model="staff.especialidad" type="text" placeholder="Ej: Estética Canina" 
              class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all" />
          </div>

          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Turno de Trabajo *</label>
            <select v-model="staff.turno" 
              class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all">
              <option value="Mañana">Turno Mañana</option>
              <option value="Tarde">Turno Tarde</option>
            </select>
          </div>

          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Teléfono de Contacto *</label>
            <input v-model="staff.telefono" type="text" placeholder="70000000" 
              class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all" />
          </div>

          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Contraseña Temporal *</label>
            <input v-model="staff.password" type="password" placeholder="Password123!" 
              class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 text-sm transition-all" />
          </div>
        </div>

        <button 
          @click="crearStaff" 
          class="w-full mt-10 bg-gray-900 text-white p-4 rounded-2xl font-black hover:bg-black transition-all active:scale-95 shadow-lg flex justify-center items-center gap-2"
        >
          <span>CREAR CUENTA INTERNA</span>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
          </svg>
        </button>
        <p class="text-[10px] text-gray-400 mt-4 text-center">
          * El personal no puede registrarse por sí mismo por motivos de seguridad institucional[cite: 2].
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import axios from '../services/axiosConfig';

// Estructura de datos obligatoria para el grupo Personal[cite: 2]
const staff = reactive({ 
  email: '', 
  password: '', // Password encriptado en el backend[cite: 2]
  rol: 'Groomer', 
  especialidad: '', 
  turno: 'Mañana', 
  telefono: '' 
});

const crearStaff = async () => {
  try {
    // Usamos la ruta relativa para que pase por el puente de axiosConfig
    await axios.post('/api/admin/create-staff', staff);
    alert("Usuario de personal creado. La contraseña se guardó de forma segura.");
  } catch (err) {
    alert("Error al crear: " + (err.response?.data || "Verifica tus permisos de administrador"));
  }
};
</script>