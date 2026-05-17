package com.spamascotas.service;

import com.spamascotas.dto.*;
import com.spamascotas.model.*;
import com.spamascotas.repository.*;
import com.spamascotas.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime; // Asegúrate de tener este import
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.util.Utils;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.beans.factory.annotation.Value;


@Service
public class AuthService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private GroomerRepository groomerRepository;
    @Autowired private RecepcionistaRepository recepcionistaRepository;
    @Autowired private TokenActivacionRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private JavaMailSender mailSender;
    @Autowired private AuditoriaRepository auditoriaRepository;

    
    @Value("${app.backend.url}")
    private String backendUrl;
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*#])(?=\\S+$).{8,}$";

public LoginResponse autenticar(LoginRequest request, HttpServletRequest httpRequest) {
    // 1. Búsqueda de usuario
    Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

    // 2. Lógica de desbloqueo automático si ya pasó el tiempo (Regla 6)
    if (usuario.getBloqueado_hasta() != null && LocalDateTime.now().isAfter(usuario.getBloqueado_hasta())) {
        usuario.setIntentos_fallidos(0);
        usuario.setBloqueado_hasta(null);
        usuarioRepository.save(usuario);
        System.out.println(">>> [SEGURIDAD] Tiempo de bloqueo cumplido para: " + usuario.getEmail());
    }

    // 3. Verificación de cuenta bloqueada (Regla 6)
    if (usuario.getBloqueado_hasta() != null && usuario.getBloqueado_hasta().isAfter(LocalDateTime.now())) {
        registrarLog(usuario, "INTENTO_EN_CUENTA_BLOQUEADA", httpRequest); // Auditoría
        throw new RuntimeException("Cuenta bloqueada hasta: " + usuario.getBloqueado_hasta());
    }

    // 4. VALIDACIÓN DE ESTADO (Regla 3.2 - Verificación de Identidad)
    if (!"Activo".equals(usuario.getEstado())) {
        registrarLog(usuario, "LOGIN_DENEGADO_ESTADO_PENDIENTE", httpRequest); // Auditoría
        throw new RuntimeException("Tu cuenta está '" + usuario.getEstado() + "'. Revisa tu correo para activarla.");
    }

    // 5. Verificación de Contraseña
    if (passwordEncoder.matches(request.getPassword(), usuario.getPassword_hash())) {
        usuario.setIntentos_fallidos(0);
        usuario.setBloqueado_hasta(null);
        usuarioRepository.save(usuario); 

        // CASO A: Si es Administrador, requiere 2FA (Regla 3.2)
        if ("Administrador".equals(usuario.getRol().getNombreRol())) {
            registrarLog(usuario, "LOGIN_PASO_1_EXITOSO_2FA_REQUERIDO", httpRequest);
            // IMPORTANTE: Pasamos token null, pero incluimos el ID y datos del usuario
            return new LoginResponse(null, usuario.getId_usuario(), usuario.getEmail(), usuario.getUsername(), "Administrador");
        }

        // CASO B: Login exitoso para Clientes o Personal
        registrarLog(usuario, "LOGIN_EXITOSO", httpRequest); // Trazabilidad
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().getNombreRol());
        
        // Enviamos el Token y el ID_USUARIO (para que el perfil funcione)
        return new LoginResponse(token, usuario.getId_usuario(), usuario.getEmail(), usuario.getUsername(), usuario.getRol().getNombreRol());
        
    } else {
        // 6. Manejo de intentos fallidos y bloqueo (Regla 6)
        int nuevosIntentos = usuario.getIntentos_fallidos() + 1;
        usuario.setIntentos_fallidos(nuevosIntentos);

        if (nuevosIntentos >= 5) {
            usuario.setBloqueado_hasta(LocalDateTime.now().plusMinutes(15));
            usuarioRepository.save(usuario);
            registrarLog(usuario, "BLOQUEO_POR_FUERZA_BRUTA", httpRequest);
            throw new RuntimeException("5 intentos fallidos. Bloqueado por 15 min.");
        }
        
        usuarioRepository.save(usuario);
        registrarLog(usuario, "LOGIN_FALLIDO", httpRequest);
        throw new RuntimeException("Contraseña incorrecta. Intentos restantes: " + (5 - nuevosIntentos));
    }
}

    private void registrarLog(Usuario usuario, String accion, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String navegador = request.getHeader("User-Agent");
        
        // 1. Crear el objeto de Auditoría con los datos del Requerimiento 5.2
        Auditoria log = new Auditoria();
        
        // Convertimos el ID a Long para evitar el conflicto de tipos
        log.setIdUsuario(usuario.getId_usuario().longValue());
        log.setEmail(usuario.getEmail());
        log.setRol(usuario.getRol().getNombreRol());
        log.setFechaHora(LocalDateTime.now());
        log.setIp(ip);
        log.setNavegador(navegador);
        log.setAccion(accion);

        // 2. Guardar en la base de datos (Persistencia permanente)
        auditoriaRepository.save(log);
        
        // 3. Debug en consola (Regla 5.2)
        System.out.println("\n********** LOG GUARDADO EN BD (REGLA 5.2) **********");
        System.out.println("USUARIO: " + usuario.getEmail() + " (" + usuario.getRol().getNombreRol() + ")");
        System.out.println("ACCIÓN:  " + accion);
        System.out.println("****************************************************\n");
    }

    @Transactional
    public String verificarCuenta(String token) {
        System.out.println(">>> [DEBUG VERIFICACIÓN] Intentando activar cuenta con token: " + token);
        
        TokenActivacion tokenDb = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de activación inválido o inexistente"));

        if (LocalDateTime.now().isAfter(tokenDb.getFechaExpiracion())) {
            System.out.println(">>> [ALERTA] El link de activación ha expirado.");
            throw new RuntimeException("El link de activación ha expirado (superó el tiempo límite).");
        }

        Usuario usuario = tokenDb.getUsuario();
        
        if ("Activo".equals(usuario.getEstado())) {
            tokenRepository.delete(tokenDb);
            return "Esta cuenta ya se encuentra activa.";
        }

        usuario.setEstado("Activo");
        usuarioRepository.save(usuario);
        tokenRepository.delete(tokenDb);
        
        System.out.println(">>> [DEBUG VERIFICACIÓN] ¡Cuenta activada con éxito para: " + usuario.getEmail());
        return "¡Cuenta activada con éxito! Ya puedes iniciar sesión.";
    }

    @Transactional
    public String registrarCliente(RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setUsername(request.getUsername());
        usuario.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rolRepository.findByNombreRol("Cliente").get());
        usuario.setEstado("Pendiente"); 
        usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setCi(request.getCi());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        clienteRepository.save(cliente);

        String cadenaToken = UUID.randomUUID().toString();
        TokenActivacion tokenActivacion = new TokenActivacion();
        tokenActivacion.setToken(cadenaToken);
        tokenActivacion.setUsuario(usuario);
        tokenActivacion.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(tokenActivacion);

        try {
            enviarCorreoActivacion(usuario.getEmail(), cadenaToken);
        } catch (Exception e) {
            System.out.println(">>> [ERROR CORREO] " + e.getMessage());
        }
        return "Registro inicial exitoso. Por favor, revisa tu correo para activar la cuenta (Válido por 10 min).";
    }

