<!-- Campos obligatorios del requerimiento 4[cite: 4] -->
<div class="space-y-4 p-6 bg-gray-50 rounded-xl">
  <h3 class="font-bold">Registrar Nuevo Empleado</h3>
  <input v-model="nuevoUser.email" type="email" placeholder="Email institucional" class="input-pet" />
  <select v-model="nuevoUser.rol" class="input-pet">
    <option value="Groomer">Groomer</option>
    <option value="Recepción">Recepción</option>
  </select>
  <!-- Campos específicos de Personal[cite: 4] -->
  <input v-model="nuevoUser.especialidad" v-if="nuevoUser.rol === 'Groomer'" placeholder="Ej: Corte Tijera" class="input-pet" />
  <select v-model="nuevoUser.turno" class="input-pet">
    <option value="Mañana">Mañana</option>
    <option value="Tarde">Tarde</option>
  </select>
  <button @click="crearEmpleado" class="bg-red-600 text-white px-4 py-2 rounded-lg font-bold">Crear Cuenta</button>
</div>