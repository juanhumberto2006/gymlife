package com.proyecto.GymLife.controller;

import com.proyecto.GymLife.model.Clase;
import com.proyecto.GymLife.model.Reserva;
import com.proyecto.GymLife.model.Usuario;
import com.proyecto.GymLife.repository.ClaseRepository;
import com.proyecto.GymLife.repository.ReservaRepository;
import com.proyecto.GymLife.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserViewController {

    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public UserViewController(ClaseRepository claseRepository, ReservaRepository reservaRepository, UsuarioRepository usuarioRepository) {
        this.claseRepository = claseRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String home(Model model, Authentication auth) {
        try {
            List<Clase> clases = claseRepository.findAll();
            model.addAttribute("clases", clases);

            Map<Long, Long> reservados = new HashMap<>();
            for (Clase c : clases) {
                reservados.put(c.getId(), reservaRepository.countByClaseId(c.getId()));
            }
            model.addAttribute("reservados", reservados);

            if (auth != null && auth.isAuthenticated()) {
                Optional<Usuario> userOpt = usuarioRepository.findByUsername(auth.getName());
                if (userOpt.isPresent()) {
                    model.addAttribute("misReservas", reservaRepository.findByUsuario(userOpt.get()));
                }
            }
        } catch (Exception e) {
            model.addAttribute("clases", List.of());
            model.addAttribute("reservados", Map.of());
            model.addAttribute("dbError", "Servicio de base de datos no disponible. Mostrando vista básica.");
        }
        return "vista_usuario/index";
    }

    @GetMapping("/clases")
    public String verClases(Model model, Authentication auth) {
        try {
            List<Clase> clases = claseRepository.findAll();
            model.addAttribute("clases", clases);

            Map<Long, Long> reservados = new HashMap<>();
            for (Clase c : clases) {
                reservados.put(c.getId(), reservaRepository.countByClaseId(c.getId()));
            }
            model.addAttribute("reservados", reservados);
            
            Map<Long, Boolean> misReservasMap = new HashMap<>();
            if (auth != null && auth.isAuthenticated()) {
                Optional<Usuario> userOpt = usuarioRepository.findByUsername(auth.getName());
                if (userOpt.isPresent()) {
                    Usuario usuario = userOpt.get();
                    for (Clase c : clases) {
                        misReservasMap.put(c.getId(), reservaRepository.existsByUsuarioIdAndClaseId(usuario.getId(), c.getId()));
                    }
                }
            }
            model.addAttribute("misReservasMap", misReservasMap);
        } catch (Exception e) {
            model.addAttribute("clases", List.of());
            model.addAttribute("reservados", Map.of());
            model.addAttribute("misReservasMap", Map.of());
            model.addAttribute("dbError", "Servicio de base de datos no disponible. Mostrando vista básica.");
        }

        return "vista_usuario/clases";
    }

    @GetMapping("/about")
    public String about() {
        return "vista_usuario/about";
    }

    @GetMapping("/info")
    public String info() {
        return "vista_usuario/info";
    }

    @PostMapping("/reservar")
    public String reservar(@RequestParam Long claseId, Authentication auth, RedirectAttributes redirectAttributes, @RequestHeader(value = "referer", required = false) String referer) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Optional<Usuario> userOpt = usuarioRepository.findByUsername(auth.getName());
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/clases";

        if (userOpt.isPresent() && claseOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            Clase clase = claseOpt.get();
            
            // Validar si ya tiene reserva
            if (reservaRepository.existsByUsuarioIdAndClaseId(usuario.getId(), claseId)) {
                redirectAttributes.addFlashAttribute("error", "Ya tienes una reserva para esta clase.");
                return "redirect:" + redirectUrl;
            }
            
            // Validar cupo
            long cuposOcupados = reservaRepository.countByClaseId(claseId);
            if (cuposOcupados >= clase.getCapacidad()) {
                redirectAttributes.addFlashAttribute("error", "La clase está llena.");
                return "redirect:" + redirectUrl;
            }
            
            Reserva reserva = new Reserva(usuario, clase);
            reservaRepository.save(reserva);
            redirectAttributes.addFlashAttribute("mensaje", "Reserva realizada con éxito.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la reserva.");
        }
        
        return "redirect:" + redirectUrl;
    }
}
