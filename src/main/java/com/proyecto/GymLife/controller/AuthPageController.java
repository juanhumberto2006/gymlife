package com.proyecto.GymLife.controller;

import com.proyecto.GymLife.model.Rol;
import com.proyecto.GymLife.model.Usuario;
import com.proyecto.GymLife.repository.UsuarioRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Validated
public class AuthPageController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public AuthPageController(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam @NotBlank String username,
                             @RequestParam @NotBlank String password,
                             Model model) {
        if (usuarioRepository.existsByUsername(username)) {
            model.addAttribute("error", "El usuario ya existe");
            return "auth/register";
        }
        Usuario user = new Usuario(username, encoder.encode(password));
        user.addRol(new Rol("ROLE_USER"));
        usuarioRepository.save(user);
        return "redirect:/login?registered";
    }
}
