package com.proyecto.GymLife.controller;

import com.proyecto.GymLife.dto.RegisterRequest;
import com.proyecto.GymLife.model.Rol;
import com.proyecto.GymLife.model.Usuario;
import com.proyecto.GymLife.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Usuario ya existe");
        }
        Usuario usuario = new Usuario(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        usuario.addRol(new Rol("ROLE_USER"));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Registro exitoso");
    }
}
