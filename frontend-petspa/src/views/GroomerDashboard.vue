<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-7xl mx-auto space-y-6">
      
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        <div class="lg:col-span-1 bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4 h-fit">
          <div>
            <h2 class="text-xl font-black text-gray-800 uppercase tracking-tighter">✂️ Estación de Grooming</h2>
            <p class="text-[11px] text-gray-400 font-bold uppercase tracking-wider">Turnos Pendientes de Atención</p>
          </div>

          <div class="space-y-3 max-h-[50vh] overflow-y-auto pr-1">
            <div v-for="c in agendaActiva" :key="c.id_cita" @click="seleccionarCita(c)"
                 :class="['p-4 rounded-2xl border cursor-pointer transition-all space-y-2', citaSeleccionada?.id_cita === c.id_cita ? 'bg-purple-600 text-white border-purple-600 shadow-lg shadow-purple-100' : 'bg-gray-50 hover:bg-gray-100 border-gray-100 text-gray-600']">
              <div class="flex justify-between items-center border-b pb-1.5 border-current/10">
                <span class="text-[10px] font-mono font-black uppercase">TURNO ID: #{{ c.id_cita }}</span>
                <span class="text-[9px] font-black uppercase px-2 py-0.5 rounded bg-current/10">{{ c.estado }}</span>
              </div>
              <p class="text-sm font-black">🐾 Mascota: {{ c.mascota?.nombre || 'Paciente' }}</p>
              <p class="text-xs opacity-90">🧼 Servicio: {{ c.servicio?.nombreServicio }}</p>
              <p class="text-[11px] font-bold text-right tracking-tight opacity-75">⏰ Horario: {{ c.horaInicio.substring(0,5) }} - {{ c.horaFinEstimada.substring(0,5) }}</p>
            </div>
            <p v-if="agendaActiva.length === 0" class="text-xs text-gray-400 font-bold text-center py-6">No registras turnos activos para atender ahora.</p>
          </div>
        </div>

        <div class="lg:col-span-2 bg-white p-6 rounded-3xl border border-gray-100 shadow-xl min-h-[500px] flex flex-col justify-between">
          <div v-if="citaSeleccionada" class="space-y-6">
            
            <div class="border-b pb-3 flex justify-between items-center">
              <div>
                <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider">🐾 Ficha de Evaluación Clínica & Estética</h3>
                <p class="text-[11px] text-gray-400 font-bold">Paciente Activo: {{ citaSeleccionada.mascota?.nombre }} ({{ citaSeleccionada.mascota?.tamano }})</p>
              </div>
              <span class="text-xs font-mono font-bold text-gray-400">📅 {{ citaSeleccionada.fecha }}</span>
            </div>

            <div class="space-y-3">
              <h4 class="text-[11px] font-black text-purple-600 uppercase tracking-widest">1. Inspección Sanitaria de Entrada</h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Estado de Ingreso (Afecciones encontradas)</label>
                  <input v-model="formFicha.estadoIngreso" type="text" placeholder="Ej: Presenta nudos en orejas, rastros de pulgas"
                         class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border focus:border-purple-400" />
                </div>
                <div>
                  <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Temperamento Observado</label>
                  <select v-model="formFicha.temperamento" class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none text-gray-700">
                    <option value="Tranquilo">🟢 Tranquilo / Cooperativo</option>
                    <option value="Nervioso">🟡 Nervioso / Asustado</option>
                    <option value="Inquieto">🟠 Inquieto / Hiperactivo</option>
                    <option value="Agresivo">🔴 Agresivo (Requiere Bozal)</option>
                  </select>
                </div>
              </div>
            </div>

            <div class="space-y-3 bg-gray-50/50 p-4 rounded-2xl border border-gray-100">
              <h4 class="text-[11px] font-black text-green-600 uppercase tracking-widest">2. Checklist Técnico Obligatorio</h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <label v-for="item in catalogoChecklist" :key="item.id_item" class="flex items-center space-x-3 bg-white p-2.5 rounded-xl border border-gray-100 cursor-pointer select-none shadow-xs hover:border-green-300 transition-all">
                  <input type="checkbox" :value="item.id_item" v-model="itemsSeleccionados" class="w-4 h-4 rounded text-purple-600 focus:ring-purple-400 border-gray-300" />
                  <span class="text-xs font-bold text-gray-600 uppercase tracking-tight">{{ item.descripcionTarea }}</span>
                </label>
              </div>
            </div>

            <div class="space-y-3">
              <h4 class="text-[11px] font-black text-blue-600 uppercase tracking-widest">3. Desglose de Insumos & Materiales</h4>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-2">
                <label v-for="insumo in ['Champú Neutro', 'Champú Antipulgas', 'Bálsamo Brillo', 'Colonia Bebé']" :key="insumo"
                       class="p-2.5 bg-gray-50 rounded-xl border border-gray-100 text-[11px] font-bold text-gray-600 flex items-center space-x-2 cursor-pointer">
                  <input type="checkbox" class="rounded border-gray-300 text-purple-600" />
                  <span>{{ insumo }}</span>
                </label>
              </div>
            </div>

            <div class="space-y-3">
              <h4 class="text-[11px] font-black text-amber-600 uppercase tracking-widest">4. Fotos de Evidencia Operativa</h4>
              <div class="grid grid-cols-2 gap-4 text-center">
                
                <div @click="abrirSelectorAntes" class="border-2 border-dashed border-gray-200 rounded-2xl p-4 bg-gray-50/50 hover:bg-white transition-all cursor-pointer relative overflow-hidden flex flex-col items-center justify-center min-h-[110px]">
                  <input type="file" ref="refInputAntes" accept="image/*" class="hidden" @change="procesarFotoAntes" />
                  <img v-if="previewAntes" :src="previewAntes" class="absolute inset-0 w-full h-full object-cover" />
                  <template v-else>
                    <p class="text-xl">📸</p>
                    <span class="text-[10px] font-black text-gray-400 uppercase tracking-wider block mt-1">Cargar Foto Antes</span>
                  </template>
                </div>

                <div @click="abrirSelectorDespues" class="border-2 border-dashed border-gray-200 rounded-2xl p-4 bg-gray-50/50 hover:bg-white transition-all cursor-pointer relative overflow-hidden flex flex-col items-center justify-center min-h-[110px]">
                  <input type="file" ref="refInputDespues" accept="image/*" class="hidden" @change="procesarFotoDespues" />
                  <img v-if="previewDespues" :src="previewDespues" class="absolute inset-0 w-full h-full object-cover" />
                  <template v-else>
                    <p class="text-xl">✨</p>
                    <span class="text-[10px] font-black text-gray-400 uppercase tracking-wider block mt-1">Cargar Foto Después</span>
                  </template>
                </div>

              </div>
            </div>

            <div class="space-y-2">
              <label class="block text-[10px] font-black text-gray-400 uppercase">Observaciones & Recomendaciones Clínicas para el Dueño</label>
              <textarea v-model="formFicha.observaciones" rows="2" placeholder="Ej: Se sugiere cepillado periódico por nudos leves..."
                        class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border focus:border-purple-400 resize-none"></textarea>
            </div>

            <button @click="procesarFichaGrooming" :disabled="itemsSeleccionados.length === 0"
                    class="w-full py-3 bg-gray-900 hover:bg-purple-600 text-white rounded-xl text-xs font-black uppercase tracking-widest transition-all shadow-lg disabled:opacity-50">
              Finalizar Servicio y Guardar Expediente
            </button>

          </div>
          <div v-else class="flex flex-col items-center justify-center my-auto text-gray-400 space-y-2">
            <p class="text-3xl">🐾</p>
            <p class="text-xs font-bold uppercase tracking-wider">Selecciona un turno de la agenda izquierda para desplegar la mesa de trabajo</p>
          </div>
        </div>
      </div>

      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4">
        <div class="border-b pb-2 flex justify-between items-center">
          <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider">📋 Historial de Trabajos Completados Hoy</h3>
          <span class="text-[10px] bg-purple-50 text-purple-600 font-mono font-bold px-2 py-0.5 rounded">Total: {{ agendaHistorial.length }}</span>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full border-collapse text-left text-xs">
            <thead>
              <tr class="bg-gray-50 text-gray-400 font-black uppercase text-[10px] border-b">
                <th class="p-3">Turno ID</th>
                <th class="p-3">Fecha</th>
                <th class="p-3">Mascota</th>
                <th class="p-3">Servicio Estético Prestado</th>
                <th class="p-3">Horario Ejecución</th>
                <th class="p-3 text-center">Estado</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="h in agendaHistorial" :key="h.id_cita" class="border-b hover:bg-gray-50/50 font-medium text-gray-600">
                <td class="p-3 font-mono font-bold text-gray-800">#{{ h.id_cita }}</td>
                <td class="p-3">{{ h.fecha }}</td>
                <td class="p-3 font-bold text-purple-600">🐾 {{ h.mascota?.nombre || 'Paciente' }}</td>
                <td class="p-3">{{ h.servicio?.nombreServicio }}</td>
                <td class="p-3 font-mono">{{ h.horaInicio.substring(0,5) }} - {{ h.horaFinEstimada.substring(0,5) }}</td>
                <td class="p-3 text-center">
                  <span class="px-2 py-0.5 bg-green-50 text-green-700 rounded text-[10px] uppercase font-black tracking-wider border border-green-200">
                    {{ h.estado }}
                  </span>
                </td>
              </tr>
              <tr v-if="agendaHistorial.length === 0">
                <td colspan="6" class="p-4 text-center text-gray-400 font-bold">No registras cierres técnicos completados en esta jornada.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from '../services/axiosConfig';

