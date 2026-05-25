<template>
  <div class="min-h-screen bg-gray-50 p-6 no-print">
    <div class="max-w-6xl mx-auto space-y-6">
      
      <header class="flex flex-col sm:flex-row justify-between items-start sm:items-center bg-white p-6 rounded-3xl border border-gray-100 shadow-sm gap-4">
        <div>
          <button @click="$router.push('/dashboard')" class="text-blue-600 font-black text-[10px] mb-1 hover:underline uppercase tracking-wider block">
            ← Volver al Menú Principal
          </button>
          <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">Mi Historial del Spa</h2>
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Reporte Centralizado de Turnos, Facturas y Tratamientos Estéticos</p>
        </div>
        <div class="flex flex-wrap gap-2">
          <button v-for="tab in ['citas', 'facturas', 'fichas']" :key="tab" @click="activeTab = tab"
                  :class="['px-4 py-2 text-xs font-black uppercase tracking-wider rounded-xl transition-all', activeTab === tab ? 'bg-blue-600 text-white shadow-lg shadow-blue-100' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            {{ tab === 'citas' ? '📅 Mis Citas' : tab === 'facturas' ? '💳 Pagos y Facturas' : '🧼 Tratamientos Grooming' }}
          </button>
        </div>
      </header>

      <div v-if="activeTab === 'citas'" class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4">
        <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider border-b pb-2">📅 Cronología de Solicitudes y Turnos</h3>
        <div class="overflow-x-auto">
          <table class="w-full border-collapse text-left text-xs">
            <thead>
              <tr class="bg-gray-50 text-gray-400 font-black uppercase text-[10px] border-b">
                <th class="p-3">Turno Ref</th>
                <th class="p-3">Fecha</th>
                <th class="p-3">Mascota</th>
                <th class="p-3">Servicio Solicitado</th>
                <th class="p-3">Horario Reservado</th>
                <th class="p-3 text-center">Estado</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="c in listaCitas" :key="c.id_cita" class="border-b hover:bg-gray-50/50 font-medium text-gray-600">
                <td class="p-3 font-mono font-bold">#00{{ c.id_cita }}</td>
                <td class="p-3">{{ c.fecha }}</td>
                <td class="p-3 font-bold text-gray-800">🐾 {{ c.mascota?.nombre || 'Mi Mascota' }}</td>
                <td class="p-3">{{ c.servicio?.nombreServicio || 'Grooming General' }}</td>
                <td class="p-3 font-mono">{{ c.horaInicio.substring(0,5) }} - {{ c.horaFinEstimada.substring(0,5) }}</td>
                <td class="p-3 text-center">
                  <span class="px-2 py-0.5 rounded text-[10px] uppercase font-black border tracking-wider"
                        :class="c.estado === 'PENDIENTE' ? 'bg-yellow-50 text-yellow-600 border-yellow-200' : c.estado === 'CONFIRMADA' ? 'bg-blue-50 text-blue-600 border-blue-200' : c.estado === 'PAGADA' ? 'bg-purple-50 text-purple-600 border-purple-200' : c.estado === 'CANCELADA' ? 'bg-red-50 text-red-600 border-red-200' : 'bg-green-50 text-green-700 border-green-200'">
                    {{ c.estado }}
                  </span>
                </td>
              </tr>
              <tr v-if="listaCitas.length === 0">
                <td colspan="6" class="p-4 text-center text-gray-400 font-bold">Aún no registras solicitudes en el sistema de agenda.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === 'facturas'" class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4">
        <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider border-b pb-2">💳 Comprobantes de Pago Electrónicos</h3>
        <div class="overflow-x-auto">
          <table class="w-full border-collapse text-left text-xs">
            <thead>
              <tr class="bg-gray-50 text-gray-400 font-black uppercase text-[10px] border-b">
                <th class="p-3">Nro Factura</th>
                <th class="p-3">Fecha de Pago</th>
                <th class="p-3">Cita Código</th>
                <th class="p-3">Modalidad</th>
                <th class="p-3 text-right">Monto Liquidado</th>
                <th class="p-3 text-center">Acción</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="f in listaFacturas" :key="f.id_factura" class="border-b hover:bg-gray-50/50 font-medium text-gray-600">
                <td class="p-3 font-mono font-bold text-gray-800">{{ f.numeroFactura }}</td>
                <td class="p-3">{{ f.fecha_emision }}</td>
                <td class="p-3">#{{ f.idCita }}</td>
                <td class="p-3"><span class="px-2 py-0.5 bg-gray-100 rounded font-bold uppercase text-[9px]">{{ f.metodoPago }}</span></td>
                <td class="p-3 text-right font-black text-gray-900">Bs. {{ f.total }}</td>
                <td class="p-3 text-center">
                  <button @click="reimprimirFacturaCliente(f)" class="px-3 py-1 bg-blue-50 text-blue-600 hover:bg-blue-600 hover:text-white text-[10px] font-black uppercase rounded-lg transition-all shadow-xs">
                    🖨️ Ver Recibo
                  </button>
                </td>
              </tr>
              <tr v-if="listaFacturas.length === 0">
                <td colspan="6" class="p-4 text-center text-gray-400 font-bold">No registras transacciones comerciales liquidadas.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

