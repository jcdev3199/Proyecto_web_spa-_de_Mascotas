<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-2xl mx-auto">
      
      <header class="flex justify-between items-center mb-8">
        <div>
          <button @click="$router.push('/dashboard')" class="text-blue-600 font-bold text-xs mb-2 hover:underline">
            ← VOLVER AL PANEL
          </button>
          <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">Mi Perfil de Cliente</h2>
        </div>
        
        <div class="flex gap-2">
          <button 
            @click="solicitarCambioPassword"
            class="bg-orange-50 text-orange-600 px-6 py-2 rounded-xl text-[10px] font-black hover:bg-orange-100 border border-orange-200 transition-all uppercase"
          >
            🔑 Cambiar Contraseña
          </button>

          <button 
            v-if="!modoEdicion"
            @click="modoEdicion = true"
            class="bg-blue-50 text-blue-600 px-6 py-2 rounded-xl text-xs font-black hover:bg-blue-100 transition-all border border-blue-200"
          >
            MODIFICAR DATOS
          </button>
        </div>
      </header>

      <form @submit.prevent="actualizarPerfil" class="bg-white p-8 rounded-3xl shadow-xl border border-gray-100 space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Nombre Real</label>
            <input v-model="perfil.nombre" :disabled="!modoEdicion" type="text" class="w-full p-3 bg-gray-50 rounded-xl border-none text-sm disabled:opacity-60 disabled:cursor-not-allowed">
          </div>
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Apellido</label>
            <input v-model="perfil.apellido" :disabled="!modoEdicion" type="text" class="w-full p-3 bg-gray-50 rounded-xl border-none text-sm disabled:opacity-60">
          </div>
        </div>

        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Cédula de Identidad (CI)</label>
          <input v-model="perfil.ci" :disabled="!modoEdicion" type="text" class="w-full p-3 bg-gray-50 rounded-xl border-none text-sm font-mono disabled:opacity-60">
        </div>

        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Teléfono</label>
          <input v-model="perfil.telefono" :disabled="!modoEdicion" type="text" class="w-full p-3 bg-gray-50 rounded-xl border-none text-sm disabled:opacity-60">
        </div>

        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Dirección</label>
          <textarea v-model="perfil.direccion" :disabled="!modoEdicion" rows="2" class="w-full p-3 bg-gray-50 rounded-xl border-none text-sm disabled:opacity-60"></textarea>
        </div>

        <div v-if="modoEdicion" class="pt-4 flex gap-3">
          <button 
            type="submit" 
            :disabled="loading"
            class="flex-1 py-4 bg-green-600 text-white rounded-2xl font-black text-xs uppercase tracking-widest shadow-lg hover:bg-green-700 transition-all"
          >
            {{ loading ? 'Guardando...' : 'Confirmar y Actualizar' }}
          </button>
          <button 
            @click="modoEdicion = false" 
            type="button"
            class="px-6 py-4 bg-gray-100 text-gray-500 rounded-2xl font-black text-xs uppercase"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '../services/axiosConfig';
import { authState } from '../store';

const loading = ref(false);
const modoEdicion = ref(false);
const perfil = ref({
  nombre: '',
  apellido: '',
  ci: '',
  telefono: '',
  direccion: ''
});

const cargarDatosDesdeDB = async () => {
  const id = authState.user?.id_usuario;
  if (!id) return;
  try {
    const res = await axios.get(`/api/cliente/perfil/${id}`);
    perfil.value = res.data;
  } catch (err) {
    console.error("Error al obtener datos:", err);
  }
};

const solicitarCambioPassword = async () => {
  if(!confirm("¿Deseas recibir un enlace en tu correo para establecer una nueva contraseña?")) return;
  
  try {
    // Reutilizamos el mismo endpoint del empleado
    await axios.post('/api/auth/solicitar-reset-password', { 
      email: authState.user.email 
    });
    alert("✉️ Enlace enviado. Revisa tu bandeja de entrada (Mailinator si es prueba).");
  } catch (err) {
    alert("❌ Error: " + (err.response?.data || "No se pudo procesar la solicitud"));
  }
};

const actualizarPerfil = async () => {
  loading.value = true;
  try {
    const id = authState.user.id_usuario;
    await axios.put(`/api/cliente/perfil/${id}`, perfil.value);
    alert("✅ Perfil actualizado y movimiento registrado en auditoría.");
    modoEdicion.value = false;
  } catch (err) {
    alert("Error al actualizar: " + (err.response?.data || "Servidor no disponible"));
  } finally {
    loading.value = false;
  }
};

onMounted(cargarDatosDesdeDB);
</script>