// 1. Asegúrate de tener esta anotación al inicio de la clase AuthService


// 2. Reemplaza la función enviarCorreoActivacion por esta
private void enviarCorreoActivacion(String destinatario, String token) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Activación de cuenta - Pet Spa UMSA");
        
        // Link hacia la nueva pantalla de "Establecer Contraseña" en Vue
        String frontendUrl = "https://relatives-identical-particle-traditions.trycloudflare.com"; 
        String link = frontendUrl + "/configurar-password?token=" + token;
        
        message.setText("¡Bienvenido! Haz clic aquí para establecer tu contraseña y activar tu cuenta: \n" + link);
        mailSender.send(message);
    } catch (Exception e) {
        System.err.println(">>> [ERROR] Fallo al enviar correo: " + e.getMessage());
    }
}

// Reemplaza tu función registrarPersonal por esta versión corregida
@Transactional
public String registrarPersonal(java.util.Map<String, Object> payload, String rolNombre) {
    // 1. Extraemos los datos base del Map (Nivel Raíz)
    String username = (String) payload.get("username");
    String email = (String) payload.get("email");
    String password = (String) payload.get("password");

    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setEmail(email);
    usuario.setPassword_hash(passwordEncoder.encode(password));
    usuario.setEstado("Activo"); // Personal interno se registra activo por defecto[cite: 2]
    
    Rol rol = rolRepository.findByNombreRol(rolNombre)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));
    usuario.setRol(rol);
    Usuario guardado = usuarioRepository.save(usuario);

    // 2. Si es Groomer, extraemos los datos del objeto anidado enviado por Vue[cite: 2, 3]
    if (rolNombre.equalsIgnoreCase("Groomer") && payload.containsKey("groomer")) {
        java.util.Map<String, Object> gMap = (java.util.Map<String, Object>) payload.get("groomer");
        
        if (gMap != null) {
            Groomer groomer = new Groomer();
            groomer.setUsuario(guardado);
            
            // Priorizamos el teléfono del objeto groomer, si no existe usamos el de la raíz[cite: 2]
            String tel = gMap.get("telefono") != null ? (String) gMap.get("telefono") : (String) payload.get("telefono");
            groomer.setTelefono(tel);
            
            groomer.setEspecialidad((String) gMap.get("especialidad"));
            groomer.setTurno((String) gMap.get("turno"));
            
            // Validación y parseo de horarios[cite: 2]
            try {
                String entrada = (String) gMap.get("hora_entrada");
                String salida = (String) gMap.get("hora_salida");
                
                if (entrada != null && !entrada.isEmpty()) {
                    groomer.setHora_entrada(java.time.LocalTime.parse(entrada));
                    groomer.setHora_salida(java.time.LocalTime.parse(salida));
                }
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Fallo al parsear horas del groomer: " + e.getMessage());
            }
            
            groomerRepository.save(groomer);
        }
    }
    
    return "Personal registrado exitosamente con rol: " + rolNombre;
}

    public String generarQR2FA(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getTwoFaSecret() == null) {
            usuario.setTwoFaSecret(new DefaultSecretGenerator().generate());
            usuarioRepository.save(usuario);
        }

        QrData data = new QrData.Builder()
                .label(email)
                .secret(usuario.getTwoFaSecret())
                .issuer("PetSpa-Admin")
                .build();

        try {
            ZxingPngQrGenerator generator = new ZxingPngQrGenerator();
            byte[] imageData = generator.generate(data);
            return Utils.getDataUriForImage(imageData, generator.getImageMimeType());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el código QR");
        }
    }

    public boolean validarCodigo2FA(String email, String codigo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DefaultCodeVerifier verifier = new DefaultCodeVerifier(
                new DefaultCodeGenerator(), 
                new SystemTimeProvider()
        );

        return verifier.isValidCode(usuario.getTwoFaSecret(), codigo);
    }
    // Asegúrate de tener inyectado el ClienteRepository al inicio de la clase

    @Transactional
    public String actualizarPerfilCliente(Integer idUsuario, PerfilUpdateRequest request, HttpServletRequest httpRequest) {
        
        // Ahora que idUsuario es Integer, findById no se quejará
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Datos de cliente no encontrados"));

        // Actualización de campos (Regla 4: Datos Obligatorios)
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setCi(request.getCi());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());

        clienteRepository.save(cliente);

        // REGISTRO DE AUDITORÍA (Regla 5.2)
        registrarLog(usuario, "PERFIL_PROPIO_ACTUALIZADO", httpRequest);

        return "Perfil actualizado con éxito";
    }

    public Cliente buscarDatosCliente(Integer idUsuario) {
    Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    return clienteRepository.findByUsuario(usuario)
            .orElseThrow(() -> new RuntimeException("No existen datos de perfil para este cliente"));
    }   