<div v-if="activeTab === 'fichas'" class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4">
        <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider border-b pb-2">🧼 Informes de Estética y Control Canino</h3>
        <p v-if="listaFichas.length === 0" class="text-xs text-gray-400 font-bold">Tus mascotas aún no cuentan con informes de cierre técnico.</p>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div v-for="ficha in listaFichas" :key="ficha.id_ficha" class="p-5 bg-gray-50 rounded-2xl border border-gray-100 flex flex-col justify-between shadow-sm space-y-4">
            
            <div class="space-y-3">
              <div class="flex justify-between items-center border-b pb-2">
                <span class="text-[10px] font-mono font-black text-purple-600 bg-purple-50 px-2 py-0.5 rounded">INFORME REF: #{{ ficha.id_ficha }}</span>
                <span class="text-[10px] font-bold text-gray-400">Cita Vinc: #{{ ficha.idCita }}</span>
              </div>
              <div class="text-xs space-y-1.5 text-gray-600">
                <p>📋 <b>Diagnóstico de Ingreso:</b> {{ ficha.estadoIngreso }}</p>
                <p>🎭 <b>Temperamento:</b> <span class="font-bold text-purple-600 uppercase text-[10px]">{{ ficha.temperamento }}</span></p>
                <p class="bg-white p-2.5 rounded-xl border border-gray-100 mt-2">💡 <b>Consejo del Estilista:</b> {{ ficha.observaciones }}</p>
              </div>
            </div>

            <div v-if="ficha.fotoAntes || ficha.fotoDespues" class="grid grid-cols-2 gap-3 pt-3 border-t border-gray-200">
              <div class="space-y-1">
                <span class="text-[9px] font-black text-gray-400 uppercase block tracking-wider text-center">📸 Ingreso (Antes)</span>
                <div class="w-full h-32 rounded-xl bg-gray-200 border border-gray-300 overflow-hidden relative shadow-inner">
                  <img v-if="ficha.fotoAntes" :src="ficha.fotoAntes" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-[10px]">Sin Registro</div>
                </div>
              </div>
              
              <div class="space-y-1">
                <span class="text-[9px] font-black text-gray-400 uppercase block tracking-wider text-center">✨ Cierre (Después)</span>
                <div class="w-full h-32 rounded-xl bg-gray-200 border border-gray-300 overflow-hidden relative shadow-inner">
                  <img v-if="ficha.fotoDespues" :src="ficha.fotoDespues" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-[10px]">Sin Registro</div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>

    </div>
  </div>

  <div id="area-impresion-exclusiva" class="print-only font-mono p-8 text-xs space-y-4" v-if="reciboActivo">
    <div class="text-center font-black border-b border-black pb-2">
      <p class="text-lg">PET SPA & GROOMING</p>
      <p class="text-[10px]">Copia de Control Oficial del Cliente</p>
    </div>
    <div class="space-y-1">
      <p><b>NRO FACTURA:</b> {{ reciboActivo.numeroFactura }}</p>
      <p><b>FECHA LIQUIDACIÓN:</b> {{ reciboActivo.fecha_emision }}</p>
      <p><b>MODALIDAD PAGO:</b> {{ reciboActivo.metodoPago }}</p>
    </div>
    <div class="border-b border-t py-2 space-y-0.5">
      <p><b>Referencia Cita:</b> #{{ reciboActivo.idCita }}</p>
      <p><b>Subtotal Neto (87%):</b> Bs. {{ reciboActivo.subtotal }}</p>
      <p><b>IVA Crédito Fiscal (13%):</b> Bs. {{ reciboActivo.impuestos }}</p>
    </div>
    <div class="text-right pt-2">
      <p class="text-base font-black">TOTAL LIQUIDADO: Bs. {{ reciboActivo.total }}</p>
    </div>
    <p class="text-center text-[9px] pt-6 tracking-widest border-t border-dashed">*** COMPROBANTE EMITIDO DIGITALMENTE ***</p>
    <div class="text-center pt-4 no-print-window">
      <button @click="cerrarRecibo" class="px-4 py-1.5 bg-black text-white rounded text-[10px] font-black uppercase">Cerrar Visor</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '../services/axiosConfig';

const userSession = JSON.parse(localStorage.getItem('user') || '{}');
const idUsuarioLogueado = userSession.id_usuario || 1;

const activeTab = ref('citas');
const listaCitas = ref([]);
const listaFacturas = ref([]);
const listaFichas = ref([]);
const reciboActivo = ref(null);

const cargarHistorialCompleto = async () => {
  try {
    const [resCitas, resFacturas, resFichas] = await Promise.all([
      axios.get(`http://localhost:8080/api/cliente/historial/citas/${idUsuarioLogueado}`),
      axios.get(`http://localhost:8080/api/cliente/historial/facturas/${idUsuarioLogueado}`),
      axios.get(`http://localhost:8080/api/cliente/historial/grooming/${idUsuarioLogueado}`)
    ]);
    listaCitas.value = resCitas.data || [];
    listaFacturas.value = resFacturas.data || [];
    listaFichas.value = resFichas.data || [];
  } catch (err) {
    console.error("❌ Error recuperando el historial centralizado:", err);
  }
};

const reimprimirFacturaCliente = (factura) => {
  reciboActivo.value = factura;
  // Retraso de renderizado para permitir que window.print capture el modal inyectado
  setTimeout(() => {
    window.print();
  }, 300);
};

const cerrarRecibo = () => {
  reciboActivo.value = null;
};

onMounted(cargarHistorialCompleto);
</script>

<style>
.print-only { display: none; }
@media print {
  .no-print, header, nav, button, .no-print-window {
    display: none !important;
  }
  body { background-color: white !important; font-family: monospace !important; }
  .print-only {
    display: block !important;
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
    color: black;
  }
}
</style>