<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-4xl mx-auto">
      
      <header class="mb-6 flex justify-between items-center">
        <div>
          <button @click="$router.push('/dashboard')" class="text-blue-600 font-black text-[10px] mb-2 hover:underline uppercase tracking-wider block">
            ← Volver al Panel
          </button>
          <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">
            Gestión de Mis Mascotas
          </h2>
        </div>
        <button 
          @click="mostrarFormulario = !mostrarFormulario" 
          class="py-2 px-4 bg-gray-900 text-white text-xs font-black rounded-xl uppercase tracking-wider shadow"
        >
          {{ mostrarFormulario ? '✕ Cancelar' : '＋ Agregar Mascota' }}
        </button>
      </header>

      <div v-if="mostrarFormulario" class="bg-white p-8 rounded-3xl shadow-xl border border-gray-100 mb-6 transition-all">
        <h4 class="text-sm font-black text-gray-700 uppercase tracking-wider mb-4">🐾 Registrar Nueva Mascota</h4>
        
        <form @submit.prevent="agregarMascota" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Nombre de la Mascota</label>
              <input v-model="nuevaMascota.nombre" type="text" placeholder="Ej. Max" required class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent focus:border-blue-400 transition-all" />
            </div>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Especie</label>
              <select v-model="nuevaMascota.especie" required class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent focus:border-blue-400 transition-all text-gray-700">
                <option value="" disabled>Seleccione Especie...</option>
                <option value="Perro">Perro</option>
                <option value="Gato">Gato</option>
                <option value="Otro">Otro</option>
              </select>
            </div>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Raza</label>
              <input v-model="nuevaMascota.raza" type="text" placeholder="Ej. Poodle" class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent focus:border-blue-400 transition-all" />
            </div>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Tamaño</label>
              <select v-model="nuevaMascota.tamano" required class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent focus:border-blue-400 transition-all text-gray-700">
                <option value="" disabled>Seleccione Tamaño...</option>
                <option value="Pequeño">Pequeño</option>
                <option value="Mediano">Mediano</option>
                <option value="Grande">Grande</option>
                <option value="Gigante">Gigante</option>
              </select>
            </div>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Fecha de Nacimiento</label>
              <input v-model="nuevaMascota.fechaNacimiento" type="date" required class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent focus:border-blue-400 transition-all text-gray-500" />
            </div>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Temperamento</label>
              <select v-model="nuevaMascota.temperamento" required class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent focus:border-blue-400 transition-all text-gray-700">
                <option value="" disabled>Seleccione Temperamento...</option>
                <option value="Tranquilo">Tranquilo</option>
                <option value="Nervioso">Nervioso</option>
                <option value="Agresivo">Agresivo</option>
                <option value="Inquieto">Inquieto</option>
              </select>
            </div>
          </div>
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Alergias o Restricciones Médicas</label>
            <textarea v-model="nuevaMascota.alergias" rows="2" placeholder="Describa alergias..." class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent focus:border-blue-400 transition-all resize-none"></textarea>
          </div>
          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1 ml-1">Carnet de Vacunas (Documento o Imagen)</label>
            <input type="file" accept=".pdf,image/*" @change="manejarCargaArchivo" class="w-full p-2 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent" />
          </div>
          <div class="flex justify-end pt-2">
            <button type="submit" :disabled="loading" class="py-3 px-8 bg-blue-600 text-white text-xs font-black uppercase tracking-wider rounded-xl hover:bg-blue-700 transition-colors shadow">
              Guardar Perfil de Mascota
            </button>
          </div>
        </form>
      </div>

      <div class="bg-white p-6 rounded-3xl shadow-xl border border-gray-100">
        <h4 class="text-sm font-black text-gray-700 uppercase tracking-wider mb-4">🐾 Mis Mascotas Registradas</h4>
        <p v-if="misMascotas.length === 0" class="text-xs text-gray-400 font-bold ml-1">Aún no tienes compañeros registrados.</p>
        
        <div class="space-y-3">
          <div v-for="m in misMascotas" :key="m.id_mascota" class="border border-gray-100 rounded-2xl overflow-hidden bg-gray-50">
            
            <button @click="m.abierto = !m.abierto" type="button" class="w-full p-4 flex justify-between items-center bg-white hover:bg-gray-50 transition-colors text-left outline-none">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 bg-blue-50 text-sm flex items-center justify-center rounded-xl">🐶</div>
                <div>
                  <span class="font-black text-gray-800 text-sm block">{{ m.nombre }}</span>
                  <span class="text-[10px] text-gray-400 font-bold uppercase tracking-tight">{{ m.especie }} ➔ {{ m.raza || 'Sin raza' }}</span>
                </div>
              </div>
              <span class="text-[10px] text-blue-600 font-black tracking-wider bg-blue-50 px-3 py-1 rounded-full">
                {{ m.abierto ? '▲ OCULTAR' : '▼ VER DETALLES' }}
              </span>
            </button>
            
            <div v-if="m.abierto" class="p-5 space-y-4 bg-gray-50 border-t border-gray-100 text-gray-600 text-xs">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <p class="bg-white p-3 rounded-xl border border-gray-100"><strong>Tamaño de Manto:</strong> {{ m.tamano }}</p>
                <p class="bg-white p-3 rounded-xl border border-gray-100"><strong>Fecha de Nacimiento:</strong> {{ m.fechaNacimiento || 'No registrada' }}</p>
                <p class="col-span-full bg-white p-3 rounded-xl border border-gray-100"><strong>Alergias o Contraindicaciones:</strong> {{ m.alergias || 'Ninguna registrada' }}</p>
                <p class="col-span-full bg-white p-3 rounded-xl border border-gray-100"><strong>Restricciones de Temperamento:</strong> {{ m.restricciones || 'Tranquilo' }}</p>
              </div>

              <div class="bg-white p-4 rounded-xl border border-gray-100 space-y-3">
                <div class="flex justify-between items-center">
                  <div>
                    <span class="font-bold text-gray-700 block">📋 Documento de Vacunas:</span>
                    <span class="text-[10px] text-gray-400 block font-mono max-w-xs truncate">{{ m.vacunas }}</span>
                  </div>
                  
                  <button 
                    v-if="m.vacunas && m.vacunas !== 'No adjuntado'" 
                    @click="abrirVistaPrevia(m.vacunas)"
                    type="button"
                    class="py-1 px-3 bg-blue-50 text-blue-600 font-black rounded-lg hover:bg-blue-100 transition-colors text-[11px]"
                  >
                    👁️ VER VISTA PREVIA
                  </button>
                  <span v-else class="text-gray-400 font-bold text-[11px]">No tiene carnet cargado</span>
                </div>

                <div class="pt-2 border-t border-gray-100">
                  <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Cargar otro carnet (Reemplazar)</label>
                  <div class="flex items-center space-x-2">
                    <input type="file" accept=".pdf,image/*" @change="(e) => cambiarCarnetInline(e, m)" class="w-full text-xs" />
                    <button 
                      @click="guardarNuevoCarnet(m)" 
                      :disabled="!m.nuevoArchivoBinario"
                      class="py-1.5 px-4 bg-green-600 text-white text-[11px] font-black uppercase rounded-lg disabled:opacity-50 hover:bg-green-700 transition-all"
                    >
                      Actualizar
                    </button>
                  </div>
                </div>
              </div>

              <div class="flex justify-end pt-2">
                <button 
                  @click="eliminarMascota(m.id_mascota)" 
                  class="py-2 px-4 bg-red-50 text-red-600 font-black rounded-xl hover:bg-red-600 hover:text-white transition-all uppercase text-[10px] tracking-wider"
                >
                  🗑️ Eliminar Mascota
                </button>
              </div>
            </div>

          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '../services/axiosConfig';

