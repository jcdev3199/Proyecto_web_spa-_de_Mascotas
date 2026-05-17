package com.spamascotas.service;

import com.spamascotas.model.Usuario;
import com.spamascotas.model.Cliente;
import com.spamascotas.model.Rol;
import com.spamascotas.repository.UsuarioRepository;
import com.spamascotas.repository.ClienteRepository;
import com.spamascotas.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        System.out.println("[DEBUG] Iniciando comunicación con Google...");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String email = oAuth2User.getAttribute("email");
        String nombresGoogle = oAuth2User.getAttribute("given_name"); 
        String apellidosGoogle = oAuth2User.getAttribute("family_name"); 

        System.out.println("[DEBUG] Google devolvió el correo: " + email);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            System.out.println("[DEBUG] El usuario no existe. Iniciando Auto-registro...");
            
            // 1. Crear el Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setUsername(email);
            nuevoUsuario.setEstado("Activo"); 
            nuevoUsuario.setPassword_hash("OAUTH_USER_" + UUID.randomUUID().toString()); 

            // Buscar el Rol Cliente (ID 4 según tu base de datos)
            Rol rolCliente = rolRepository.findByNombreRol("Cliente")
                .orElseThrow(() -> new RuntimeException("Error: El Rol 'Cliente' no existe en la base de datos"));
            
            nuevoUsuario.setRol(rolCliente);
            usuarioRepository.save(nuevoUsuario);
            System.out.println("[DEBUG] Usuario guardado con éxito.");

            // 2. Crear el perfil de Cliente
            // SOLUCIÓN: Llenamos CI, Apellido y otros campos obligatorios con placeholders
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setUsuario(nuevoUsuario);
            

            nuevoCliente.setNombre(nombresGoogle != null ? nombresGoogle : "Usuario");
            nuevoCliente.setApellido(apellidosGoogle != null ? apellidosGoogle : "Google");


            // Datos obligatorios adicionales (CI, Teléfono, Dirección)
            nuevoCliente.setCi("0"); // Solución al error: DataIntegrityViolationException en 'ci'
            nuevoCliente.setTelefono("00000000"); 
            nuevoCliente.setDireccion("Por completar");

            clienteRepository.save(nuevoCliente);
            System.out.println("[DEBUG] Perfil de Cliente creado con éxito.");
        } else {
            System.out.println("[DEBUG] El usuario ya existe en la base de datos.");
        }

        return oAuth2User;
    }
}