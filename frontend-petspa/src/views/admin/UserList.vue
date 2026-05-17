<template>
  <div class="p-8 bg-gray-50 min-h-screen">
    <!-- Encabezado -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
      <div>
        <h1 class="text-4xl font-black uppercase text-black tracking-tighter">Registro de Usuarios</h1>
        <p class="text-gray-500 font-medium mt-1">Panel de control administrativo e historial</p>
      </div>
      <router-link to="/admin/usuarios/nuevo" class="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-2xl font-bold shadow-lg shadow-blue-100 transition transform hover:scale-105 active:scale-95">
        + AÑADIR EMPLEADO
      </router-link>
    </div>

    <!-- Buscador y Filtros -->
    <div class="mb-6 flex flex-col md:flex-row gap-4">
      <div class="relative flex-1">
        <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-gray-400">🔍</span>
        <input v-model="searchTerm" type="text" placeholder="Buscar por nombre o email..." class="w-full pl-11 pr-4 py-3 bg-white border border-gray-200 rounded-2xl focus:ring-2 focus:ring-blue-500 outline-none shadow-sm"/>
      </div>
      <div class="flex gap-2">
        <button @click="toggleSort('username')" class="px-5 py-3 bg-white border border-gray-200 rounded-2xl font-bold text-xs uppercase hover:bg-gray-50 flex items-center gap-2 shadow-sm">
          Nombre {{ sortColumn === 'username' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
        </button>
        <button @click="toggleSort('id_rol')" class="px-5 py-3 bg-white border border-gray-200 rounded-2xl font-bold text-xs uppercase hover:bg-gray-50 flex items-center gap-2 shadow-sm">
          Rol {{ sortColumn === 'id_rol' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
        </button>
      </div>
    </div>

    <!-- Tabla de Usuarios -->
    <div class="bg-white rounded-[2rem] shadow-sm border border-gray-100 overflow-hidden">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr class="bg-[#0f172a] text-white uppercase text-[11px] font-black tracking-[0.2em]">
            <th class="p-6">Usuario / Email</th>
            <th class="p-6 text-center">Rol</th>
            <th class="p-6 text-center">Estado</th>
            <th class="p-6 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-for="user in filteredUsers" :key="user.id_usuario" class="hover:bg-gray-50/80 transition-colors">
            <td class="p-6">
              <div class="flex flex-col">
                <span class="font-bold text-gray-900 text-lg">{{ user.username }}</span>
                <span class="text-sm text-gray-500 font-medium">{{ user.email }}</span>
              </div>
            </td>
            <td class="p-6 text-center">
              <span class="px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-wider border-2" :class="getRolEstilo(user.id_rol)">
                {{ getRolNombre(user.id_rol) }}
              </span>
            </td>
            <td class="p-6 text-center">
              <span class="px-3 py-1 rounded-lg text-[10px] font-black uppercase border" :class="user.estado === 'Activo' ? 'bg-green-50 text-green-600 border-green-200' : 'bg-red-50 text-red-600 border-red-200'">
                {{ user.estado }}
              </span>
            </td>
            <td class="p-6 text-right">
              <div class="flex justify-end gap-2">
                <button @click="abrirGestion(user)" class="px-4 py-2 bg-gray-100 hover:bg-black hover:text-white text-gray-900 rounded-xl transition-all font-black text-[10px] uppercase tracking-widest active:scale-95 border border-transparent shadow-sm">
                  Gestionar
                </button>
                <button @click="confirmarEliminar(user)" class="px-4 py-2 bg-red-50 hover:bg-red-600 hover:text-white text-red-600 rounded-xl transition-all font-black text-[10px] uppercase tracking-widest active:scale-95 border border-red-100 shadow-sm">
                  Eliminar
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- MODAL DE GESTIÓN -->
    <div v-if="modalAbierto" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      <div class="bg-white w-full max-w-2xl rounded-[2.5rem] shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200">
        <!-- Header -->
        <div class="p-8 bg-gray-900 text-white flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-black uppercase tracking-tight">Modificar Credenciales</h2>
            <p class="text-blue-400 text-xs font-bold uppercase tracking-widest">{{ getRolNombre(userEdit.id_rol) }} ID: {{ userEdit.id_usuario }}</p>
          </div>
          <button @click="modalAbierto = false" class="text-gray-400 hover:text-white text-2xl">×</button>
        </div>

        <div class="p-8 max-h-[75vh] overflow-y-auto custom-scrollbar">
          <form @submit.prevent="guardarCambios" class="space-y-6">
            
            <!-- DATOS BÁSICOS -->
            <div class="grid grid-cols-2 gap-4">
              <div class="flex flex-col gap-1">
                <label class="text-[10px] font-black text-gray-400 uppercase ml-2">Nombre de Usuario</label>
                <input v-model="userEdit.username" type="text" class="p-3 bg-gray-50 border border-gray-200 rounded-2xl focus:ring-2 focus:ring-blue-500 outline-none font-bold text-gray-700"/>
              </div>
              <div class="flex flex-col gap-1">
                <label class="text-[10px] font-black text-gray-400 uppercase ml-2">Estado</label>
                <select v-model="userEdit.estado" class="p-3 bg-gray-50 border border-gray-200 rounded-2xl focus:ring-2 focus:ring-blue-500 outline-none font-bold text-gray-700">
                  <option value="Activo">ACTIVO</option>
                  <option value="Inactivo">INACTIVO</option>
                  <option value="Bloqueado">BLOQUEADO</option>
                </select>
              </div>
            </div>

            <!-- SEGURIDAD: CAMBIO DE CONTRASEÑA (REQUERIMIENTOS ESPECÍFICOS) -->
            <div class="p-6 bg-yellow-50/50 rounded-3xl border border-yellow-100 space-y-4">
              <div class="flex justify-between items-center">
                <h4 class="text-[10px] font-black text-yellow-700 uppercase tracking-widest">Seguridad de Acceso (Opcional)</h4>
                <span class="text-[9px] font-black px-2 py-0.5 rounded bg-yellow-200 text-yellow-800 uppercase">Fuerza: {{ passwordLabel }}</span>
              </div>
              
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="flex flex-col gap-1">
                  <input v-model="userEdit.newPassword" type="password" placeholder="Nueva contraseña" class="p-3 bg-white border border-yellow-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-yellow-400"/>
                </div>
                <div class="flex flex-col gap-1">
                  <input v-model="userEdit.confirmPassword" type="password" placeholder="Confirmar contraseña" class="p-3 bg-white border border-yellow-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-yellow-400"/>
                </div>
              </div>

              <!-- Medidor de Fuerza Visual -->
              <div class="h-1.5 w-full bg-gray-200 rounded-full overflow-hidden flex gap-1">
                <div v-for="n in 4" :key="n" class="h-full flex-1 transition-all duration-500"
                     :class="n <= strengthScore ? strengthColor : 'bg-gray-200'"></div>
              </div>

              <!-- Sugerencia Proactiva y Requisitos -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-1">
                  <p class="text-[9px] font-black text-yellow-800 uppercase">Sugerencia Proactiva:</p>
                  <p class="text-[10px] text-yellow-700 leading-tight">Usa <strong>Passphrases</strong>: frases de 3 o 4 palabras comunes pero aleatorias (ej: PerroAzulLlaveSol!).</p>
                </div>
                <ul class="text-[9px] font-bold space-y-0.5 uppercase">
                  <li :class="passwordCriteria.length ? 'text-green-600' : 'text-gray-400'">• Mínimo 8 caracteres</li>
                  <li :class="passwordCriteria.caps ? 'text-green-600' : 'text-gray-400'">• Mayúsculas y Minúsculas</li>
                  <li :class="passwordCriteria.nums ? 'text-green-600' : 'text-gray-400'">• Números</li>
                  <li :class="passwordCriteria.syms ? 'text-green-600' : 'text-gray-400'">• Símbolos (*, #, !)</li>
                </ul>
              </div>
            </div>

<!-- CAMPOS ESPECÍFICOS: GROOMER -->
<div v-if="userEdit.id_rol === 3" class="p-6 bg-purple-50/50 rounded-3xl border border-purple-100 space-y-4">
  <h4 class="text-xs font-black text-purple-600 uppercase tracking-widest">Configuración Laboral Groomer</h4>
  <div class="grid grid-cols-2 gap-4">
    <!-- Teléfono -->
    <div class="flex flex-col gap-1 col-span-1">
      <label class="text-[10px] font-bold text-purple-400 ml-2">TELÉFONO DE CONTACTO</label>
      <input v-model="userEdit.groomer.telefono" type="text" class="p-3 bg-white border border-purple-100 rounded-xl text-sm outline-none"/>
    </div>
    
    <!-- CAMPO AGREGADO: ESPECIALIDAD -->
    <div class="flex flex-col gap-1 col-span-1">
      <label class="text-[10px] font-bold text-purple-400 ml-2">ESPECIALIDAD</label>
      <input v-model="userEdit.groomer.especialidad" type="text" placeholder="Ej. Corte canino, gatos" class="p-3 bg-white border border-purple-100 rounded-xl text-sm outline-none"/>
    </div>

    <div class="flex flex-col gap-1">
      <label class="text-[10px] font-bold text-purple-400 ml-2">HORA ENTRADA</label>
      <input v-model="userEdit.groomer.hora_entrada" type="time" class="p-3 bg-white border border-purple-100 rounded-xl text-sm outline-none"/>
    </div>
    <div class="flex flex-col gap-1">
      <label class="text-[10px] font-bold text-purple-400 ml-2">HORA SALIDA</label>
      <input v-model="userEdit.groomer.hora_salida" type="time" class="p-3 bg-white border border-purple-100 rounded-xl text-sm outline-none"/>
    </div>
    <div class="flex flex-col gap-1">
      <label class="text-[10px] font-bold text-purple-400 ml-2">TURNO</label>
      <select v-model="userEdit.groomer.turno" class="p-3 bg-white border border-purple-100 rounded-xl text-sm outline-none">
        <option value="Mañana">MAÑANA</option>
        <option value="Tarde">TARDE</option>
        <option value="Completo">COMPLETO</option>
      </select>
    </div>
    <div class="flex flex-col gap-1">
      <label class="text-[10px] font-bold text-purple-400 ml-2">CAPACIDAD</label>
      <input v-model="userEdit.groomer.capacidad_simultanea" type="number" class="p-3 bg-white border border-purple-100 rounded-xl text-sm outline-none"/>
    </div>
  </div>
</div>

            <!-- CAMPOS ESPECÍFICOS: CLIENTE -->
            <div v-if="userEdit.id_rol === 4" class="p-6 bg-blue-50/50 rounded-3xl border border-blue-100 space-y-4">
              <h4 class="text-xs font-black text-blue-600 uppercase tracking-widest">Ficha de Cliente</h4>
              <div class="grid grid-cols-2 gap-4">
                <input v-model="userEdit.cliente.nombre" type="text" placeholder="Nombre" class="p-3 bg-white border border-blue-100 rounded-xl text-sm outline-none"/>
                <input v-model="userEdit.cliente.ci" type="text" placeholder="C.I." class="p-3 bg-white border border-blue-100 rounded-xl text-sm outline-none"/>
                <input v-model="userEdit.cliente.telefono" type="text" placeholder="Teléfono" class="p-3 bg-white border border-blue-100 rounded-xl text-sm outline-none col-span-2"/>
                <textarea v-model="userEdit.cliente.direccion" placeholder="Dirección" class="p-3 bg-white border border-blue-100 rounded-xl text-sm outline-none col-span-2 h-20"></textarea>
              </div>
            </div>

            <div class="flex gap-3 pt-4">
              <button type="button" @click="modalAbierto = false" class="flex-1 py-4 bg-gray-100 text-gray-500 rounded-2xl font-bold uppercase text-xs hover:bg-gray-200 transition">Cancelar</button>
              <button type="submit" class="flex-1 py-4 bg-blue-600 text-white rounded-2xl font-black uppercase text-xs shadow-lg shadow-blue-200 hover:bg-blue-700 transition">Sincronizar Cambios</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import axios from '../../services/axiosConfig';
import { useRouter } from 'vue-router';

const usuarios = ref([]);
const loading = ref(true);
const searchTerm = ref('');
const sortColumn = ref('username');
const sortOrder = ref('asc');
const router = useRouter();

// Modal de Gestión
const modalAbierto = ref(false);
const userEdit = ref({
  id_usuario: null,
  username: '',
  estado: '',
  id_rol: null,
  newPassword: '',
  confirmPassword: '',
  cliente: { nombre: '', ci: '', telefono: '', direccion: '' },
  groomer: { telefono: '', especialidad: '', turno: 'Mañana', capacidad_simultanea: 1, hora_entrada: '08:00', hora_salida: '16:00' }
});

// Lógica de Validación de Contraseña
const passwordCriteria = computed(() => {
  const p = userEdit.value.newPassword || '';
  return {
    length: p.length >= 8,
    caps: /[A-Z]/.test(p) && /[a-z]/.test(p),
    nums: /\d/.test(p),
    syms: /[!@#$%^&*(),.?":{}|<>]/.test(p)
  };
});

const strengthScore = computed(() => {
  if (!userEdit.value.newPassword) return 0;
  let score = 0;
  if (passwordCriteria.value.length) score++;
  if (passwordCriteria.value.caps) score++;
  if (passwordCriteria.value.nums) score++;
  if (passwordCriteria.value.syms) score++;
  return score;
});

const strengthColor = computed(() => {
  const colors = ['bg-gray-200', 'bg-red-500', 'bg-orange-500', 'bg-yellow-500', 'bg-green-500'];
  return colors[strengthScore.value];
});

const passwordLabel = computed(() => {
  const labels = ['Nula', 'Débil', 'Media', 'Buena', 'Excelente'];
  return labels[strengthScore.value];
});

const filteredUsers = computed(() => {
  let result = [...usuarios.value];
  if (searchTerm.value) {
    const term = searchTerm.value.toLowerCase();
    result = result.filter(u => u.username.toLowerCase().includes(term) || u.email.toLowerCase().includes(term));
  }
  result.sort((a, b) => {
    let valA = a[sortColumn.value];
    let valB = b[sortColumn.value];
    if (typeof valA === 'string') valA = valA.toLowerCase();
    if (typeof valB === 'string') valB = valB.toLowerCase();
    if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1;
    if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1;
    return 0;
  });
  return result;
});

const toggleSort = (column) => {
  if (sortColumn.value === column) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortColumn.value = column;
    sortOrder.value = 'asc';
  }
};

const abrirGestion = (user) => {
  console.log(">>> [DEBUG VUE] Datos recibidos para autocompletar:", user);
  
  const db = JSON.parse(JSON.stringify(user));

  userEdit.value = {
    id_usuario: db.id_usuario,
    username: db.username,
    estado: db.estado,
    id_rol: db.id_rol,
    newPassword: '',
    confirmPassword: '',

    // AUTOCOMPLETADO DE CLIENTE (Prioridad a la base de datos)
    cliente: {
      nombre: db.cliente?.nombre || '',
      ci: db.cliente?.ci || '',
      telefono: db.cliente?.telefono || '',
      direccion: db.cliente?.direccion || ''
    },

    // Mantenemos la lógica de Groomer que ya funciona
    groomer: {
      telefono: db.groomer?.telefono || '',
      especialidad: db.groomer?.especialidad || '',
      hora_entrada: db.groomer?.hora_entrada || '', 
      hora_salida: db.groomer?.hora_salida || '',
      turno: db.groomer?.turno || '',
      capacidad_simultanea: db.groomer?.capacidad_simultanea || 1
    }
  };

  modalAbierto.value = true;
};

const confirmarEliminar = (user) => {
  const confirmacion = confirm(`¿ESTÁ SEGURO DE ELIMINAR PERMANENTEMENTE AL USUARIO: ${user.username}?\nEsta acción no se puede deshacer.`);
  if (confirmacion) {
    usuarios.value = usuarios.value.filter(u => u.id_usuario !== user.id_usuario);
    alert("Usuario eliminado (Localmente)");
  }
};

const guardarCambios = async () => {
  console.log(">>> [DEBUG VUE] Iniciando proceso de guardado...");

  // Validación de seguridad de contraseña
  if (userEdit.value.newPassword) {
    console.log(">>> [DEBUG VUE] Validando nueva contraseña...");
    if (strengthScore.value < 4) {
      alert("La contraseña no es lo suficientemente segura.");
      return;
    }
    if (userEdit.value.newPassword !== userEdit.value.confirmPassword) {
      alert("Las contraseñas no coinciden.");
      return;
    }
  }

  // PREPARAR PAYLOAD (El objeto que viaja al backend)
  const payload = {
    idUsuario: userEdit.value.id_usuario,
    username: userEdit.value.username,
    estado: userEdit.value.estado,
    newPassword: userEdit.value.newPassword || null,
    groomer: userEdit.value.id_rol === 3 ? userEdit.value.groomer : null,
    cliente: userEdit.value.id_rol === 4 ? userEdit.value.cliente : null
  };

  console.log(">>> [DEBUG VUE] Objeto enviado al Backend:", JSON.stringify(payload, null, 2));

  try {
    const token = localStorage.getItem('token');
    const res = await axios.put(`http://localhost:8080/api/admin/usuarios/actualizar/${payload.idUsuario}`, payload, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    console.log(">>> [DEBUG VUE] Respuesta exitosa del servidor:", res.data);
    alert("¡Usuario sincronizado correctamente!");
    modalAbierto.value = false;
    cargarUsuarios(); // Recargar la tabla para ver los cambios

  } catch (err) {
    console.error(">>> [DEBUG VUE] ERROR AL SINCRONIZAR:");
    if (err.response) {
      console.error("Status:", err.response.status);
      console.error("Data:", err.response.data);
      alert(`Error del servidor: ${err.response.data.message || 'Error desconocido'}`);
    } else {
      console.error("Error de conexión:", err.message);
      alert("No se pudo conectar con el servidor.");
    }
  }
};

const getRolNombre = (id) => {
  const roles = { 1: 'Administrador', 2: 'Recepción', 3: 'Groomer', 4: 'Cliente' };
  return roles[id] || 'Invitado';
};

const getRolEstilo = (id) => {
  const estilos = {
    1: 'bg-purple-50 text-purple-700 border-purple-100',
    2: 'bg-orange-50 text-orange-700 border-orange-100',
    3: 'bg-blue-50 text-blue-700 border-blue-100',
    4: 'bg-gray-50 text-gray-700 border-gray-100'
  };
  return estilos[id] || 'bg-gray-50';
};

const cargarUsuarios = async () => {
  loading.value = true;
  try {
    const token = localStorage.getItem('token');
    const res = await axios.get('http://localhost:8080/api/admin/usuarios/lista', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    usuarios.value = res.data;
  } catch (err) {
    if (err.response?.status === 401) router.push('/login');
  } finally {
    loading.value = false;
  }
};

onMounted(cargarUsuarios);
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}
</style>