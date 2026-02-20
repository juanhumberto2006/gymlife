package com.proyecto.GymLife.security;

import com.proyecto.GymLife.model.Rol;
import com.proyecto.GymLife.model.Usuario;
import com.proyecto.GymLife.repository.UsuarioRepository;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        Collection<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(Rol::getRol)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }
}
