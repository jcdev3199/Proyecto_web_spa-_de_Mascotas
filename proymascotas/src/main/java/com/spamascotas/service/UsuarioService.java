package com.spamascotas.service;

import com.spamascotas.dto.ClienteDTO;
import com.spamascotas.dto.GroomerDTO;
import com.spamascotas.dto.PerfilUpdateRequest;
import com.spamascotas.dto.UserUpdateRequest;
import com.spamascotas.dto.UsuarioDTO;
import com.spamascotas.model.*;
import com.spamascotas.repository.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GroomerRepository groomerRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private RecepcionistaRepository recepcionistaRepository;

    @Transactional
    public void actualizarUsuarioCompleto(UserUpdateRequest request) {
        System.out.println(">>> [DEBUG SERVICE] Iniciando actualización segura para ID: " + request.getIdUsuario());

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            usuario.setUsername(request.getUsername());
        }
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            usuario.setEstado(request.getEstado());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            usuario.setPassword_hash(passwordEncoder.encode(request.getNewPassword()));
        }

        usuarioRepository.save(usuario);

        if (usuario.getRol() != null && usuario.getRol().getId_rol() == 3 && request.getGroomer() != null) {
            Groomer g = groomerRepository.findByUsuario(usuario).orElse(new Groomer());
            g.setUsuario(usuario);

            if (request.getGroomer().getTelefono() != null && !request.getGroomer().getTelefono().isBlank()) {
                g.setTelefono(request.getGroomer().getTelefono());
            }
            if (request.getGroomer().getEspecialidad() != null && !request.getGroomer().getEspecialidad().isBlank()) {
                g.setEspecialidad(request.getGroomer().getEspecialidad());
            }
            if (request.getGroomer().getTurno() != null && !request.getGroomer().getTurno().isBlank()) {
                g.setTurno(request.getGroomer().getTurno());
            }
            if (request.getGroomer().getCapacidadSimultanea() != null) {
                g.setCapacidad_simultanea(request.getGroomer().getCapacidadSimultanea());
            }
            if (request.getGroomer().getHoraEntrada() != null) {
                g.setHora_entrada(request.getGroomer().getHoraEntrada());
            }
            if (request.getGroomer().getHoraSalida() != null) {
                g.setHora_salida(request.getGroomer().getHoraSalida());
            }

            groomerRepository.save(g);
        }

        if (usuario.getRol() != null && usuario.getRol().getId_rol() == 4 && request.getCliente() != null) {
            Cliente c = clienteRepository.findByUsuario(usuario).orElse(new Cliente());
            c.setUsuario(usuario);

            if (request.getCliente().getNombre() != null && !request.getCliente().getNombre().isBlank()) {
                c.setNombre(request.getCliente().getNombre());
            }
            if (request.getCliente().getCi() != null && !request.getCliente().getCi().isBlank()) {
                c.setCi(request.getCliente().getCi());
            }
            if (request.getCliente().getTelefono() != null && !request.getCliente().getTelefono().isBlank()) {
                c.setTelefono(request.getCliente().getTelefono());
            }
            if (request.getCliente().getDireccion() != null && !request.getCliente().getDireccion().isBlank()) {
                c.setDireccion(request.getCliente().getDireccion());
            }

            clienteRepository.save(c);
        }
    }

        @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuariosConDetalles() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        return usuarios.stream().map(u -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId_usuario(u.getId_usuario());
            dto.setUsername(u.getUsername());
            dto.setEmail(u.getEmail());
            dto.setEstado(u.getEstado());
            dto.setId_rol(u.getRol().getId_rol());

            if (u.getRol().getId_rol() == 3) {
                groomerRepository.findByUsuario(u).ifPresent(g -> {
                    GroomerDTO gDto = new GroomerDTO();
                    gDto.setTelefono(g.getTelefono());
                    gDto.setEspecialidad(g.getEspecialidad());
                    gDto.setTurno(g.getTurno());
                    gDto.setCapacidadSimultanea(g.getCapacidad_simultanea());
                    gDto.setHoraEntrada(g.getHora_entrada());
                    gDto.setHoraSalida(g.getHora_salida());
                    dto.setGroomer(gDto);
                });
            }

        if (u.getRol().getId_rol() == 4) {
            clienteRepository.findByUsuario(u).ifPresent(c -> {
                ClienteDTO cDto = new ClienteDTO();
                cDto.setNombre(c.getNombre());
                cDto.setCi(c.getCi());
                cDto.setTelefono(c.getTelefono());
                cDto.setDireccion(c.getDireccion());
                dto.setCliente(cDto);
                System.out.println(">>> [DEBUG] Cargando datos de cliente para: " + u.getUsername());
            });
        }
            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public void actualizarDatosEmpleado(Long id, PerfilUpdateRequest request) {
        // Tu entidad usa Integer para el ID, casteamos el Long de la ruta
        Usuario u = usuarioRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Si es Groomer (Rol ID 3), actualizamos su tabla específica
        if (u.getRol() != null && u.getRol().getId_rol() == 3) {
            Groomer g = groomerRepository.findByUsuario(u).orElse(new Groomer());
            g.setUsuario(u);
            
            // Mapeo directo usando la estructura exacta de tu entidad Groomer
            g.setNombres(request.getNombre());
            g.setApellidos(request.getApellido());
            g.setTelefono(request.getTelefono());
            g.setEspecialidad(request.getEspecialidad());
            g.setTurno(request.getTurno());
            g.setCapacidad_simultanea(request.getCapacidadSimultanea());
            g.setHora_entrada(request.getHoraEntrada());
            g.setHora_salida(request.getHoraSalida());
            
            groomerRepository.save(g);
        }
        
        // Si es Recepcionista (Rol ID 2), manejamos su persistencia
        if (u.getRol() != null && u.getRol().getId_rol() == 2) {
            Recepcionista r = recepcionistaRepository.findByUsuario(u).orElse(new Recepcionista());
            r.setUsuario(u);
            r.setTurno(request.getTurno());
            recepcionistaRepository.save(r);
        }
    }
    
}


