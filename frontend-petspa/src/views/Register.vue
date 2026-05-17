<template>
  <div class="min-h-screen flex items-center justify-center bg-blue-50 p-4">
    <div class="bg-white p-8 rounded-3xl shadow-2xl w-full max-w-2xl border border-blue-100">
      <h2 class="text-3xl font-black text-gray-800 mb-2 text-center tracking-tighter">PET SPA UMSA</h2>
      <p class="text-center text-gray-500 mb-8 font-medium">Crea tu cuenta de cliente</p>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div>
          <label class="text-[10px] font-black text-blue-600 uppercase ml-1">Nombre(s) *</label>
          <input v-model="form.nombre" type="text" placeholder="" 
            class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition-all" />
        </div>
        <div>
          <label class="text-[10px] font-black text-blue-600 uppercase ml-1">Apellido(s) *</label>
          <input v-model="form.apellido" type="text" placeholder="" 
            class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition-all" />
        </div>

        <div>
          <label class="text-[10px] font-black text-blue-600 uppercase ml-1">Correo Electrónico *</label>
          <input v-model="form.email" type="email" placeholder="usuario@correo.com" 
            class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition-all" />
        </div>
        <div>
          <label class="text-[10px] font-black text-blue-600 uppercase ml-1">Nombre de Usuario *</label>
          <input v-model="form.username" type="text" placeholder="jc_cliente1" 
            class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition-all" />
        </div>

        <div>
          <label class="text-[10px] font-black text-blue-600 uppercase ml-1">CI (Cédula de Identidad) *</label>
          <input v-model="form.ci" type="text" placeholder="1234567" 
            class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition-all" />
        </div>
        <div>
          <label class="text-[10px] font-black text-blue-600 uppercase ml-1">Teléfono *</label>
          <input v-model="form.telefono" type="text" placeholder="70000000" 
            class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition-all" />
        </div>
      </div>

      <div class="mt-5">
        <label class="text-[10px] font-black text-blue-600 uppercase ml-1">Dirección de Domicilio *</label>
        <input v-model="form.direccion" type="text" placeholder="Ej: La Paz, Bolivia" 
          class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 text-sm transition-all" />
      </div>

      <div class="mt-5 p-4 bg-blue-50 rounded-2xl border border-blue-100 shadow-inner">
        <label class="text-[10px] font-black text-blue-800 uppercase">Establecer Contraseña *</label>
        <input v-model="form.password" type="password" placeholder="Mínimo 8 caracteres" 
          class="w-full p-3 mt-1 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-600 bg-white text-sm transition-all" />
        
        <div class="mt-3 h-2 w-full bg-gray-200 rounded-full overflow-hidden">
          <div :class="medidorColor" :style="{ width: medidorProgreso }" class="h-full transition-all duration-500"></div>
        </div>
        <div class="flex justify-between items-center mt-2">
          <p class="text-[10px] text-gray-500 font-bold uppercase tracking-tighter">Nivel de Seguridad:</p>
          <p class="text-[10px] font-black uppercase" :class="textoFuerzaColor">{{ medidorTexto }}</p>
        </div>
      </div>

      <button 
        @click="registrar" 
        :disabled="!formValido || loading"
        class="w-full mt-8 p-4 rounded-2xl font-black transition-all shadow-lg active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
        :class="formValido ? 'bg-blue-600 text-white hover:bg-blue-700' : 'bg-gray-300 text-gray-500'"
      >
        {{ loading ? 'PROCESANDO...' : 'REGISTRAR CON FORMULARIO' }}
      </button>

      <div class="my-6 flex items-center before:flex-1 before:border-t before:border-gray-300 after:flex-1 after:border-t after:border-gray-300">
        <p class="mx-4 text-[10px] font-black text-gray-400 uppercase">O también</p>
      </div>

      <a href="http://localhost:8080/oauth2/authorization/google" 
         class="flex items-center justify-center w-full border-2 border-gray-200 p-3 rounded-2xl font-black text-gray-700 hover:bg-gray-50 hover:border-blue-200 transition-all active:scale-95 shadow-sm">
        <img src="https://www.svgrepo.com/show/475656/google-color.svg" class="w-5 h-5 mr-3" alt="Google" />
        REGISTRARSE CON GOOGLE
      </a>
      
      <p class="mt-8 text-center text-xs font-bold text-gray-400 uppercase tracking-widest">
        ¿Ya eres parte del Spa? <router-link to="/login" class="text-blue-600 underline">Inicia Sesión</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, ref } from 'vue';
import axios from '../services/axiosConfig';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(false);

const form = reactive({
  nombre: '', apellido: '', email: '', username: '',
  password: '', ci: '', telefono: '', direccion: ''
});

// LÓGICA DE FUERZA (Regla 3.1)
const fuerzaPuntos = computed(() => {
  let p = 0;
  if (form.password.length >= 8) p++;
  if (/[A-Z]/.test(form.password) && /[a-z]/.test(form.password)) p++;
  if (/[0-9]/.test(form.password)) p++;
  if (/[^A-Za-z0-9]/.test(form.password)) p++;
  return p;
});

const medidorProgreso = computed(() => (fuerzaPuntos.value * 25) + '%');
const medidorColor = computed(() => {
  const c = ['bg-red-500', 'bg-orange-500', 'bg-yellow-500', 'bg-green-500'];
  return c[fuerzaPuntos.value - 1] || 'bg-gray-200';
});
const textoFuerzaColor = computed(() => {
  const c = ['text-red-500', 'text-orange-500', 'text-yellow-500', 'text-green-500'];
  return c[fuerzaPuntos.value - 1] || 'text-gray-400';
});
const medidorTexto = computed(() => {
  const t = ['Muy Débil', 'Débil', 'Aceptable', 'Fuerte'];
  return t[fuerzaPuntos.value - 1] || 'Esperando datos...';
});

// VALIDACIÓN DEL FORMULARIO
const formValido = computed(() => {
  const camposLlenos = Object.values(form).every(val => val.trim() !== '');
  const emailValido = /.+@.+\..+/.test(form.email);
  const passwordFuerte = fuerzaPuntos.value >= 3; 
  return camposLlenos && emailValido && passwordFuerte;
});

const registrar = async () => {
  if (!formValido.value) return;
  
  loading.value = true;
  try {
    const res = await axios.post('/api/auth/signup', form);
    alert(res.data); 
    router.push('/login');
  } catch (err) {
    const msg = err.response?.data || "Error de conexión con el servidor.";
    alert("ERROR: " + (typeof msg === 'object' ? JSON.stringify(msg) : msg));
  } finally {
    loading.value = false;
  }
};
</script>