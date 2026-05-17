<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-4">
    <div class="max-w-md w-full bg-white p-8 rounded-xl shadow-lg border-t-4 border-blue-600">
      <h2 class="text-2xl font-bold text-center text-gray-800 mb-2">Activa tu Cuenta</h2>
      <p class="text-gray-600 text-sm text-center mb-6">Establece tu contraseña para finalizar el registro en Pet Spa UMSA.</p>

      <form @submit.prevent="handleGuardar">
        <div class="mb-4">
          <label class="block text-sm font-semibold text-gray-700 mb-1">Nueva Contraseña</label>
          <input 
            v-model="password" 
            type="password" 
            class="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-400 outline-none"
            placeholder="Crea una contraseña segura"
            required
          />
        </div>

        <div class="mb-6 bg-gray-50 p-3 rounded-lg border">
          <div class="flex justify-between text-xs mb-1">
            <span class="font-bold">Seguridad:</span>
            <span :class="strengthTextClass">{{ strengthLabel }}</span>
          </div>
          <div class="h-2 w-full bg-gray-200 rounded-full overflow-hidden">
            <div :class="strengthBarClass" :style="{ width: strengthWidth }" class="h-full transition-all duration-500"></div>
          </div>
          
          <ul class="mt-3 space-y-1 text-[11px]">
            <li :class="rules.length ? 'text-green-600' : 'text-gray-400'">✓ Mínimo 8 caracteres</li>
            <li :class="rules.upperLower ? 'text-green-600' : 'text-gray-400'">✓ Mayúsculas y minúsculas</li>
            <li :class="rules.number ? 'text-green-600' : 'text-gray-400'">✓ Al menos un número</li>
            <li :class="rules.special ? 'text-green-600' : 'text-gray-400'">✓ Un carácter especial (@#$!%*?)</li>
          </ul>
        </div>

        <button 
          :disabled="!isStrong || loading"
          class="w-full py-2.5 bg-blue-600 text-white font-bold rounded-md hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition"
        >
          {{ loading ? 'Procesando...' : 'ACTIVAR CUENTA' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from '../services/axiosConfig';

const route = useRoute();
const router = useRouter();
const password = ref('');
const loading = ref(false);

// REGLAS DE VALIDACIÓN
const rules = computed(() => ({
  length: password.value.length >= 8,
  upperLower: /[A-Z]/.test(password.value) && /[a-z]/.test(password.value),
  number: /\d/.test(password.value),
  special: /[@#$!%*?&]/.test(password.value)
}));

const isStrong = computed(() => Object.values(rules.value).every(Boolean));

// LÓGICA DEL MEDIDOR VISUAL
const strengthPoints = computed(() => Object.values(rules.value).filter(Boolean).length);
const strengthWidth = computed(() => (strengthPoints.value / 4) * 100 + '%');

const strengthLabel = computed(() => {
  if (strengthPoints.value === 0) return 'Esperando datos...';
  if (strengthPoints.value <= 2) return 'Débil';
  if (strengthPoints.value === 3) return 'Media';
  return 'Fuerte';
});

const strengthBarClass = computed(() => {
  if (strengthPoints.value <= 2) return 'bg-red-500';
  if (strengthPoints.value === 3) return 'bg-yellow-500';
  return 'bg-green-500';
});

const strengthTextClass = computed(() => {
  if (strengthPoints.value <= 2) return 'text-red-500';
  if (strengthPoints.value === 3) return 'text-yellow-600';
  return 'text-green-600';
});

const handleGuardar = async () => {
  console.log(">>> Intentando enviar datos al backend...");
  console.log(">>> Token extraido de URL:", route.query.token);
  
  loading.value = true;
  try {
    const response = await axios.post('/api/auth/establecer-password', {
      token: route.query.token,
      password: password.value
    });
    console.log(">>> RESPUESTA BACKEND:", response.data);
    alert(response.data);
    router.push('/login');
  } catch (error) {
    console.error(">>> ERROR EN PETICIÓN:", error);
    alert(error.response?.data || "Error de conexión");
  } finally {
    loading.value = false;
  }
};
</script>