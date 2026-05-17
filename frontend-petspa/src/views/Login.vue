<template>
  <div class="min-h-screen flex items-center justify-center bg-blue-50 p-4">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md">
      <h2 class="text-3xl font-bold text-center text-gray-800 mb-8">
        {{ mostrar2FA ? 'Seguridad 2FA' : 'Iniciar Sesión' }}
      </h2>
      
      <!-- CASO 1: LOGIN MANUAL -->
      <div v-if="!mostrar2FA" class="space-y-4">
        <input 
          v-model="loginData.username" 
          type="text" 
          placeholder="Correo electrónico" 
          class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" 
        />
        <input 
          v-model="loginData.password" 
          type="password" 
          placeholder="Contraseña" 
          class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" 
        />
        
        <button 
          @click="handleLogin" 
          :disabled="loading"
          class="w-full bg-blue-600 text-white p-3 rounded-lg font-bold hover:bg-blue-700 transition disabled:bg-gray-400"
        >
          {{ loading ? 'Ingresando...' : 'Ingresar' }}
        </button>

        <div class="my-6 flex items-center before:flex-1 before:border-t before:border-gray-300 after:flex-1 after:border-t after:border-gray-300">
          <p class="mx-4 text-gray-500">O</p>
        </div>

        <a href="http://localhost:8080/oauth2/authorization/google" 
           class="flex items-center justify-center w-full border-2 border-gray-200 p-3 rounded-lg font-medium hover:bg-gray-50 transition">
          <img src="https://www.svgrepo.com/show/475656/google-color.svg" class="w-6 h-6 mr-3" alt="Google" />
          Entrar con Google
        </a>
      </div>

      <!-- CASO 2: VERIFICACIÓN 2FA -->
      <div v-else class="space-y-6 text-center">
        <p class="text-gray-600">Introduce el código de 6 dígitos de tu aplicación **Google Authenticator**.</p>
        
        <div v-if="qrCodeUri" class="flex flex-col items-center border-2 border-dashed border-blue-200 p-4 rounded-xl bg-blue-50">
          <p class="text-xs font-bold text-blue-700 mb-2 uppercase">Escanea este código QR:</p>
          <img :src="qrCodeUri" alt="QR 2FA" class="w-48 h-48 shadow-sm" />
        </div>

        <input 
          v-model="codigoOTP" 
          type="text" 
          maxlength="6"
          placeholder="000000" 
          @keyup.enter="verificarCodigoAdmin"
          class="w-full text-center text-2xl tracking-widest p-3 border border-blue-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" 
        />
        
        <button 
          @click="verificarCodigoAdmin" 
          :disabled="loading"
          class="w-full bg-green-600 text-white p-3 rounded-lg font-bold hover:bg-green-700 transition disabled:bg-gray-400"
        >
          {{ loading ? 'Verificando...' : 'Verificar y Entrar' }}
        </button>
        
        <button @click="mostrar2FA = false" class="text-sm text-gray-400 hover:underline">Volver atrás</button>
      </div>

      <p v-if="!mostrar2FA" class="mt-6 text-center text-gray-600">
        ¿Eres cliente y no tienes cuenta? <router-link to="/register" class="text-blue-600 font-bold">Regístrate aquí</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import axios from '../services/axiosConfig'; // Importamos la configuración del puente
import { useRouter } from 'vue-router';
import { authState } from '../store';

const router = useRouter();
const loading = ref(false);
const mostrar2FA = ref(false);
const qrCodeUri = ref('');
const codigoOTP = ref('');

const loginData = reactive({ username: '', password: '' });

const handleLogin = async () => {
  loading.value = true;
  try {
    const res = await axios.post('/api/auth/login', {
      email: loginData.username,
      password: loginData.password
    });
    const data = res.data;
    if (data.rol === "Administrador" && !data.token) {
      authState.tempEmail = data.email; 
      mostrar2FA.value = true; 
      await obtenerQR(); 
    } else if (data.token) {
      authState.setAuth(data); 
      router.push('/dashboard');
    }
  } catch (error) {
    alert("Error: " + (error.response?.data || "Credenciales inválidas"));
  } finally {
    loading.value = false;
  }
};

const obtenerQR = async () => {
  try {
    const res = await axios.get(`/api/auth/2fa/setup?email=${authState.tempEmail}`);
    qrCodeUri.value = res.data; 
  } catch (err) { console.log("Código directo solicitado."); }
};

const verificarCodigoAdmin = async () => {
  if (codigoOTP.value.length !== 6) return;
  loading.value = true;
  try {
    const res = await axios.post('/api/auth/2fa/verify', {
      email: authState.tempEmail,
      codigo: codigoOTP.value
    });
    if (res.data.token) {
      authState.setAuth(res.data); 
      router.push('/dashboard');
    }
  } catch (error) {
    alert("Código incorrecto.");
  } finally {
    loading.value = false;
  }
};
</script>