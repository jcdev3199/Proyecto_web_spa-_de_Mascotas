<template>
  <div class="min-h-screen bg-gray-100 p-6">
    <div class="max-w-4xl mx-auto">
      
    <div class="mt-10">
      <button @click="$router.push('/dashboard')" class="bg-black text-white px-8 py-3 rounded-2xl font-black text-xs uppercase hover:bg-gray-800 transition shadow-lg active:scale-95">
        ← Volver al Dashboard
      </button>
    </div>

      <div class="bg-white rounded-3xl shadow-xl overflow-hidden border-4 border-red-600">
        <div class="bg-red-600 p-4">
          <h2 class="text-xl font-black text-white uppercase text-center">Registro de Nuevo Personal</h2>
        </div>

        <form @submit.prevent="registrarEmpleado" class="p-8 grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="space-y-4">
            <h4 class="text-xs font-bold text-gray-400 uppercase tracking-widest">Datos Personales</h4>
            <input v-model="nuevoEmp.nombres" type="text" placeholder="Nombre(s)" class="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-red-500" required />
            <input v-model="nuevoEmp.apellidos" type="text" placeholder="Apellido(s)" class="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-red-500" required />
            
            <h4 class="text-xs font-bold text-gray-400 uppercase tracking-widest pt-2">Credenciales</h4>
            <input v-model="nuevoEmp.username" type="text" placeholder="Username" class="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-red-500" required />
            <input v-model="nuevoEmp.email" type="email" placeholder="Email Institucional" class="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-red-500" required />
            <input v-model="nuevoEmp.password" type="password" placeholder="Contraseña Inicial" class="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-red-500" required />
            <input v-model="nuevoEmp.telefono" type="text" placeholder="Teléfono" class="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-red-500" required />
          </div>

          <div class="space-y-4">
            <h4 class="text-xs font-bold text-gray-400 uppercase tracking-widest">Asignación</h4>
            <select v-model="nuevoEmp.rol" class="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-red-500 bg-white">
              <option value="Groomer">Groomer</option>
              <option value="Recepción">Recepción</option>
            </select>

            <div v-if="nuevoEmp.rol === 'Groomer' || nuevoEmp.rol === 'Recepción'" class="space-y-4 pt-2 border-t border-gray-100">
  <input v-if="nuevoEmp.rol === 'Groomer'" v-model="nuevoEmp.especialidad" type="text" placeholder="Especialidad" class="w-full p-3 border-2 border-red-50 rounded-xl outline-none focus:border-red-500 bg-red-50/20" />

  <div v-if="nuevoEmp.rol === 'Groomer'">
    <label class="text-[10px] font-black text-red-400 uppercase ml-2">Capacidad Simultánea (Mascotas a la vez)</label>
    <input v-model.number="nuevoEmp.capacidadSimultanea" type="number" min="1" max="5" class="w-full p-3 border-2 border-red-50 rounded-xl outline-none focus:border-red-500 bg-red-50/20 font-bold" />
  </div>

  <select v-model="nuevoEmp.turno" class="w-full p-3 border-2 border-red-50 rounded-xl outline-none focus:border-red-500 bg-red-50/20">
    <option value="Mañana">Turno: Mañana</option>
    <option value="Tarde">Turno: Tarde</option>
    <option value="Tarde">Turno: Completo</option>
  </select>

  <div class="grid grid-cols-2 gap-3">
    <div>
      <label class="text-[10px] font-black text-red-400 uppercase ml-2">H. Entrada</label>
      <input v-model="nuevoEmp.horaEntrada" type="time" class="w-full p-2 border-2 border-red-50 rounded-xl focus:border-red-500 outline-none text-center font-bold" />
    </div>
    <div>
      <label class="text-[10px] font-black text-red-400 uppercase ml-2">H. Salida</label>
      <input v-model="nuevoEmp.horaSalida" type="time" class="w-full p-2 border-2 border-red-50 rounded-xl focus:border-red-500 outline-none text-center font-bold" />
    </div>
  </div>
</div>
            

          </div>

          <button type="submit" class="md:col-span-2 bg-red-600 text-white p-4 rounded-2xl font-black uppercase hover:bg-red-700 transition shadow-lg">
            Registrar Personal Interno
          </button>
        </form>
      </div>

      <div class="mt-10 flex justify-center">
        <button @click="$router.push('/admin/usuarios/lista')" class="bg-blue-600 hover:bg-blue-700 text-white px-12 py-4 rounded-2xl font-black uppercase shadow-2xl transition transform hover:scale-105">
          Listas de Usuarios
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from '../../services/axiosConfig';
import { useRouter } from 'vue-router';

const router = useRouter();
const nuevoEmp = ref({
  nombres: '',
  apellidos: '',
  username: '',
  email: '',
  password: '',
  rol: 'Groomer', 
  telefono: '',
  especialidad: '',
  capacidadSimultanea: 1,
  turno: 'Mañana',
  horaEntrada: '08:00',
  horaSalida: '14:00'
});

const registrarEmpleado = async () => {
  console.log(">>> [DEBUG] Iniciando registro. Datos actuales:", nuevoEmp.value);
  
  try {
    const token = localStorage.getItem('token');
    
    const payload = {
      nombres: nuevoEmp.value.nombres,
      apellidos: nuevoEmp.value.apellidos,
      username: nuevoEmp.value.username,
      email: nuevoEmp.value.email,
      password: nuevoEmp.value.password,
      rol: nuevoEmp.value.rol,
      telefono: nuevoEmp.value.telefono,
      especialidad: nuevoEmp.value.rol === 'Groomer' ? nuevoEmp.value.especialidad : null,
      capacidadSimultanea: nuevoEmp.value.rol === 'Groomer' ? nuevoEmp.value.capacidadSimultanea : null,
      turno: nuevoEmp.value.turno,
      horaEntrada: nuevoEmp.value.horaEntrada,
      horaSalida: nuevoEmp.value.horaSalida
    };

    console.log(">>> [DEBUG] Enviando Payload:", payload);

    await axios.post('/api/auth/registrar-personal', payload, {
      params: { rol: nuevoEmp.value.rol },
      headers: { Authorization: `Bearer ${token}` }
    });

    alert("✅ Personal registrado con éxito.");
    router.push('/admin/usuarios/lista');
  } catch (err) {
    const msg = err.response?.data || "No se pudo completar el registro";
    alert("❌ Error: " + (typeof msg === 'object' ? JSON.stringify(msg) : msg));
  }
};
</script>