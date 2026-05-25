<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-xl mx-auto">
      <header class="mb-6">
        <button @click="$router.push('/dashboard')" class="text-blue-600 font-black text-[10px] mb-2 hover:underline uppercase tracking-wider block">
          ← Volver al Panel
        </button>
        <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">
          Solicitar Cita de Grooming
        </h2>
      </header>

      <form @submit.prevent="enviarCita" class="bg-white p-8 rounded-3xl shadow-xl border border-gray-100 space-y-6">
        
        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Seleccionar Mi Mascota</label>
          <select v-model="form.idMascota" required class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent outline-none text-sm font-bold text-gray-700 focus:border-blue-400 transition-all">
            <option value="" disabled>Seleccione una de sus mascotas...</option>
            <option v-for="m in listaMascotas" :key="m.id_mascota" :value="m.id_mascota">
              🐾 {{ m.nombre }} ({{ m.especie }} - {{ m.tamano }})
            </option>
          </select>
          <p v-if="listaMascotas.length === 0" class="text-[10px] text-red-500 font-bold mt-1 ml-1">
            ⚠️ No tienes mascotas registradas. Ve al menú "Mis Mascotas" para registrar una primero.
          </p>
        </div>

        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Tipo de Servicio Solicitado</label>
          <select v-model="form.idServicio" required class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent outline-none text-sm font-bold text-gray-700 focus:border-blue-400 transition-all">
            <option value="" disabled>Seleccione un servicio...</option>
            <option v-for="s in listaServicios" :key="s.id_servicio" :value="s.id_servicio">
              {{ s.nombreServicio }} ({{ s.duracionBaseMinutos }} min) - {{ s.precioBase }} Bs.
            </option>
          </select>
          <p v-if="listaServicios.length === 0" class="text-[10px] text-red-500 font-bold mt-1 ml-1">
            ⚠️ No hay servicios disponibles. Ve al menú "Servicios" o contacta al administrador.
          </p>
        </div>

        <div class="space-y-2">
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Buscar y Seleccionar Estilista (Groomer)</label>
          
          <input 
            v-model="filtroGroomer" 
            type="text" 
            placeholder="🔍 Filtrar por nombre o especialidad..." 
            class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent outline-none text-xs font-medium focus:border-blue-300 transition-all"
          />

          <select v-model="form.idGroomer" required class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent outline-none text-sm font-bold text-gray-700 focus:border-blue-400 transition-all">
            <option value="" disabled>Seleccione un groomer...</option>
            <option v-for="g in groomersFiltrados" :key="g.id_groomer" :value="g.id_groomer">
              {{ g.nombres }} {{ g.apellidos }} ➔ [{{ g.especialidad || 'General' }}]
            </option>
          </select>
          <p v-if="groomersFiltrados.length === 0" class="text-[10px] text-red-500 font-bold ml-1">No se encontraron estilistas con ese criterio.</p>
        </div>

        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Fecha de la Cita</label>
          <input v-model="form.fecha" type="date" required class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent outline-none text-sm font-bold focus:border-blue-400 transition-all">
        </div>

        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-2 ml-1">Horarios Sugeridos Disponibles</label>
          <div class="grid grid-cols-4 gap-2">
            
            <div v-if="horariosSugeridos.length === 0" class="col-span-4 text-center py-3 text-[10px] font-black text-gray-400 uppercase border-2 border-dashed rounded-xl bg-gray-50">
              ⚠️ Seleccione un estilista para visualizar sus horas de jornada
            </div>

            <button 
              v-for="slot in horariosSugeridos" 
              :key="slot"
              type="button"
              @click="form.horaInicio = slot"
              :class="[
                'p-3 text-xs font-black rounded-xl border-2 transition-all uppercase tracking-tight',
                form.horaInicio === slot 
                  ? 'bg-blue-600 text-white border-blue-600 shadow-md scale-95' 
                  : 'bg-green-50 text-green-700 border-green-200 hover:bg-green-100'
              ]"
            >
              {{ slot }}
            </button>
          </div>
        </div>

        <button 
          type="submit" 
          :disabled="loading || !form.horaInicio || !form.fecha || !form.idGroomer || !form.idMascota || !form.idServicio"
          class="w-full py-4 bg-gray-900 text-white rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl hover:bg-blue-600 transition-all disabled:opacity-50"
        >
          {{ loading ? 'Procesando Reserva...' : 'Solicitar Turno Ahora' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'; // <-- Añadido watch
import { useRouter } from 'vue-router';
import axios from '../services/axiosConfig';

const router = useRouter();
const loading = ref(false);

const listaGroomers = ref([]);
const listaMascotas = ref([]);
const listaServicios = ref([]);
const filtroGroomer = ref('');

// Extraer ID de sesión del almacenamiento local
const userSession = JSON.parse(localStorage.getItem('user') || '{}');
const idUsuarioLogueado = userSession.id_usuario || 1;

const form = ref({
  idMascota: '',
  idGroomer: '', 
  idServicio: 1, 
  fecha: '',
  horaInicio: ''
});

// SINCRONIZACIÓN DE HORARIOS BASADA EN LA JORNADA LABORAL DEL EXCEL
const horariosSugeridos = computed(() => {
  const groomer = listaGroomers.value.find(g => g.id_groomer === form.value.idGroomer);
  if (!groomer) return [];

  // Leer hora de entrada y salida (soporta ambas nomenclaturas camelCase y snake_case)
  const entradaStr = groomer.horaEntrada || groomer.hora_entrada || "09:00:00";
  const salidaStr = groomer.horaSalida || groomer.hora_salida || "18:00:00";

  const horaEntrada = parseInt(entradaStr.split(':')[0]);
  const horaSalida = parseInt(salidaStr.split(':')[0]);

  const slots = [];
  for (let h = horaEntrada; h < horaSalida; h++) {
    const formatHora = h < 10 ? `0${h}:00` : `${h}:00`;
    slots.push(formatHora);
  }
  return slots;
});

// Limpiar la hora seleccionada si se cambia el estilista para evitar desbordes horarios
watch(() => form.value.idGroomer, () => {
  form.value.horaInicio = '';
});

// Cargar catálogos dinámicos cruzados desde el Backend
const cargarCatalogos = async () => {
  try {
    const [resGroomers, resMascotas, resServicios] = await Promise.all([
      axios.get('http://localhost:8080/api/citas/groomers'),
      axios.get(`http://localhost:8080/api/cliente/mascotas/${idUsuarioLogueado}`),
      axios.get('http://localhost:8080/api/citas/servicios')
    ]);
    listaGroomers.value = resGroomers.data;
    listaMascotas.value = resMascotas.data;
    listaServicios.value = resServicios.data;
  } catch (err) {
    console.error("Error al cargar los catálogos de la cita:", err);
  }
};

// Filtrado reactivo de Groomers por nombre o especialidad
const groomersFiltrados = computed(() => {
  return listaGroomers.value.filter(g => {
    const termino = filtroGroomer.value.toLowerCase();
    const nombreCompleto = `${g.nombres} ${g.apellidos}`.toLowerCase();
    const especialidad = (g.especialidad || '').toLowerCase();
    return nombreCompleto.includes(termino) || especialidad.includes(termino);
  });
});

const enviarCita = async () => {
  loading.value = true;
  try {
    const payload = {
      ...form.value,
      horaInicio: form.value.horaInicio + ":00" 
    };

    const res = await axios.post('http://localhost:8080/api/citas/solicitar', payload);
    alert(`✅ Solicitud de Cita procesada.\nID Registro: ${res.data.id_cita}\nEstado: ${res.data.estado}`);
    router.push('/dashboard');
  } catch (err) {
    alert("❌ Error de Agenda: " + (err.response?.data?.error || "Servidor no disponible"));
  } finally {
    loading.value = false;
  }
};

onMounted(cargarCatalogos);
</script>