const userSession = JSON.parse(localStorage.getItem('user') || '{}'); 
const idUsuarioLogueado = userSession.id_usuario || 1;

const mostrarFormulario = ref(false);
const loading = ref(false);
const misMascotas = ref([]);
const archivoSeleccionado = ref(null);

const nuevaMascota = ref({
  nombre: '', especie: '', raza: '', tamano: '', fechaNacimiento: '', alergias: '', temperamento: '', vacunas: ''
});

const manejarCargaArchivo = (event) => {
  const file = event.target.files[0];
  if (file) { archivoSeleccionado.value = file; }
};

const cambiarCarnetInline = (event, mascota) => {
  const file = event.target.files[0];
  if (file) {
    mascota.nuevoArchivoBinario = file;
    console.log("📎 [DEBUG FRONTEND] Archivo acoplado inline para actualizar:", file.name);
  }
};

const obtenerMascotas = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/cliente/mascotas/${idUsuarioLogueado}`);
    misMascotas.value = res.data.map(m => ({ ...m, abierto: false, nuevoArchivoBinario: null }));
  } catch (err) {
    console.error("❌ [DEBUG FRONTEND ERROR] Al recuperar lista de mascotas:", err);
  }
};

const abrirVistaPrevia = async (filename) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/cliente/mascotas/vacunas/${filename}`, {
      responseType: 'blob'
    });
    const blobUrl = URL.createObjectURL(response.data);
    window.open(blobUrl, '_blank');
  } catch (err) {
    console.error("❌ [DEBUG FRONTEND ERROR PREVIEW]:", err);
    alert("No se pudo cargar el archivo original.");
  }
};

