package com.proyecto.GymLife.controller;

import com.proyecto.GymLife.dto.ReservaRequest;
import com.proyecto.GymLife.model.Clase;
import com.proyecto.GymLife.model.Reserva;
import com.proyecto.GymLife.model.Usuario;
import com.proyecto.GymLife.repository.ClaseRepository;
import com.proyecto.GymLife.repository.ReservaRepository;
import com.proyecto.GymLife.repository.UsuarioRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReservaController {
    private final UsuarioRepository usuarioRepository;
    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;

    public ReservaController(UsuarioRepository usuarioRepository, ClaseRepository claseRepository, ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.claseRepository = claseRepository;
        this.reservaRepository = reservaRepository;
    }

    @PostMapping("/reservas")
    public ResponseEntity<?> reservar(Authentication auth, @Valid @RequestBody ReservaRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        if (usuario == null) return ResponseEntity.status(401).build();
        Clase clase = claseRepository.findById(request.getClaseId()).orElse(null);
        if (clase == null) return ResponseEntity.notFound().build();
        long reservas = reservaRepository.countByClaseId(clase.getId());
        if (reservas >= clase.getCapacidad()) {
            return ResponseEntity.badRequest().body("Clase llena");
        }
        if (reservaRepository.existsByUsuarioAndClaseId(usuario, clase.getId())) {
            return ResponseEntity.badRequest().body("Ya reservada");
        }
        Reserva r = reservaRepository.save(new Reserva(usuario, clase));
        return ResponseEntity.ok(r.getId());
    }

    @GetMapping("/reservas/mis")
    public ResponseEntity<List<Reserva>> misReservas(Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(reservaRepository.findByUsuario(usuario));
    }

    @GetMapping("/admin/reservas")
    public List<Reserva> todas() {
        return reservaRepository.findAll();
    }
}