// Asegúrate de inyectar los repositorios necesarios al inicio de la clase




@Transactional
public void crearEmpleado(RegistroEmpleadoRequest request, HttpServletRequest httpRequest) {
    // 1. Validar duplicados
    if (usuarioRepository.existsByUsername(request.getUsername())) {
        throw new RuntimeException("Error: El username '" + request.getUsername() + "' ya existe.");
    }
    if (usuarioRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Error: El email '" + request.getEmail() + "' ya existe.");
    }

    // 2. Crear Usuario base
    Usuario nuevo = new Usuario();
    nuevo.setUsername(request.getUsername());
    nuevo.setEmail(request.getEmail());
    nuevo.setPassword_hash(passwordEncoder.encode(request.getPassword()));
    
    // --- EL ESTADO INICIA EN PENDIENTE PARA VERIFICACIÓN ---
    nuevo.setEstado("Pendiente");
    
    // Aquí buscará "Recepción" en la BD y encontrará el ID 2
    nuevo.setRol(rolRepository.findByNombreRol(request.getRol())
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.getRol())));

    Usuario usuarioGuardado = usuarioRepository.saveAndFlush(nuevo);

    // 3. Lógica según el Rol
    if ("Groomer".equals(request.getRol())) {
        Groomer g = new Groomer();
        g.setUsuario(usuarioGuardado);
        g.setNombres(request.getNombres());
        g.setApellidos(request.getApellidos());
        g.setTelefono(request.getTelefono());
        g.setEspecialidad(request.getEspecialidad());
        g.setTurno(request.getTurno() != null ? request.getTurno() : "Mañana");

        g.setCapacidad_simultanea(request.getCapacidadSimultanea() != null ? request.getCapacidadSimultanea() : 1);
        
        try {
            if (request.getHoraEntrada() != null && !request.getHoraEntrada().isEmpty()) {
                g.setHora_entrada(java.time.LocalTime.parse(request.getHoraEntrada()));
                g.setHora_salida(java.time.LocalTime.parse(request.getHoraSalida()));
            } else {
                g.setHora_entrada(java.time.LocalTime.of(8, 0));
                g.setHora_salida(java.time.LocalTime.of(14, 0));
            }
        } catch (Exception e) {
            g.setHora_entrada(java.time.LocalTime.of(8, 0));
            g.setHora_salida(java.time.LocalTime.of(14, 0));
        }
        groomerRepository.save(g);
    } 
    else if ("Recepción".equals(request.getRol())) {
        Recepcionista r = new Recepcionista();
        r.setUsuario(usuarioGuardado);
        r.setNombres(request.getNombres());
        r.setApellidos(request.getApellidos());
        r.setTelefono(request.getTelefono());
        r.setTurno(request.getTurno() != null ? request.getTurno() : "Mañana");

        try {
            if (request.getHoraEntrada() != null && !request.getHoraEntrada().isEmpty()) {
                r.setHora_entrada(java.time.LocalTime.parse(request.getHoraEntrada()));
                r.setHora_salida(java.time.LocalTime.parse(request.getHoraSalida()));
            } else {
                r.setHora_entrada(java.time.LocalTime.of(8, 0));
                r.setHora_salida(java.time.LocalTime.of(14, 0));
            }
        } catch (Exception e) {
            r.setHora_entrada(java.time.LocalTime.of(8, 0));
            r.setHora_salida(java.time.LocalTime.of(14, 0));
        }
        recepcionistaRepository.save(r);
    }
    
    // --- GENERAR TOKEN Y ENVIAR CORREO DE ACTIVACIÓN ---
    generarYEnviarToken(usuarioGuardado);
    
    registrarLog(obtenerUsuarioActual(), "REGISTRO_EMPLEADO: " + nuevo.getEmail(), httpRequest);
}