const agregarMascota = async () => {
  loading.value = true;
  try {
    const formData = new FormData();
    formData.append("nombre", nuevaMascota.value.nombre);
    formData.append("especie", nuevaMascota.value.especie);
    formData.append("raza", nuevaMascota.value.raza || "");
    formData.append("tamano", nuevaMascota.value.tamano);
    formData.append("fechaNacimiento", nuevaMascota.value.fechaNacimiento);
    formData.append("alergias", nuevaMascota.value.alergias || "");
    formData.append("restricciones", nuevaMascota.value.temperamento);
    
    if (archivoSeleccionado.value) { 
      formData.append("file", archivoSeleccionado.value); 
    }

    await axios.post(`http://localhost:8080/api/cliente/mascotas/registrar/${idUsuarioLogueado}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    alert("✨ Mascota guardada exitosamente.");
    nuevaMascota.value = { nombre: '', especie: '', raza: '', tamano: '', fechaNacimiento: '', alergias: '', temperamento: '', vacunas: '' };
    archivoSeleccionado.value = null;
    mostrarFormulario.value = false;
    await obtenerMascotas();
  } catch (err) {
    alert("❌ Error al guardar: " + (err.response?.data?.error || "Error de red"));
  } finally {
    loading.value = false;
  }
};

const guardarNuevoCarnet = async (mascota) => {
  try {
    const formData = new FormData();
    formData.append("nombre", mascota.nombre);
    formData.append("especie", mascota.especie);
    formData.append("raza", mascota.raza || "");
    formData.append("tamano", mascota.tamano);
    formData.append("fechaNacimiento", mascota.fechaNacimiento || "");
    formData.append("alergias", mascota.alergias || "");
    formData.append("restricciones", mascota.restricciones || "");
    formData.append("file", mascota.nuevoArchivoBinario);

    await axios.post(`http://localhost:8080/api/cliente/mascotas/actualizar/${mascota.id_mascota}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    alert("🔄 Carnet actualizado de forma correcta.");
    await obtenerMascotas();
  } catch (err) {
    alert("Error al actualizar carnet: " + (err.response?.data?.error || "Error"));
  }
};

const eliminarMascota = async (idMascota) => {
  if (!confirm("⚠️ ¿Está completamente seguro de eliminar este perfil? Esta acción borrará permanentemente sus documentos físicos y sus citas asociadas.")) return;
  try {
    await axios.delete(`http://localhost:8080/api/cliente/mascotas/${idMascota}`);
    alert("🗑️ Perfil eliminado correctamente.");
    await obtenerMascotas();
  } catch (err) {
    alert("Error al eliminar perfil: " + (err.response?.data?.error || "Error"));
  }
};

onMounted(obtenerMascotas);
</script>