const userSession = JSON.parse(localStorage.getItem('user') || '{}');
const idUsuarioGroomer = userSession.id_usuario || 1;

const miAgenda = ref([]);
const catalogoChecklist = ref([]);
const citaSeleccionada = ref(null);

const refInputAntes = ref(null);
const refInputDespues = ref(null);
const previewAntes = ref(null);
const previewDespues = ref(null);

const itemsSeleccionados = ref([]); 
// INTEGRACIÓN: Agregamos fotoAntes y fotoDespues vacíos al formulario reactivo inicial
const formFicha = ref({ idCita: null, estadoIngreso: '', temperamento: 'Tranquilo', observaciones: '', recomendaciones: 'Ninguna', fotoAntes: '', fotoDespues: '' });

const agendaActiva = computed(() => miAgenda.value.filter(c => c.estado === 'CONFIRMADA' || c.estado === 'PAGADA'));
const agendaHistorial = computed(() => miAgenda.value.filter(c => c.estado === 'FINALIZADA'));

const seleccionarCita = (cita) => {
  citaSeleccionada.value = cita;
  itemsSeleccionados.value = []; 
  previewAntes.value = null;
  previewDespues.value = null;
  formFicha.value = { idCita: cita.id_cita, estadoIngreso: '', temperamento: 'Tranquilo', observaciones: '', recomendaciones: 'Ninguna', fotoAntes: '', fotoDespues: '' };
};

