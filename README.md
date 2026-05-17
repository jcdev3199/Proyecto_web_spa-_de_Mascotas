Módulo de Autenticación y Seguridad - Pet Spa UMSA
Este repositorio contiene la implementación integral del sistema de acceso y resguardo de información para el Spa de mascotas , basado en una arquitectura Fullstack (Spring Boot + Vue.js) y un modelo de Control de Acceso Basado en Roles (RBAC).

Características Principales
1. Gestión de Accesos (RBAC)

El sistema clasifica a los usuarios en cuatro perfiles con flujos de registro diferenciados:

Administrador: Control total y gestión de cuentas del personal. (Requiere 2FA obligatorio).

Personal (Recepción/Groomers): Cuentas creadas exclusivamente por el Administrador.

Clientes: Auto-registro mediante formulario tradicional o Google OAuth 2.0.

2.Seguridad
Políticas de Password: Validación de complejidad (8+ caracteres, alfanuméricos y caracteres especiales) con Medidor de Fuerza integrado en el Frontend.

Protección de Datos: Encriptación de credenciales mediante BCrypt.

Verificación: Activación de cuenta vía correo electrónico con tokens de expiración corta.

Sesiones Seguras: Implementación de JWT (JSON Web Tokens) con cierre de sesión automático tras 30 minutos de inactividad tanto en el backend y frontend.

3. Resiliencia y Auditoría
Protección ante Fuerza Bruta: Bloqueo preventivo de cuentas tras 5 intentos fallidos por un periodo de 15 minutos.

Trazabilidad (Logs): Registro detallado de eventos críticos (quién, cuándo, IP y acción) para auditorías forenses.

Integridad: Sanitización de entradas para prevenir ataques XSS y SQL Inyection, junto con un sistema de "borrado lógico" (Estado Inactivo) para preservar el historial.

Stack Tecnológico
Backend: Java / Spring Boot 3 .

Frontend: Vue.js 3 / Tailwind CSS / Vite.

Base de Datos: MySQL.

Autenticación: JWT / OAuth 2.0.
