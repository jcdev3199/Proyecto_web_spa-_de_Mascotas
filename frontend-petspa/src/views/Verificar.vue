<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gray-50 p-4">
    <div class="max-w-md w-full bg-white p-8 rounded-lg shadow-md text-center">
      <h1 class="text-2xl font-bold text-gray-800 mb-4">Activación de Cuenta</h1>
      
      <div v-if="cargando">
        <p class="text-blue-600 animate-pulse">Procesando tu activación, por favor espera...</p>
      </div>

      <div v-else>
        <p :class="exito ? 'text-green-600' : 'text-red-600'" class="text-lg font-medium mb-6">
          {{ mensaje }}
        </p>
        <button @click="irAlLogin" class="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition">
          Ir al Inicio de Sesión
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from '../services/axiosConfig'; // Usamos tu configuración con el switch

const route = useRoute();
const router = useRouter();
const cargando = ref(true);
const mensaje = ref('');
const exito = ref(false);

onMounted(async () => {
  const token = route.query.token;
  
  if (!token) {
    mensaje.value = "No se encontró un token de activación válido.";
    cargando.value = false;
    return;
  }

  try {
    // Llamada silenciosa al backend (Heather) desde el frontend (Eminem)
    const response = await axios.get(`/api/auth/verificar?token=${token}`);
    mensaje.value = response.data;
    exito.value = true;
  } catch (error) {
    mensaje.value = error.response?.data || "Hubo un error al activar tu cuenta. El link podría haber expirado.";
    exito.value = false;
  } finally {
    cargando.value = false;
  }
});

const irAlLogin = () => router.push('/login');
</script>