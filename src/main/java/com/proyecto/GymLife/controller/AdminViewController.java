package com.proyecto.GymLife.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.GymLife.model.Clase;
import com.proyecto.GymLife.model.Rol;
import com.proyecto.GymLife.model.Usuario;
import com.proyecto.GymLife.repository.ClaseRepository;
import com.proyecto.GymLife.repository.ReservaRepository;
import com.proyecto.GymLife.repository.RolRepository;
import com.proyecto.GymLife.repository.UsuarioRepository;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final UsuarioRepository usuarioRepository;
    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminViewController(UsuarioRepository usuarioRepository, ClaseRepository claseRepository, 
                               ReservaRepository reservaRepository, RolRepository rolRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.claseRepository = claseRepository;
        this.reservaRepository = reservaRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalClases", claseRepository.count());
        model.addAttribute("totalReservas", reservaRepository.count());
        
        List<Clase> clases = claseRepository.findAll();
        model.addAttribute("clases", clases);
        
        Map<Long, Long> reservados = new HashMap<>();
        for (Clase c : clases) {
            reservados.put(c.getId(), reservaRepository.countByClaseId(c.getId()));
        }
        model.addAttribute("reservados", reservados);
        
        return "admin/index";
    }

    @GetMapping("/clases")
    public String listarClases(Model model) {
        List<Clase> clases = claseRepository.findAll();
        model.addAttribute("clases", clases);
        Map<Long, Long> reservados = new HashMap<>();
        for (Clase c : clases) {
            reservados.put(c.getId(), reservaRepository.countByClaseId(c.getId()));
        }
        model.addAttribute("reservados", reservados);
        return "admin/clases";
    }

    @GetMapping("/clases/nuevo")
    public String nuevaClase(Model model) {
        model.addAttribute("clase", new Clase());
        return "admin/clase-form";
    }

    @GetMapping("/clases/editar/{id}")
    public String editarClase(@PathVariable Long id, Model model) {
        Optional<Clase> clase = claseRepository.findById(id);
        if (clase.isPresent()) {
            model.addAttribute("clase", clase.get());
            return "admin/clase-form";
        }
        return "redirect:/admin/clases";
    }

    @PostMapping("/clases/guardar")
    public String guardarClase(@ModelAttribute Clase clase, RedirectAttributes redirectAttributes) {
        claseRepository.save(clase);
        redirectAttributes.addFlashAttribute("mensaje", "Clase guardada exitosamente");
        return "redirect:/admin/clases";
    }

    @GetMapping("/clases/eliminar/{id}")
    public String eliminarClase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            claseRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Clase eliminada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la clase porque tiene reservas asociadas.");
        }
        return "redirect:/admin/clases";
    }

    @GetMapping("/clases/{id}/usuarios")
    public String verUsuariosClase(@PathVariable Long id, Model model) {
        Optional<Clase> claseOpt = claseRepository.findById(id);
        if (claseOpt.isPresent()) {
            Clase clase = claseOpt.get();
            model.addAttribute("clase", clase);
            // Assuming ReservaRepository has findByClase(Clase clase) or we can filter
            // Let's assume we fetch all reservas for this class
            // We need to add a method to ReservaRepository or use the existing countByClaseId
            // I'll add findByClaseId to repository if not exists, or just use filter if lazy loading works
            // Actually Reserva has ManyToOne Clase.
            // Let's update ReservaRepository later to be sure.
            model.addAttribute("reservas", reservaRepository.findAll().stream()
                    .filter(r -> r.getClase().getId().equals(id)).toList());
            return "admin/usuarios-clase";
        }
        return "redirect:/admin";
    }

    // --- USUARIOS CRUD ---

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/usuario-form";
    }
    
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, @RequestParam(required = false) String rawPassword) {
        // Simple create/update logic
        if (usuario.getId() != null) {
            // Edit
            Usuario existing = usuarioRepository.findById(usuario.getId()).orElse(null);
            if (existing != null) {
                existing.setUsername(usuario.getUsername());
                if (rawPassword != null && !rawPassword.isEmpty()) {
                    existing.setPassword(passwordEncoder.encode(rawPassword));
                }
                usuarioRepository.save(existing);
            }
        } else {
            // Create
            if (rawPassword != null && !rawPassword.isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(rawPassword));
            }
            usuario.addRol(new Rol("ROLE_USER")); // Default role
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/usuarios";
    }

    // --- RESERVAS CRUD ---

    @GetMapping("/reservas")
    public String listarReservas(Model model) {
        model.addAttribute("reservas", reservaRepository.findAll());
        return "admin/reservas";
    }

    @GetMapping("/reservas/eliminar/{id}")
    public String eliminarReserva(@PathVariable Long id) {
        reservaRepository.deleteById(id);
        return "redirect:/admin/reservas";
    }
}
