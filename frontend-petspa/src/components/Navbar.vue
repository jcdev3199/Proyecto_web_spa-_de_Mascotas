<template>
  <nav class="bg-slate-900 text-white p-4 flex justify-between items-center shadow-lg">
    <router-link to="/" class="text-xl font-black tracking-tighter">PET<span class="text-blue-400">SPA</span></router-link>
    
    <div class="flex items-center gap-6">
      <div v-if="!authState.user" class="flex gap-4">
        <router-link to="/login" class="hover:text-blue-400 font-medium">Iniciar Sesión</router-link>
        <router-link to="/register" class="bg-blue-600 px-4 py-1 rounded-lg hover:bg-blue-700">Registrarse</router-link>
      </div>

      <div v-else class="flex items-center gap-4">
        <div class="text-right leading-none">
          <p class="font-bold text-sm">{{ authState.user.nombre || authState.user.username }}</p>
          <span class="text-[10px] bg-blue-900 text-blue-200 px-2 py-0.5 rounded uppercase">{{ authState.user.rol }}</span>
        </div>
        <!-- Cambiado de authState.logout() a handleLogout local -->
        <button @click="handleLogout" class="text-red-400 text-xs hover:underline font-bold uppercase">Salir</button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { authState } from '../store'
import { useRouter } from 'vue-router'

const router = useRouter();

const handleLogout = () => {
  // Limpia estado y localStorage
  authState.logout();
  
  // Reemplaza la ruta en el historial para que 'atrás' no vuelva al dashboard
  router.replace('/login').then(() => {
    // Recarga forzada para limpiar cualquier rastro de datos en memoria/RAM
    window.location.reload();
  });
};
</script>