public void solicitarResetPassword(String email) {
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("El correo no está registrado."));
    generarYEnviarToken(usuario);
}

private void generarYEnviarToken(Usuario usuario) {
    String tokenStr = java.util.UUID.randomUUID().toString();
    TokenActivacion token = new TokenActivacion();
    token.setToken(tokenStr);
    token.setUsuario(usuario);
    token.setFechaExpiracion(java.time.LocalDateTime.now().plusHours(24));
    tokenRepository.save(token);

    enviarCorreoActivacion(usuario.getEmail(), tokenStr);
}


private Usuario obtenerUsuarioActual() {
    String email = org.springframework.security.core.context.SecurityContextHolder
                   .getContext().getAuthentication().getName();
    
    return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No se pudo identificar al usuario en sesión"));
}



    @Transactional
    public String establecerPasswordYActivar(SetPasswordRequest request) {
        System.out.println("\n>>> [DEBUG] Iniciando activacion con contraseña...");
        System.out.println(">>> Token recibido: " + request.getToken());

        // 1. Buscar token
        TokenActivacion tokenDb = tokenRepository.findByToken(request.getToken())
                .orElseGet(() -> {
                    System.err.println(">>> [ERROR] El token no existe en la BD.");
                    return null;
                });
        
        if (tokenDb == null) throw new RuntimeException("Token inválido");

        // 2. Validar expiración
        System.out.println(">>> Token encontrado para usuario: " + tokenDb.getUsuario().getEmail());
        if (java.time.LocalDateTime.now().isAfter(tokenDb.getFechaExpiracion())) {
            System.err.println(">>> [ERROR] El token ha expirado. Fecha limite: " + tokenDb.getFechaExpiracion());
            throw new RuntimeException("El link ha expirado.");
        }

        // 3. Actualizar
        Usuario usuario = tokenDb.getUsuario();
        System.out.println(">>> Hasheando nueva contraseña y activando estado...");
        usuario.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado("Activo");
        usuarioRepository.save(usuario);

        // 4. Limpieza
        tokenRepository.delete(tokenDb);
        System.out.println(">>> [ÉXITO] Usuario " + usuario.getEmail() + " activado correctamente.\n");

        return "¡Cuenta activada con éxito!";
    }

