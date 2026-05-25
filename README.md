Estructura del proyecto, diseño de interfaces, formularios principales, agenda, citas,recepción y grooming


MODULOS CONSOLIDADOS

Modulo de Agenda y Citas

Estructura dinamica de slots basada en la jornada laboral.

Logica preventiva de solapamientos horariales en consultas JPQL.

Filtros automaticos por fecha para resguardar la disponibilidad de la peluqueria.

Formularios del Cliente

Registro y administracion de perfiles de mascotas (Especie, Tamaño, Temperamento, Alergias).

Modulo seguro para la carga de carnet de vacunas y documentos medicos.

Sistema de solicitud de reservas en estado pendiente orientadas a revision.

Modulo de Recepcion y Caja (POS)

Calendario Maestro: Cuadricula interactiva organizada en columnas por estilista que muestra la distribucion tecnica del trabajo diario.

Bandeja de Confirmacion: Aprobacion o anulacion manual de turnos con escudo de proteccion contra sobreventas.

Punto de Venta (POS): Emision de recibos de caja con desglose automatico de impuestos (13 por ciento IVA de Bolivia), snapshots anticaida de datos en el reset del formulario y soporte de impresion nativa optimizada mediante CSS media print.

Modulo de Grooming (Estacion de Estilistas)

Ficha de evaluacion tecnica para registrar afecciones de ingreso (nudos, heridas, parasitos) y temperamento.

Checklist interactivo obligatorio (unas, oidos, glandulas, corte, bano, perfume).

Carga de fotos de evidencia del antes y el despues automatizada mediante conversion FileReader a Base64, evitando problemas de rutas fisicas o permisos del servidor.

Mecanismo de actualizacion inteligente (Upsert) en el controlador para evitar errores de registros duplicados en MySQL.

Historial Centralizado del Cliente

Acceso instantaneo a la cronologia de turnos pedida por el dueno.

Reimpresion de comprobantes fiscales antiguos en formato ticket desde la interfaz web.

Visualizacion directa de las recomendaciones técnicas y las fotos de control de calidad subidas por el peluquero.

REGLAS ALGORITMICAS DE NEGOCIO IMPLEMENTADAS

Algoritmo de Ajuste de Duracion Automatica
La duracion total estimada de una cita se calcula dinamicamente en el Backend multiplicando los minutos base del servicio segun el tamaño sanitizado de la mascota:

Mascota Pequena: Duracion base del servicio.

Mascota Mediana: Duracion base * 1.10

Mascota Grande: Duracion base * 1.15

Mascota Gigante: Duracion base * 1.30


Control de Capacidad Simultanea Reactiva
El sistema valida matematicamente que el conteo de mascotas en atencion paralela no supere el limite asignado al estilista en MySQL. El algoritmo excluye de manera automatica los registros en estado FINALIZADA o CANCELADA, permitiendo que la capacidad retorne a la normalidad (+1) e incremente la disponibilidad de slots en tiempo real tanto en el panel de recepcion como en el del cliente.
