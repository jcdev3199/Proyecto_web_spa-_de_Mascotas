<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-6xl mx-auto">
      
      <!-- CABECERA CON BOTÓN VOLVER -->
      <header class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
        <div>
          <button 
            @click="$router.push('/dashboard')" 
            class="text-blue-600 font-bold text-xs flex items-center gap-1 mb-2 hover:underline"
          >
            ← VOLVER AL PANEL
          </button>
          <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">
            Historial de Auditoría
          </h2>
        </div>
        
        <button 
          @click="exportarCSV" 
          class="bg-green-600 text-white px-6 py-2 rounded-xl text-xs font-black shadow-lg hover:bg-green-700 transition-all active:scale-95"
        >
          DESCARGAR REPORTE (.CSV)
        </button>
      </header>

      <!-- TABLA DE DATOS -->
      <div class="bg-white rounded-3xl shadow-xl overflow-hidden border border-gray-100">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-900 text-white text-[10px] uppercase tracking-widest">
            <tr>
              <th class="p-4 text-center w-20">ID</th>
              <th class="p-4">Fecha / Hora</th>
              <th class="p-4">Usuario</th>
              <th class="p-4 text-center">Acción</th>
              <th class="p-4">IP Origen</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="log in logs" :key="log.idLog" class="hover:bg-blue-50/50 transition-colors">
              <td class="p-4 text-center font-bold text-gray-400">{{ log.idLog }}</td>
              <td class="p-4 font-mono text-xs text-gray-500">
                {{ formatearFecha(log.fechaHora) }}
              </td>
              <td class="p-4">
                <p class="font-bold text-gray-800 leading-tight">{{ log.email }}</p>
                <p class="text-[10px] text-blue-600 font-black uppercase tracking-tighter">{{ log.rol }}</p>
              </td>
              <td class="p-4 text-center">
                <span 
                  :class="claseAccion(log.accion)" 
                  class="px-3 py-1 rounded-full text-[9px] font-black uppercase whitespace-nowrap"
                >
                  {{ log.accion.replace(/_/g, ' ') }}
                </span>
              </td>
              <td class="p-4 text-gray-400 font-mono text-xs">{{ log.ip }}</td>
            </tr>
            
            <!-- ESTADO VACÍO -->
            <tr v-if="logs.length === 0">
              <td colspan="5" class="p-12 text-center text-gray-400 font-medium">
                No se encontraron registros de auditoría.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '../services/axiosConfig';
import { authState } from '../store';

const logs = ref([]);

const cargarLogs = async () => {
  try {
    const token = authState.token || authState.user?.token;
    const res = await axios.get('http://localhost:8080/api/admin/logs', {
      headers: { Authorization: `Bearer ${token}` }
    });
    logs.value = res.data;
  } catch (err) {
    console.error("Error al cargar auditoría:", err);
    alert("Error de sesión: Por favor, vuelve a iniciar sesión.");
  }
};

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  return new Date(fecha).toLocaleString('es-BO', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  });
};

const claseAccion = (accion) => {
  const a = accion.toUpperCase();
  if (a.includes('FALLIDO') || a.includes('BLOQUEO') || a.includes('DENEGADO')) 
    return 'bg-red-100 text-red-600 border border-red-200';
  if (a.includes('EXITOSO')) 
    return 'bg-green-100 text-green-600 border border-green-200';
  return 'bg-blue-100 text-blue-600 border border-blue-200';
};

const exportarCSV = () => {
  if (logs.value.length === 0) return;
  const encabezados = "ID,Fecha_Hora,Email,Rol,Accion,IP_Origen\n";
  const filas = logs.value.map(l => 
    `${l.idLog},${l.fechaHora},${l.email},${l.rol},${l.accion},${l.ip}`
  ).join("\n");
  const blob = new Blob([encabezados + filas], { type: 'text/csv;charset=utf-8;' });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `auditoria_petspa_${new Date().toLocaleDateString()}.csv`);
  link.click();
};

onMounted(cargarLogs);
</script>