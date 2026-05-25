<template>
  <div v-if="authState.user" class="min-h-screen bg-gray-50">
    <!-- BARRA SUPERIOR DE NAVEGACIÓN -->
    <header class="bg-white border-b border-gray-200 p-4 sticky top-0 z-10 shadow-sm">
      <div class="max-w-7xl mx-auto flex justify-between items-center">
        <div class="flex flex-col">
          <h1 class="text-xl font-black text-gray-900 tracking-tight">
            PET<span class="text-blue-600">SPA</span> UMSA
          </h1>
          <p class="text-xs text-gray-500 font-medium">
            Hola, <span class="text-blue-600">{{ authState.user?.nombre || authState.user?.username }}</span>
          </p>
        </div>
        
        <div class="flex items-center gap-3">
          <!-- Etiqueta de Rol Dinámica -->
          <span class="hidden sm:inline-block px-3 py-1 rounded-full text-[10px] font-bold bg-blue-50 text-blue-700 uppercase border border-blue-100">
            {{ authState.user?.rol }}
          </span>
          
          <!-- Botón de Salida -->
          <button 
            @click="confirmarSalida"
            class="flex items-center gap-2 bg-red-50 text-red-600 px-4 py-2 rounded-xl text-sm font-bold hover:bg-red-100 transition-all active:scale-95"
          >
            <span>Salir</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>
        </div>
      </div>
    </header>

    <!-- CONTENIDO PRINCIPAL -->
    <main class="max-w-7xl mx-auto p-6">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        
        <!-- SECCIÓN: ADMINISTRADOR (Poder Total) -->
<template v-if="authState.user?.rol === 'Administrador'">
          
          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-purple-100 transition-all">
            <div class="w-12 h-12 bg-purple-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-purple-500 group-hover:scale-110 transition-all">⚙️</div>
            <h3 class="font-bold text-gray-800 text-lg">Gestión de Sistema</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Backups, auditoría forense y configuración de seguridad global.</p>
            <router-link to="/admin/logs" class="block w-full py-2 rounded-xl bg-gray-50 text-purple-600 text-center text-xs font-black hover:bg-purple-100 transition-colors uppercase">
              Ver Logs
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border-2 border-red-50 hover:shadow-xl hover:border-red-500 transition-all">
            <div class="w-12 h-12 bg-red-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-red-600 group-hover:scale-110 transition-all">➕</div>
            <h3 class="font-bold text-gray-800 text-lg">Registro de Personal</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Dar de alta a nuevos Groomers, Recepcionistas y personal interno.</p>
            <router-link to="/admin/usuarios/nuevo" class="block w-full py-2 rounded-xl bg-red-600 text-white text-center text-xs font-black hover:bg-red-700 transition-colors shadow-lg shadow-red-100 uppercase">
              Nuevo Registro
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-100 transition-all">
            <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-blue-600 group-hover:scale-110 transition-all">👥</div>
            <h3 class="font-bold text-gray-800 text-lg">Base de Datos</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Listado maestro de usuarios, edición de roles y estados de cuenta.</p>
            <router-link to="/admin/usuarios/lista" class="block w-full py-2 rounded-xl bg-blue-600 text-white text-center text-xs font-black hover:bg-blue-700 transition-colors shadow-lg shadow-blue-100 uppercase">
              Listas de Usuarios
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-orange-100 transition-all">
            <div class="w-12 h-12 bg-orange-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-orange-500 group-hover:scale-110 transition-all">📊</div>
            <h3 class="font-bold text-gray-800 text-lg">Reportes de Negocio</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Análisis de ventas, nivel de ocupación de citas y productividad.</p>
            <button class="w-full py-2 rounded-xl bg-gray-50 text-orange-600 text-xs font-black hover:bg-orange-100 transition-colors uppercase">Ver Estadísticas</button>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-amber-100 transition-all">
            <div class="w-12 h-12 bg-amber-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-amber-500 group-hover:scale-110 transition-all">🏷️</div>
            <h3 class="font-bold text-gray-800 text-lg">Precios y Servicios</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Configuración de tarifas para peluquería, spa y catálogo.</p>
            <button class="w-full py-2 rounded-xl bg-gray-50 text-amber-600 text-xs font-black hover:bg-amber-100 transition-colors uppercase">Ajustar Tarifas</button>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-indigo-100 transition-all">
            <div class="w-12 h-12 bg-indigo-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-indigo-500 group-hover:scale-110 transition-all">📦</div>
            <h3 class="font-bold text-gray-800 text-lg">Control de Inventario</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Stock global de insumos, proveedores y alertas de reabastecimiento.</p>
            <button class="w-full py-2 rounded-xl bg-gray-50 text-indigo-600 text-xs font-black hover:bg-indigo-100 transition-colors uppercase">Ver Almacén</button>
          </div>

        </template>

        <!-- SECCIÓN: RECEPCIÓN -->