public PerfilEmpleadoDTO buscarPerfilEmpleado(Integer idUsuario) {
    // 1. Buscamos el usuario base
    Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    PerfilEmpleadoDTO dto = new PerfilEmpleadoDTO();
    dto.setRol(usuario.getRol().getNombreRol());

    // 2. Extraemos datos según el Rol
    if ("Groomer".equals(dto.getRol())) {
        Groomer g = groomerRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Datos de Groomer no encontrados"));
        dto.setNombres(g.getNombres());
        dto.setApellidos(g.getApellidos());
        dto.setTelefono(g.getTelefono());
        dto.setTurno(g.getTurno());
        dto.setHoraEntrada(g.getHora_entrada() != null ? g.getHora_entrada().toString() : "08:00");
        dto.setHoraSalida(g.getHora_salida() != null ? g.getHora_salida().toString() : "14:00");
        dto.setEspecialidad(g.getEspecialidad());
        dto.setCapacidadSimultanea(g.getCapacidad_simultanea());
    } 
    else if ("Recepción".equals(dto.getRol())) {
        Recepcionista r = recepcionistaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Datos de Recepcionista no encontrados"));
        dto.setNombres(r.getNombres());
        dto.setApellidos(r.getApellidos());
        dto.setTelefono(r.getTelefono());
        dto.setTurno(r.getTurno());
        dto.setHoraEntrada(r.getHora_entrada() != null ? r.getHora_entrada().toString() : "08:00");
        dto.setHoraSalida(r.getHora_salida() != null ? r.getHora_salida().toString() : "14:00");
    }
    
    return dto;
}
}