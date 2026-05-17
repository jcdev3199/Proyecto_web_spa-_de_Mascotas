<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 p-4">
    <div class="bg-white p-8 rounded-3xl shadow-xl max-w-md w-full text-center">
      <div v-if="status === 'loading'" class="animate-pulse">
        <div class="text-4xl mb-4">⏳</div>
        <h2 class="text-xl font-bold text-gray-700">Verificando tu cuenta...</h2>
      </div>

      <div v-if="status === 'success'">
        <div class="text-4xl mb-4">✅</div>
        <h2 class="text-2xl font-black text-green-600">¡Cuenta Activada!</h2>
        <p class="text-gray-500 mt-2">Tu identidad ha sido confirmada. Ya puedes ingresar al sistema.</p>
        <router-link to="/login" class="mt-6 inline-block bg-blue-600 text-white px-6 py-2 rounded-xl font-bold">Ir al Login</router-link>
      </div>

      <div v-if="status === 'error'">
        <div class="text-4xl mb-4">❌</div>
        <h2 class="text-2xl font-black text-red-600">Link Expirado</h2>
        <p class="text-gray-500 mt-2">El token ha caducado (superó los 15 minutos) o es inválido.</p>
        <button @click="solicitarNuevo" class="mt-6 text-blue-600 font-bold underline">Solicitar nuevo link</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from '../services/axiosConfig';

const route = useRoute();
const status = ref('loading');

onMounted(async () => {
  try {
    const token = route.query.token;
    await axios.get(`http://localhost:8080/api/auth/verify?token=${token}`);
    status.value = 'success';
  } catch (err) {
    status.value = 'error';
  }
});
</script>