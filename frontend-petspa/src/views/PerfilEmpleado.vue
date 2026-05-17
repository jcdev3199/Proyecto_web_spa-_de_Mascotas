<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-2xl mx-auto">
      <header class="flex justify-between items-center mb-8">
        <div>
          <button @click="$router.push('/dashboard')" class="text-blue-600 font-bold text-xs mb-2 hover:underline uppercase">
            ← Volver al Panel
          </button>
          <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">
            Perfil de {{ perfil.rol }}
          </h2>
        </div>

        <div class="flex gap-2">
          <button 
            @click="solicitarCambioPassword"
            class="bg-orange-50 text-orange-600 px-4 py-2 rounded-xl text-[10px] font-black hover:bg-orange-100 border border-orange-200 transition-all"
          >
            🔑 CAMBIAR CONTRASEÑA
          </button>
          
          <button 
            v-if="!modoEdicion"
            @click="modoEdicion = true"
            class="bg-blue-600 text-white px-6 py-2 rounded-xl text-xs font-black hover:bg-blue-700 shadow-lg transition-all"
          >
            MODIFICAR
          </button>
        </div>
      </header>

      <form @submit.prevent="actualizarPerfil" class="bg-white p-8 rounded-3xl shadow-xl border border-gray-100 space-y-6">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Nombre(s)</label>
            <input v-model="perfil.nombres" :disabled="!modoEdicion" type="text" 
              class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-blue-400 outline-none text-sm transition-all disabled:opacity-70">
          </div>
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Apellido(s)</label>
            <input v-model="perfil.apellidos" :disabled="!modoEdicion" type="text" 
              class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-blue-400 outline-none text-sm transition-all disabled:opacity-70">
          </div>
        </div>

        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Teléfono de Contacto</label>
          <input v-model="perfil.telefono" :disabled="!modoEdicion" type="text" 
            class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-blue-400 outline-none text-sm transition-all disabled:opacity-70">
        </div>

        <div class="bg-blue-50/50 p-4 rounded-2xl border border-blue-100 grid grid-cols-3 gap-3">
          <div>
            <label class="block text-[10px] font-black text-blue-500 uppercase mb-1 ml-1">Turno</label>
            <input v-model="perfil.turno" disabled type="text" class="w-full p-3 bg-white rounded-xl border-none outline-none text-sm font-bold text-blue-600">
          </div>
          <div>
            <label class="block text-[10px] font-black text-blue-500 uppercase mb-1 ml-1">Entrada</label>
            <input v-model="perfil.horaEntrada" disabled type="time" class="w-full p-3 bg-white rounded-xl border-none outline-none text-sm font-bold text-blue-600">
          </div>
          <div>
            <label class="block text-[10px] font-black text-blue-500 uppercase mb-1 ml-1">Salida</label>
            <input v-model="perfil.horaSalida" disabled type="time" class="w-full p-3 bg-white rounded-xl border-none outline-none text-sm font-bold text-blue-600">
          </div>
        </div>

        <div v-if="perfil.rol === 'Groomer'" class="space-y-4 pt-4 border-t border-gray-100">
          <h4 class="text-[10px] font-black text-gray-400 uppercase">Información Profesional</h4>
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Especialidad Técnica</label>
            <input v-model="perfil.especialidad" :disabled="!modoEdicion" type="text" 
              class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-blue-400 outline-none text-sm transition-all disabled:opacity-70">
          </div>
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Capacidad Simultánea</label>
            <input v-model="perfil.capacidadSimultanea" :disabled="!modoEdicion" type="number" 
              class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-blue-400 outline-none text-sm transition-all disabled:opacity-70">
          </div>
        </div>

        <div v-if="modoEdicion" class="pt-4 flex gap-3">
          <button type="submit" :disabled="loading" 
            class="flex-1 py-4 bg-gray-900 text-white rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl hover:bg-blue-600 transition-all">
            {{ loading ? 'Sincronizando...' : 'Guardar Cambios' }}
          </button>
          <button @click="modoEdicion = false" type="button" class="px-6 py-4 bg-gray-100 text-gray-500 rounded-2xl font-black text-xs uppercase">
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
  nombres: '', apellidos: '', telefono: '', 
  turno: '', horaEntrada: '', horaSalida: '',
  rol: '', especialidad: '', capacidadSimultanea: 1
});

const cargarDatos = async () => {
  const id = authState.user?.id_usuario;
  if (!id) {
    console.error(">>> [ERROR] No se encontró el ID del usuario en el store.");
    return;
  }

  try {
    // Usamos la ruta completa para asegurar la conexión con el EmpleadoController
    const res = await axios.get(`http://localhost:8080/api/empleado/perfil/${id}`);
    perfil.value = res.data;
    console.log(">>> [DEBUG] Datos de empleado cargados correctamente:", res.data);
  } catch (err) {
    console.error(">>> [ERROR] Fallo al cargar perfil de empleado:", err.response?.data || err.message);
  }
};

const solicitarCambioPassword = async () => {
  if(!confirm("Se enviará un link a tu correo para que establezcas una nueva contraseña. ¿Continuar?")) return;
  
  try {
    await axios.post(`/api/auth/solicitar-reset-password`, { email: authState.user.email });
    alert("✉️ Revisa tu correo institucional para cambiar tu clave.");
  } catch (err) {
    alert("Error al solicitar el cambio: " + (err.response?.data || "Servidor no disponible"));
  }
};

const actualizarPerfil = async () => {
  loading.value = true;
  try {
    const id = authState.user.id_usuario;
    await axios.put(`http://localhost:8080/api/empleado/perfil/${id}`, perfil.value);
    alert("✅ Perfil actualizado correctamente.");
    modoEdicion.value = false;
    await cargarDatos();
  } catch (err) {
    alert("Error al actualizar: " + (err.response?.data || "Servidor no disponible"));
  } finally {
    loading.value = false;
  }
};

onMounted(cargarDatos);
</script>