<template v-if="authState.user?.rol === 'Recepción'">
          
          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-green-100 transition-all">
            <div class="w-12 h-12 bg-green-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-green-500 group-hover:scale-110 transition-all">📅</div>
            <h3 class="font-bold text-gray-800 text-lg">Calendario Maestro</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Organizar citas, confirmar reservas y gestionar la disponibilidad del día.</p>
            <router-link 
              to="/recepcion?tab=calendario" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-green-600 text-center text-xs font-black hover:bg-green-50 transition-colors uppercase">
              Gestionar Agenda
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-green-100 transition-all">
            <div class="w-12 h-12 bg-green-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-green-500 group-hover:scale-110 transition-all">📝</div>
            <h3 class="font-bold text-gray-800 text-lg">Admisión y Registro</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Dar de alta a nuevos clientes y sus mascotas en el sistema institucional.</p>
            <router-link 
              to="/recepcion?tab=bandeja" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-green-600 text-center text-xs font-black hover:bg-green-50 transition-colors uppercase">
              Ver Solicitudes
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-green-100 transition-all">
            <div class="w-12 h-12 bg-green-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-green-500 group-hover:scale-110 transition-all">💳</div>
            <h3 class="font-bold text-gray-800 text-lg">Caja y Facturación</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Gestión de cobros por servicios, venta de productos y emisión de facturas.</p>
            <router-link 
              to="/recepcion?tab=pos" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-green-600 text-center text-xs font-black hover:bg-green-50 transition-colors uppercase">
              Procesar Pago
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-green-100 transition-all">
            <div class="w-12 h-12 bg-green-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-green-500 group-hover:scale-110 transition-all">⚙️</div>
            <h3 class="font-bold text-gray-800 text-lg">Control de Servicios</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Coordinar con Groomers y monitorear el estado de los servicios activos.</p>
            <router-link 
              to="/recepcion?tab=bloqueos" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-green-600 text-center text-xs font-black hover:bg-green-50 transition-colors uppercase">
              Ver Seguimiento
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-100 transition-all">
            <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-blue-500 group-hover:scale-110 transition-all">👤</div>
            <h3 class="font-bold text-gray-800 text-lg">Mi Perfil</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Información del personal de recepción y contacto administrativo.</p>
            <router-link 
              to="/perfil-empleado" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-blue-600 text-center text-xs font-black hover:bg-blue-100 transition-colors uppercase">
              Ver Mi Ficha
            </router-link>
          </div>

        </template>

        <!-- SECCIÓN: GROOMER -->
<template v-if="authState.user?.rol === 'Groomer'">
          
          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-purple-100 transition-all">
            <div class="w-12 h-12 bg-purple-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-purple-500 group-hover:scale-110 transition-all">✂️</div>
            <h3 class="font-bold text-gray-800 text-lg">Agenda de Hoy</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Ver mascotas asignadas, iniciar atención y realizar el cierre técnico del servicio.</p>
            <router-link 
              to="/groomer" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-purple-600 text-center text-xs font-black hover:bg-purple-50 transition-colors uppercase">
              Ver Mi Agenda
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-purple-100 transition-all">
            <div class="w-12 h-12 bg-purple-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-purple-500 group-hover:scale-110 transition-all">✅</div>
            <h3 class="font-bold text-gray-800 text-lg">Checklist Técnico</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Registro de estado de salud, condiciones de piel y requerimientos de estética.</p>
            <router-link 
              to="/groomer" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-purple-600 text-center text-xs font-black hover:bg-purple-50 transition-colors uppercase">
              Completar Ficha
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-purple-100 transition-all">
            <div class="w-12 h-12 bg-purple-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-purple-500 group-hover:scale-110 transition-all">🧴</div>
            <h3 class="font-bold text-gray-800 text-lg">Insumos Asignados</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Gestión de stock de champús, lociones y materiales utilizados en el turno.</p>
            <router-link 
              to="/groomer" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-purple-600 text-center text-xs font-black hover:bg-purple-50 transition-colors uppercase">
              Reportar Consumo
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-purple-100 transition-all">
            <div class="w-12 h-12 bg-purple-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-purple-500 group-hover:scale-110 transition-all">📸</div>
            <h3 class="font-bold text-gray-800 text-lg">Registro Fotográfico</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Cargar fotos del "antes y después" para enviar evidencias al dueño de la mascota.</p>
            <router-link 
              to="/groomer" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-purple-600 text-center text-xs font-black hover:bg-purple-50 transition-colors uppercase">
              Subir Fotos
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-purple-100 transition-all">
            <div class="w-12 h-12 bg-purple-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-purple-500 group-hover:scale-110 transition-all">👤</div>
            <h3 class="font-bold text-gray-800 text-lg">Mi Perfil</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Datos técnicos, especialidad de estilismo y horarios de atención.</p>
            <router-link 
              to="/perfil-empleado" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-purple-600 text-center text-xs font-black hover:bg-purple-100 transition-colors uppercase">
              Mi Perfil Técnico
            </router-link>
          </div>

        </template>

        <!-- SECCIÓN: CLIENTE (Limitado) -->
        <template v-if="authState.user?.rol === 'Cliente'">
          
        <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-100 transition-all">
            <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-blue-500 group-hover:scale-110 transition-all">📅</div>
            <h3 class="font-bold text-gray-800 text-lg">Solicitar Cita</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Reserva un espacio para peluquería o atención especializada.</p>
            
            <router-link 
              to="/solicitar-cita" 
              class="block w-full py-2 rounded-xl bg-blue-600 text-white text-center text-xs font-black hover:bg-blue-700 transition-colors shadow-lg shadow-blue-100"
            >
              AGENDAR AHORA
            </router-link>
          </div>