const abrirSelectorAntes = () => { refInputAntes.value.click(); };
const abrirSelectorDespues = () => { refInputDespues.value.click(); };

// INTEGRACIÓN: Los readers ahora salvan la información binaria dentro de formFicha antes de enviar
const procesarFotoAntes = (e) => {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => { 
      previewAntes.value = reader.result; 
      formFicha.value.fotoAntes = reader.result; // Almacenamiento Base64
    };
  }
};

const procesarFotoDespues = (e) => {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => { 
      previewDespues.value = reader.result; 
      formFicha.value.fotoDespues = reader.result; // Almacenamiento Base64
    };
  }
};

const cargarSuiteGroomer = async () => {
  try {
    const [resAgenda, resChecklist] = await Promise.all([
      axios.get(`http://localhost:8080/api/groomer/citas/${idUsuarioGroomer}`),
      axios.get('http://localhost:8080/api/groomer/checklist/items')
    ]);
    miAgenda.value = resAgenda.data || [];
    catalogoChecklist.value = resChecklist.data || [];
  } catch (err) {
    console.error("❌ Error inicializando el espacio del estilista:", err);
  }
};

const procesarFichaGrooming = async () => {
  try {
    await axios.post(`http://localhost:8080/api/groomer/ficha/guardar?itemsCompletados=${itemsSeleccionados.value.join(',')}`, formFicha.value);
    alert("✨ Ficha de grooming y checklist guardados de manera conforme.");
    citaSeleccionada.value = null;
    await cargarSuiteGroomer(); 
  } catch (err) {
    alert("Error al guardar informe técnico.");
  }
};

onMounted(cargarSuiteGroomer);
</script>