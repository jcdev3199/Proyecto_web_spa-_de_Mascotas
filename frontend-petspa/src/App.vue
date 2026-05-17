<template>
  <div class="min-h-screen bg-gray-50">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue';
import { authState } from './store';

let timer;
let logoutTimer;

const reiniciarTemporizador = () => {
  clearTimeout(timer);
  if (authState.user) {
    //  "EXPIRO"
    // 60000 ms = 10 minuto
    timer = setTimeout(() => {
      alert("Tu sesión ha expirado por inactividad.");
      authState.logout();
      // Redirección forzada para evitar pantalla en blanco
      window.location.href = 'http://localhost:5173/';
    }, 60000); 
  }
};

onMounted(() => {
  // Escuchamos cualquier movimiento del mouse o teclado
  window.addEventListener('mousemove', reiniciarTemporizador);
  window.addEventListener('keydown', reiniciarTemporizador);
  reiniciarTemporizador();
});

onUnmounted(() => {
  window.removeEventListener('mousemove', reiniciarTemporizador);
  window.removeEventListener('keydown', reiniciarTemporizador);
});


const resetTimer = () => {
  clearTimeout(logoutTimer);
  if (authState.user) {
    // "AUSENCIA"
    // 60000 ms = 10 minuto
    logoutTimer = setTimeout(() => {
      alert("Sesión cerrada por inactividad de 30 minutos.");
      authState.logout();
      window.location.href = 'http://localhost:5173/';
    }, 60000); 
  }
};

onMounted(() => {
  // Detectar actividad del usuario
  window.addEventListener('mousemove', resetTimer);
  window.addEventListener('keydown', resetTimer);
  resetTimer();
});

onUnmounted(() => {
  window.removeEventListener('mousemove', resetTimer);
  window.removeEventListener('keydown', resetTimer);
});
</script>