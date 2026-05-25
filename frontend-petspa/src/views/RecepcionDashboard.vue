<template>
  <div class="min-h-screen bg-gray-50 p-6 no-print">
    <div class="max-w-7xl mx-auto space-y-6">
      
      <header class="flex flex-col sm:flex-row justify-between items-start sm:items-center bg-white p-6 rounded-3xl border border-gray-100 shadow-sm gap-4">
        <div>
          <h2 class="text-2xl font-black text-gray-800 uppercase tracking-tighter">Panel de Gestión - Recepción</h2>
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Calendario Maestro, Verificación de Admisiones, POS y Bloqueos</p>
        </div>
        <div class="flex flex-wrap gap-2">
          <button v-for="tab in ['calendario', 'bandeja', 'pos', 'bloqueos']" :key="tab" @click="activeTab = tab"
                  :class="['px-4 py-2 text-xs font-black uppercase tracking-wider rounded-xl transition-all', activeTab === tab ? 'bg-blue-600 text-white shadow-lg shadow-blue-100' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            {{ tab }}
          </button>
        </div>
      </header>

      <div v-if="activeTab === 'calendario'" class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4">
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b pb-4">
          <div>
            <h4 class="text-sm font-black text-gray-700 uppercase tracking-wider">📅 Distribución de Trabajo Técnico</h4>
          </div>
          <div class="flex flex-wrap items-center gap-2">
            <input v-model="buscarGroomer" type="text" placeholder="🔍 Filtrar por nombre..." 
                   class="p-2 bg-gray-50 border rounded-xl text-xs font-bold outline-none focus:border-blue-400 w-48" />
            <button @click="filtroEstadoGroomer = 'todos'" :class="['px-3 py-1.5 text-[10px] font-black uppercase rounded-lg border', filtroEstadoGroomer === 'todos' ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-500']">Listado General</button>
            <button @click="filtroEstadoGroomer = 'pendiente'" :class="['px-3 py-1.5 text-[10px] font-black uppercase rounded-lg border', filtroEstadoGroomer === 'pendiente' ? 'bg-yellow-500 text-white' : 'bg-gray-50 text-gray-500']">Con Pendientes</button>
            <button @click="filtroEstadoGroomer = 'confirmado'" :class="['px-3 py-1.5 text-[10px] font-black uppercase rounded-lg border', filtroEstadoGroomer === 'confirmado' ? 'bg-blue-600 text-white' : 'bg-gray-50 text-gray-500']">Con Confirmados</button>
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full min-w-[700px] border-collapse">
            <thead>
              <tr class="bg-gray-100">
                <th class="p-3 text-center text-[10px] font-black text-gray-400 uppercase border border-gray-200 w-24">Horarios</th>
                <th v-for="g in groomersFiltrados" :key="g.id_groomer" class="p-3 text-center text-xs font-black text-gray-700 uppercase border border-gray-200">
                  ✂️ {{ g.nombres }} {{ g.apellidos }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="hora in horasJornada" :key="hora" class="hover:bg-gray-50/50">
                <td class="p-3 font-mono text-[11px] font-black text-gray-400 border border-gray-200 bg-gray-50/50 text-center">{{ hora }}</td>
                <td v-for="g in groomersFiltrados" :key="g.id_groomer" class="p-2 border border-gray-200 text-center">
                  <template v-if="estaEnJornada(g, hora)">
                    <div v-if="getCitaGroomerHora(g.id_groomer, hora)" class="p-2 rounded-xl text-[10px] font-black text-left space-y-0.5 border"
                         :class="getCitaGroomerHora(g.id_groomer, hora).estado === 'CONFIRMADA' ? 'bg-blue-50 text-blue-700 border-blue-200' : getCitaGroomerHora(g.id_groomer, hora).estado === 'PAGADA' ? 'bg-purple-50 text-purple-700 border-purple-200' : 'bg-yellow-50 text-yellow-700 border-yellow-200'">
                      <p>🐾 <b>{{ getCitaGroomerHora(g.id_groomer, hora).mascota?.nombre }}</b></p>
                      <p class="text-[9px] opacity-80 truncate">👤 Dueño: {{ getCitaGroomerHora(g.id_groomer, hora).mascota?.cliente?.nombre }} {{ getCitaGroomerHora(g.id_groomer, hora).mascota?.cliente?.apellido }}</p>
                      <p class="text-[8px] uppercase font-bold pt-1 border-t border-current/10 text-right">ID: #{{ getCitaGroomerHora(g.id_groomer, hora).id_cita }}</p>
                    </div>
                    <span v-else class="text-[10px] text-green-500 font-black uppercase tracking-tight">Disponible</span>
                  </template>
                  <span v-else class="text-[9px] text-gray-300 font-bold uppercase tracking-widest">Fuera de Turno</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === 'bandeja'" class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl">
        <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider mb-4">📥 Solicitudes de Reserva Pendientes</h3>
        <p v-if="citasPendientes.length === 0" class="text-xs text-gray-400 font-bold ml-1">No se registran solicitudes pendientes.</p>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="c in citasPendientes" :key="c.id_cita" class="bg-gray-50 p-5 rounded-2xl border border-gray-100 flex flex-col justify-between shadow-sm space-y-3">
            <div class="border-b pb-2 flex justify-between items-center">
              <span class="text-[10px] font-mono font-black text-blue-600 bg-blue-50 px-2 py-0.5 rounded">SOLICITUD: #{{ c.id_cita }}</span>
              <span class="text-[10px] font-bold text-gray-400">📅 {{ c.fecha }}</span>
            </div>

            <div class="bg-white p-3 rounded-xl border border-gray-100 text-xs">
              <span class="text-[9px] font-black text-gray-400 uppercase block mb-0.5">Cliente / Propietario</span>
              <p class="font-black text-gray-800">{{ c.mascota?.cliente?.nombre }} {{ c.mascota?.cliente?.apellido }}</p>
              <p class="text-[10px] text-gray-400">📞 Teléfono: {{ c.mascota?.cliente?.telefono || 'N/A' }}</p>
            </div>

            <div class="bg-white p-3 rounded-xl border border-gray-100 text-xs space-y-2">
              <span class="text-[9px] font-black text-gray-400 uppercase block">Expediente de Mascota</span>
              <div class="grid grid-cols-2 gap-1 text-[11px]">
                <p><b>Mascota:</b> {{ c.mascota?.nombre }}</p>
                <p><b>Especie:</b> {{ c.mascota?.especie }}</p>
                <p><b>Tamaño:</b> {{ c.mascota?.tamano }}</p>
                <p><b>Carácter:</b> <span class="text-red-500 font-bold">{{ c.mascota?.restricciones || 'Tranquilo' }}</span></p>
              </div>
              
              <div class="pt-2 border-t flex justify-between items-center">
                <span class="text-[10px] font-bold text-gray-400">Carnet Sanitario:</span>
                <button v-if="c.mascota?.vacunas && c.mascota?.vacunas !== 'No adjuntado'" 
                        @click="activarVistaPreviaInline(c.mascota.vacunas)" type="button"
                        class="px-2 py-0.5 bg-blue-600 text-white font-black rounded text-[9px] uppercase hover:bg-blue-700">
                  Validar Carnet
                </button>
                <span v-else class="text-red-500 font-black text-[9px] bg-red-50 px-2 py-0.5 rounded">No Adjunto</span>
              </div>
            </div>

            <div class="text-[11px] text-gray-500 space-y-0.5 bg-white p-2.5 rounded-xl border border-l-4 border-l-blue-500">
              <p>✂️ <b>Estilista:</b> {{ c.groomer?.nombres }} {{ c.groomer?.apellidos }}</p>
              <p>🧼 <b>Servicio:</b> {{ c.servicio?.nombreServicio }}</p>
              <p>⏰ <b>Horario:</b> {{ limpiarHora(c.horaInicio) }} - {{ limpiarHora(c.horaFinEstimada) }}</p>
            </div>

            <div class="grid grid-cols-2 gap-2 pt-1">
              <button @click="procesarEstadoCita(c.id_cita, 'CONFIRMADA')" class="py-2 bg-green-600 hover:bg-green-700 text-white text-[10px] font-black uppercase rounded-xl shadow-sm">Confirmar</button>
              <button @click="procesarEstadoCita(c.id_cita, 'CANCELADA')" class="py-2 bg-red-600 hover:bg-red-700 text-white text-[10px] font-black uppercase rounded-xl shadow-sm">Rechazar</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'pos'" class="space-y-6">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4 h-fit">
            <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider border-b pb-2">🛍️ Terminal POS de Caja</h3>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Citas Confirmadas para Facturar</label>
              <select v-model="posForm.idCita" @change="cargarDatosPos" class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent text-gray-700">
                <option value="" disabled>Seleccione turno listo...</option>
                <option v-for="c in citasConfirmadas" :key="c.id_cita" :value="c.id_cita">
                  Cita #{{ c.id_cita }} - Mascota: {{ c.mascota?.nombre }}
                </option>
              </select>
            </div>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Método de Liquidación</label>
              <div class="grid grid-cols-3 gap-2">
                <button v-for="m in ['Efectivo', 'QR', 'Transferencia']" :key="m" type="button" @click="posForm.metodoPago = m"
                        :class="['py-2 text-[10px] font-black rounded-lg border uppercase tracking-tighter transition-all', posForm.metodoPago === m ? 'bg-blue-600 text-white border-blue-600 shadow-md' : 'bg-gray-50 text-gray-600 border-gray-100']">
                  {{ m }}
                </button>
              </div>
            </div>
            <div class="border-t pt-3 flex justify-between items-center text-sm font-black text-gray-800">
              <span>Costo Total Base:</span><span class="text-blue-600 text-lg">Bs. {{ posForm.montoTotal }}</span>
            </div>
            <button @click="ejecutarCobro" :disabled="!posForm.idCita || !posForm.metodoPago" class="w-full py-3 bg-gray-900 text-white rounded-xl text-xs font-black uppercase tracking-widest disabled:opacity-50 hover:bg-blue-600 transition-all shadow-md">
              Emitir Recibo de Pago
            </button>
          </div>

          <div class="lg:col-span-2 bg-white p-6 rounded-3xl border border-gray-100 shadow-xl flex flex-col justify-center items-center font-mono min-h-[420px]">
            <div v-if="posForm.idCita && !ticketEmitido" class="w-full max-w-md bg-gray-50 p-6 border rounded-xl text-gray-600 text-xs space-y-4 shadow-xs">
              <div class="text-center font-black border-b pb-2 text-gray-400">
                <p class="text-xs uppercase tracking-widest">📝 Hoja de Liquidación de Turno</p>
              </div>
              <div class="grid grid-cols-2 gap-y-1 text-[11px]">
                <p><strong>Cita ID Referencia:</strong> #{{ citaSeleccionadaObjeto?.id_cita }}</p>
                <p class="text-right"><strong>Fecha Reserva:</strong> {{ citaSeleccionadaObjeto?.fecha }}</p>
                <p class="col-span-2"><strong>Recepcionista Emisor:</strong> {{ nombreRecepcionista }}</p>
              </div>
              <div class="space-y-1 text-[11px] bg-white p-3 rounded-xl border border-gray-100">
                <p><strong>Propietario / Cliente:</strong> {{ citaSeleccionadaObjeto?.mascota?.cliente?.nombre }} {{ citaSeleccionadaObjeto?.mascota?.cliente?.apellido }}</p>
                <p><strong>Mascota Pasajera:</strong> {{ citaSeleccionadaObjeto?.mascota?.nombre }} ({{ citaSeleccionadaObjeto?.mascota?.especie }} - {{ citaSeleccionadaObjeto?.mascota?.tamano }})</p>
                <p><strong>Estilista Profesional:</strong> {{ citaSeleccionadaObjeto?.groomer?.nombres }} {{ citaSeleccionadaObjeto?.groomer?.apellidos }}</p>
                <p><strong>Servicio Solicitado:</strong> {{ citaSeleccionadaObjeto?.servicio?.nombreServicio }}</p>
              </div>
              <div class="flex justify-between font-black text-gray-800 text-sm pt-2 border-t">
                <span>Monto Unitario Base:</span><span>Bs. {{ posForm.montoTotal }}</span>
              </div>
            </div>

            <div v-else-if="ticketEmitido" class="w-full max-w-md bg-yellow-50 p-6 border-2 border-dashed border-yellow-300 text-gray-700 text-xs space-y-4 shadow-inner rounded-xl relative">
              <div id="seccion-ticket-oficial" class="space-y-4 text-gray-700">
                <div class="text-center font-black border-b border-dashed border-yellow-300 pb-2">
                  <p class="text-sm tracking-tight uppercase">Pet Spa & Grooming</p>
                  <p class="text-[9px] uppercase text-gray-400 mt-0.5">Comprobante Oficial de Caja (Bolivia)</p>
                </div>
                <div class="grid grid-cols-2 gap-y-1 text-[11px] border-b border-dashed border-yellow-300 pb-2">
                  <p><strong>Nro Factura:</strong> #{{ ticketEmitido.numeroFactura }}</p>
                  <p class="text-right"><strong>Emisión:</strong> {{ ticketEmitido.fecha_emision }}</p>
                  <p><strong>Cita Ref:</strong> #{{ ticketEmitido.idCita }}</p>
                  <p class="text-right"><strong>Método Pago:</strong> {{ ticketEmitido.metodoPago }}</p>
                  <p class="col-span-2"><strong>Atendido Por:</strong> {{ nombreRecepcionista }}</p>
                </div>
                <div class="space-y-1 text-[11px] border-b border-dashed border-yellow-300 pb-2 bg-white/60 p-3 rounded-xl border">
                  <p><strong>Cliente Atendido:</strong> {{ snapshotCita?.mascota?.cliente?.nombre }} {{ snapshotCita?.mascota?.cliente?.apellido }}</p>
                  <p><strong>Mascota Atendida:</strong> {{ snapshotCita?.mascota?.nombre }} ({{ snapshotCita?.mascota?.especie }})</p>
                  <p><strong>Groomer Responsable:</strong> {{ snapshotCita?.groomer?.nombres }} {{ snapshotCita?.groomer?.apellidos }}</p>
                  <p><strong>Servicio Realizado:</strong> {{ snapshotCita?.servicio?.nombreServicio }}</p>
                </div>
                <div class="space-y-0.5 text-[10px] text-gray-400 text-right border-b border-dashed pb-2">
                  <p>Subtotal Neto (87%): Bs. {{ ticketEmitido.subtotal }}</p>
                  <p>IVA Crédito Fiscal (13%): Bs. {{ ticketEmitido.impuestos }}</p>
                </div>
                <div class="pt-1 flex justify-between font-black text-sm text-gray-900">
                  <span>TOTAL LIQUIDADO:</span><span>Bs. {{ ticketEmitido.total }}</span>
                </div>
              </div>
              <div class="text-center pt-4 flex justify-center gap-2 border-t border-dashed border-yellow-300 mt-2">
                <button @click="imprimirRecibo" class="text-[10px] bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-xl font-black uppercase shadow-sm">🖨️ Imprimir Factura</button>
                <button @click="ticketEmitido = null" class="text-[10px] bg-yellow-200 hover:bg-yellow-300 text-yellow-800 px-4 py-2 rounded-xl font-black uppercase">Nueva Venta</button>
              </div>
            </div>
            <div class="text-center" v-else>
              <p class="text-xs text-gray-400 font-bold uppercase tracking-wider">A la espera de asignación de cobro en terminal POS</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4">
          <div class="flex justify-between items-center border-b pb-2">
            <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider">📋 Historial de Facturas Emitidas</h3>
            <span class="text-[10px] bg-blue-50 text-blue-600 font-mono font-bold px-2 py-0.5 rounded">Total: {{ listaFacturas.length }}</span>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full border-collapse text-left text-xs">
              <thead>
                <tr class="bg-gray-50 text-gray-400 font-black uppercase text-[10px] border-b">
                  <th class="p-3">Nro Factura</th>
                  <th class="p-3">Fecha</th>
                  <th class="p-3">Cita</th>
                  <th class="p-3">Método</th>
                  <th class="p-3 text-right">Total</th>
                  <th class="p-3 text-center">Acción</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="f in listaFacturas" :key="f.id_factura" class="border-b hover:bg-gray-50/50 font-medium text-gray-600">
                  <td class="p-3 font-mono font-bold text-gray-800">{{ f.numeroFactura }}</td>
                  <td class="p-3">{{ f.fecha_emision }}</td>
                  <td class="p-3">#{{ f.idCita }}</td>
                  <td class="p-3"><span class="px-2 py-0.5 bg-gray-100 rounded text-[10px] uppercase font-bold">{{ f.metodoPago }}</span></td>
                  <td class="p-3 text-right font-black text-gray-800">Bs. {{ f.total }}</td>
                  <td class="p-3 text-center">
                    <button @click="reimprimirFacturaHistorial(f)" class="px-3 py-1 bg-blue-50 hover:bg-blue-600 hover:text-white text-blue-600 text-[10px] font-black uppercase rounded-lg transition-all">🖨️ Ver/PDF</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'bloqueos'" class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-4">
          <h3 class="text-sm font-black text-gray-700 uppercase tracking-wider border-b pb-2">🚫 Insertar Restricción de Tiempo</h3>
          <form @submit.prevent="guardarBloqueo" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div class="col-span-2">
                <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Afectados / Destinatario</label>
                <select v-model="bloqueoForm.idGroomer" class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent text-gray-700">
                  <option :value="null">🌍 Global (Feriado / Mant. General)</option>
                  <option v-for="g in listaGroomers" :key="g.id_groomer" :value="g.id_groomer">✂️ Solo Estilista: {{ g.nombres }} {{ g.apellidos }}</option>
                </select>
              </div>
              <div>
                <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Fecha</label>
                <input v-model="bloqueoForm.fecha" type="date" required class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent text-gray-700" />
              </div>
              <div>
                <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Motivo / Evento</label>
                <select v-model="bloqueoForm.motivo" required class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent text-gray-700">
                  <option value="" disabled>Seleccione...</option>
                  <option value="Feriado">Feriado Institucional</option>
                  <option value="Mantenimiento">Mantenimiento de Maquinaria</option>
                  <option value="Ausencia">Ausencia Justificada</option>
                  <option value="Descanso">Descanso Técnico / Intervalo</option>
                </select>
              </div>
              <div>
                <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Hora Inicio (Opcional)</label>
                <input v-model="bloqueoForm.horaInicio" type="time" class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent text-gray-700" />
              </div>
              <div>
                <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Hora Fin (Opcional)</label>
                <input v-model="bloqueoForm.horaFin" type="time" class="w-full p-3 bg-gray-50 rounded-xl text-xs font-bold outline-none border border-transparent text-gray-700" />
              </div>
            </div>
            <button type="submit" class="w-full py-3 bg-red-600 text-white rounded-xl text-xs font-black uppercase tracking-wider hover:bg-red-700 transition-all shadow-md">
              Persistir Bloqueo en MySQL
            </button>
          </form>
        </div>

        <div class="space-y-6">
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-xl space-y-3">
            <h4 class="text-xs font-black text-gray-400 uppercase tracking-widest text-center border-b pb-1">📅 Vista Mensual de Alertas Activas</h4>
            <div class="grid grid-cols-7 gap-1 text-center text-[10px] font-black text-gray-400 uppercase pb-1">
              <span>Do</span><span>Lu</span><span>Ma</span><span>Mi</span><span>Ju</span><span>Vi</span><span>Sá</span>
            </div>
            <div class="grid grid-cols-7 gap-1">
              <div v-for="(slot, i) in diasDelMesActual" :key="i" class="p-2 text-xs font-bold rounded-lg flex flex-col items-center justify-center min-h-[36px]"
                   :class="slot.vacio ? 'bg-transparent' : slot.bloqueado ? 'bg-red-500 text-white shadow-xs' : 'bg-gray-50 text-gray-700'">
                <span v-if="!slot.vacio" :title="slot.motivo">{{ slot.dia }}</span>
              </div>
            </div>
          </div>

          <div class="bg-white p-5 rounded-3xl border border-gray-100 shadow-xl space-y-3">
            <h4 class="text-xs font-black text-gray-700 uppercase tracking-wider">📋 Historial de Restricciones</h4>
            <div class="max-h-48 overflow-y-auto space-y-2 pr-1">
              <div v-for="b in listaBloqueos" :key="b.id_bloqueo" class="p-3 bg-gray-50 rounded-xl border flex justify-between items-center text-xs">
                <div>
                  <p class="font-black text-gray-800">🚫 {{ b.motivo }}</p>
                  <p class="text-[10px] text-gray-400 font-medium">📅 Fecha: {{ b.fecha }} | Rango: {{ b.horaInicio ? b.horaInicio + ' - ' + b.horaFin : 'Todo el día' }}</p>
                  <p class="text-[9px] text-blue-600 font-bold" v-if="b.idGroomer">Groomer ID: #{{ b.idGroomer }}</p>
                  <p class="text-[9px] text-green-600 font-bold" v-else>Afectación Global</p>
                </div>
                <button @click="eliminarBloqueoAgenda(b.id_bloqueo)" class="p-2 bg-red-50 hover:bg-red-600 text-red-600 hover:text-white rounded-lg transition-colors text-[10px] font-black uppercase">🗑️</button>
              </div>
              <p v-if="listaBloqueos.length === 0" class="text-center text-[11px] text-gray-400 font-bold py-4">No hay bloqueos activos en el sistema.</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="urlVistaPrevia" class="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4 backdrop-blur-xs">
        <div class="bg-white rounded-3xl w-full max-w-3xl overflow-hidden shadow-2xl border flex flex-col h-[85vh]">
          <div class="p-4 bg-gray-900 text-white flex justify-between items-center">
            <div>
              <h3 class="text-xs font-black uppercase tracking-widest">Inspector de Carnet Integrado</h3>
              <p class="text-[10px] text-gray-400 font-mono">{{ nombreArchivoPrevia }}</p>
            </div>
            <button @click="cerrarVistaPreviaInline" class="w-8 h-8 bg-white/10 rounded-full flex items-center justify-center font-bold text-sm hover:bg-white/20 transition-all">✕</button>
          </div>
          <div class="flex-1 bg-gray-100 p-4 flex items-center justify-center overflow-auto">
            <iframe :src="urlVistaPrevia" class="w-full h-full rounded-xl border bg-white shadow-inner"></iframe>
          </div>
          <div class="p-3 bg-gray-50 border-t flex justify-end">
            <button @click="cerrarVistaPreviaInline" class="px-5 py-2 bg-gray-800 text-white text-xs font-black uppercase rounded-xl hover:bg-gray-900 transition-colors">Cerrar Visor</button>
          </div>
        </div>
      </div>

    </div>
  </div>

  <div id="area-impresion-exclusiva" class="print-only font-mono p-8 text-xs space-y-4" v-if="ticketEmitido">
    <div class="text-center font-black border-b border-black pb-2">
      <p class="text-lg">PET SPA & GROOMING</p>
      <p class="text-[10px]">Casa Central - La Paz, Bolivia</p>
      <p class="text-[11px] uppercase mt-2">Comprobante Oficial de Pago</p>
    </div>
    <div class="space-y-1">
      <p><b>NRO FACTURA:</b> {{ ticketEmitido.numeroFactura }}</p>
      <p><b>FECHA EMISIÓN:</b> {{ ticketEmitido.fecha_emision }}</p>
      <p><b>MODALIDAD:</b> {{ ticketEmitido.metodoPago }}</p>
      <p><b>RECEPCIONISTA:</b> {{ nombreRecepcionista }}</p>
    </div>
    <div class="border-b border-t py-2 space-y-1">
      <p><b>Propietario:</b> {{ snapshotCita?.mascota?.cliente?.nombre }} {{ snapshotCita?.mascota?.cliente?.apellido }}</p>
      <p><b>Mascota:</b> {{ snapshotCita?.mascota?.nombre }} ({{ snapshotCita?.mascota?.especie }})</p>
      <p><b>Estilista:</b> {{ snapshotCita?.groomer?.nombres }} {{ snapshotCita?.groomer?.apellidos }}</p>
      <p><b>Servicio:</b> {{ snapshotCita?.servicio?.nombreServicio }}</p>
    </div>
    <div class="text-right space-y-1 pt-2">
      <p>Subtotal Neto: Bs. {{ ticketEmitido.subtotal }}</p>
      <p>IVA Crédito Fiscal (13%): Bs. {{ ticketEmitido.impuestos }}</p>
      <p class="text-base font-black border-t pt-1">TOTAL LIQUIDADO: Bs. {{ ticketEmitido.total }}</p>
    </div>
    <p class="text-center text-[10px] pt-6 tracking-widest border-t border-dashed">*** GRACIAS POR SU PREFERENCIA ***</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import axios from '../services/axiosConfig';

const route = useRoute();
const activeTab = ref(route.query.tab || 'calendario');

const listaGroomers = ref([]);
const todasLasCitas = ref([]);
const listaFacturas = ref([]); 
const listaBloqueos = ref([]); 
const ticketEmitido = ref(null);
const snapshotCita = ref(null); 

const buscarGroomer = ref('');
const filtroEstadoGroomer = ref('todos'); 

const urlVistaPrevia = ref(null);
const esPdf = ref(false);
const nombreArchivoPrevia = ref('');

const userSession = JSON.parse(localStorage.getItem('user') || '{}');
const nombreRecepcionista = computed(() => {
  return `${userSession.nombre || 'Recepcionista'} ${userSession.apellido || 'de Turno'}`;
});

const horasJornada = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'];
const posForm = ref({ idCita: '', metodoPago: '', montoTotal: 0.0 });
const bloqueoForm = ref({ fecha: '', motivo: '', horaInicio: null, horaFin: null, idGroomer: null });

const citasPendientes = computed(() => todasLasCitas.value.filter(c => c.estado === 'PENDIENTE'));
const citasConfirmadas = computed(() => todasLasCitas.value.filter(c => c.estado === 'CONFIRMADA'));

const citaSeleccionadaObjeto = computed(() => {
  return todasLasCitas.value.find(c => c.id_cita === posForm.value.idCita);
});

// CORRECCIÓN CLAVE: Agregamos filtros para que ignore citas FINALIZADAS y CANCELADAS, liberando el bloque visual de inmediato
const limpiarHora = (timeStr) => {
  if (!timeStr) return "00:00";
  return timeStr.substring(0, 5);
};

const diasDelMesActual = computed(() => {
  const ahora = new Date();
  const año = ahora.getFullYear();
  const mes = ahora.getMonth();
  const primerDiaMes = new Date(año, mes, 1).getDay(); 
  const totalDias = new Date(año, mes + 1, 0).getDate();
  
  const matriz = [];
  for (let i = 0; i < primerDiaMes; i++) {
    matriz.push({ vacio: true });
  }
  for (let d = 1; d <= totalDias; d++) {
    const mStr = mes + 1 < 10 ? `0${mes + 1}` : `${mes + 1}`;
    const dStr = d < 10 ? `0${d}` : `${d}`;
    const fechaStr = `${año}-${mStr}-${dStr}`;
    
    const coincidenciaBloqueo = listaBloqueos.value.filter(b => b.fecha === fechaStr);
    matriz.push({
      vacio: false,
      dia: d,
      fecha: fechaStr,
      bloqueado: coincidenciaBloqueo.length > 0,
      motivo: coincidenciaBloqueo.map(b => b.motivo).join(', ')
    });
  }
  return matriz;
});

const groomersFiltrados = computed(() => {
  return listaGroomers.value.filter(g => {
    const nombreCompleto = `${g.nombres} ${g.apellidos}`.toLowerCase();
    const coincideTexto = nombreCompleto.includes(buscarGroomer.value.toLowerCase());
    if (!coincideTexto) return false;
    if (filtroEstadoGroomer.value === 'todos') return true;
    const citasDelGroomer = todasLasCitas.value.filter(c => {
      const gid = c.idGroomer || c.id_groomer || c.groomer?.id_groomer;
      return gid === g.id_groomer;
    });
    if (filtroEstadoGroomer.value === 'pendiente') return citasDelGroomer.some(c => c.estado === 'PENDIENTE');
    if (filtroEstadoGroomer.value === 'confirmado') return citasDelGroomer.some(c => c.estado === 'CONFIRMADA');
    return true;
  });
});

// MODIFICACIÓN DE REGLA TÉCNICA: Solo devuelve citas si NO están finalizadas ni canceladas
const getCitaGroomerHora = (idGroomer, hora) => {
  return todasLasCitas.value.find(c => {
    const gid = c.idGroomer || c.id_groomer || c.groomer?.id_groomer;
    return gid === idGroomer && 
           c.horaInicio.startsWith(hora) && 
           c.estado !== 'FINALIZADA' && 
           c.estado !== 'CANCELADA';
  });
};

const estaEnJornada = (groomer, horaRow) => {
  const entrada = parseInt((groomer.hora_entrada || "08:00:00").split(':')[0]);
  const salida = parseInt((groomer.hora_salida || "18:00:00").split(':')[0]);
  const current = parseInt(horaRow.split(':')[0]);
  return current >= entrada && current < salida;
};

const activarVistaPreviaInline = async (filename) => {
  nombreArchivoPrevia.value = filename;
  esPdf.value = filename.toLowerCase().endsWith('.pdf');
  try {
    const response = await axios.get(`http://localhost:8080/api/empleado/mascotas/vacunas/${filename}`, {
      responseType: 'blob'
    });
    const reader = new FileReader();
    reader.readAsDataURL(response.data);
    reader.onloadend = () => { urlVistaPrevia.value = reader.result; };
  } catch (err) {
    alert("❌ Error: No se pudo cargar el carnet.");
  }
};

const cerrarVistaPreviaInline = () => {
  urlVistaPrevia.value = null;
  nombreArchivoPrevia.value = '';
};

const cargarCatalogos = async () => {
  try {
    const [resGroomers, resCitas, resFacturas, resBloqueos] = await Promise.all([
      axios.get('http://localhost:8080/api/empleado/groomers'),
      axios.get('http://localhost:8080/api/empleado/citas/todas'),
      axios.get('http://localhost:8080/api/empleado/facturas/todas'),
      axios.get('http://localhost:8080/api/empleado/bloqueos/todas') 
    ]);
    listaGroomers.value = resGroomers.data;
    todasLasCitas.value = resCitas.data;
    listaFacturas.value = resFacturas.data || [];
    listaBloqueos.value = resBloqueos.data || [];
  } catch (err) {
    console.error("❌ Error en catálogo general de recepción:", err);
  }
};

const procesarEstadoCita = async (idCita, nuevoEstado) => {
  try {
    await axios.put(`http://localhost:8080/api/empleado/citas/estado/${idCita}?estado=${nuevoEstado}`);
    alert(`Operación procesada con éxito.`);
    await cargarCatalogos();
  } catch (err) {
    alert("⚠️ Validación Operativa: " + (err.response?.data?.error || "Error de red"));
  }
};

const cargarDatosPos = () => {
  ticketEmitido.value = null; 
  if (citaSeleccionadaObjeto.value) {
    posForm.value.montoTotal = citaSeleccionadaObjeto.value.servicio?.precioBase || 90.00;
  }
};

const ejecutarCobro = async () => {
  try {
    snapshotCita.value = { ...citaSeleccionadaObjeto.value };
    const payload = { idCita: posForm.value.idCita, montoTotal: posForm.value.montoTotal, metodoPago: posForm.value.metodoPago };
    const res = await axios.post('http://localhost:8080/api/empleado/facturas/registrar', payload);
    ticketEmitido.value = res.data;
    alert("💵 Transacción guardada con éxito.");
    posForm.value.idCita = ''; 
    await cargarCatalogos();
  } catch (err) {
    alert("❌ Error al procesar cobro.");
  }
};

const guardarBloqueo = async () => {
  try {
    const payload = { 
      idGroomer: bloqueoForm.value.idGroomer,
      fecha: bloqueoForm.value.fecha,
      motivo: bloqueoForm.value.motivo,
      horaInicio: bloqueoForm.value.horaInicio ? bloqueoForm.value.horaInicio + ":00" : null, 
      horaFin: bloqueoForm.value.horaFin ? bloqueoForm.value.horaFin + ":00" : null 
    };
    await axios.post('http://localhost:8080/api/empleado/bloqueos/registrar', payload);
    alert("🚫 Restricción guardada e insertada.");
    bloqueoForm.value = { fecha: '', motivo: '', horaInicio: null, horaFin: null, idGroomer: null };
    await cargarCatalogos();
  } catch (err) {
    alert("Error al registrar bloqueo.");
  }
};

const eliminarBloqueoAgenda = async (idBloqueo) => {
  if (!confirm("⚠️ ¿Desea levantar la restricción de la agenda?")) return;
  try {
    await axios.delete(`http://localhost:8080/api/empleado/bloqueos/${idBloqueo}`);
    alert("Agenda liberada correctamente.");
    await cargarCatalogos();
  } catch (err) {
    alert("Error al eliminar restricción.");
  }
};

const imprimirRecibo = () => { window.print(); };

const reimprimirFacturaHistorial = (factura) => {
  const citaAsociada = todasLasCitas.value.find(c => c.id_cita == factura.idCita);
  snapshotCita.value = citaAsociada;
  ticketEmitido.value = factura;
};

onMounted(cargarCatalogos);
</script>

<style>
.print-only { display: none; }
@media print {
  .no-print, header, nav, button, select, input, .bg-white, .overflow-x-auto, table {
    display: none !important;
  }
  body, .min-h-screen, .bg-gray-50 {
    background-color: white !important;
    font-family: monospace !important;
  }
  .print-only {
    display: block !important;
    width: 100% !important;
    max-width: 400px !important;
    margin: 0 auto !important;
    color: black !important;
  }
}
</style>