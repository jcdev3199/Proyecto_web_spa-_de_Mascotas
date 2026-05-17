package com.spamascotas.security;

import com.spamascotas.model.Usuario;
import com.spamascotas.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException {
    
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email"); 
    
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado tras Google Login: " + email));

    String token = jwtUtil.generarToken(email, usuario.getRol().getNombreRol());

    String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/dashboard")
            .queryParam("token", token)
            .queryParam("id_usuario", usuario.getId_usuario())
            .queryParam("username", usuario.getUsername())
            .queryParam("email", email)
            .queryParam("rol", usuario.getRol().getNombreRol())
            .build().toUriString();

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
}
}