<div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-100 transition-all">
    <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-blue-500 group-hover:scale-110 transition-all">🐾</div>
    <h3 class="font-bold text-gray-800 text-lg">Mis Mascotas</h3>
    <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Gestionar perfiles, vacunas y recordatorios de tus compañeros.</p>
    
    <router-link 
      to="/mis-mascotas" 
      class="block w-full py-2 rounded-xl bg-gray-50 text-blue-600 text-center text-xs font-black hover:bg-blue-50 transition-colors"
    >
      VER MIS PETS
    </router-link>
  </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-100 transition-all">
            <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-blue-500 group-hover:scale-110 transition-all">🛍️</div>
            <h3 class="font-bold text-gray-800 text-lg">Catálogo</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Explora alimentos, juguetes y accesorios disponibles en tienda.</p>
            <button class="w-full py-2 rounded-xl bg-gray-50 text-blue-600 text-xs font-black hover:bg-blue-50 transition-colors">VER TIENDA</button>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-100 transition-all">
            <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-blue-500 group-hover:scale-110 transition-all">📋</div>
            <h3 class="font-bold text-gray-800 text-lg">Historial</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Revisa tus citas pasadas, facturas y tratamientos realizados.</p>
            <router-link 
              to="/historial-cliente" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-blue-600 text-center text-xs font-black hover:bg-blue-100 transition-colors uppercase"
            >
              VER REPORTES
            </router-link>
          </div>

          <div class="group bg-white p-6 rounded-3xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-100 transition-all">
            <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-xl mb-4 group-hover:bg-blue-500 group-hover:scale-110 transition-all">👤</div>
            <h3 class="font-bold text-gray-800 text-lg">Mi Perfil</h3>
            <p class="text-sm text-gray-500 mt-2 mb-4 line-clamp-2">Actualiza tu información personal, dirección y datos de contacto.</p>
            <router-link 
              to="/perfil" 
              class="block w-full py-2 rounded-xl bg-gray-50 text-blue-600 text-center text-xs font-black hover:bg-blue-100 transition-colors"
            >
              EDITAR PERFIL
            </router-link>
          </div>

        </template>

      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { authState } from '../store';

const route = useRoute();
const router = useRouter();

// FUNCIÓN DE SALIDA
const confirmarSalida = () => {
  console.log(">>> [DEBUG DASHBOARD] Iniciando proceso de salida...");
  if (confirm("¿Estás seguro de que quieres cerrar sesión?")) {
    console.log(">>> [DEBUG DASHBOARD] Confirmación aceptada.");
    authState.logout(); 
    // Usamos window.location para forzar la limpieza total y evitar el botón atrás
    window.location.href = 'http://localhost:5173/';
  } else {
    console.log(">>> [DEBUG DASHBOARD] Salida cancelada por el usuario.");
  }
}

onMounted(() => {
  console.log(">>> [DEBUG DASHBOARD] Componente montado.");
  
  // 1. CAPTURAR DATOS DE GOOGLE
  const token = route.query.token;
  const nombre = route.query.nombre;
  const rol = route.query.rol;

  if (token && rol) {
    console.log(">>> [DEBUG DASHBOARD] Datos de Google detectados en URL.");
    // Si Google nos mandó datos, guardamos la sesión
    authState.setAuth({ token, nombre, rol });
    // Limpiamos la URL para seguridad y estética
    router.replace('/dashboard');
  } else if (!authState.user) {
    console.warn(">>> [DEBUG DASHBOARD] No hay sesión activa. Redirigiendo al login.");
    // 2. SEGURIDAD: Si no hay usuario, regresa al login
    router.push('/login');
  } else {
    console.log(">>> [DEBUG DASHBOARD] Sesión de usuario válida:", authState.user.nombre);
  }
});
</script>