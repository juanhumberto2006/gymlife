package com.proyecto.GymLife.controller;

import com.proyecto.GymLife.dto.ClaseRequest;
import com.proyecto.GymLife.model.Clase;
import com.proyecto.GymLife.model.Reserva;
import com.proyecto.GymLife.repository.ClaseRepository;
import com.proyecto.GymLife.repository.ReservaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClaseController {
    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;

    public ClaseController(ClaseRepository claseRepository, ReservaRepository reservaRepository) {
        this.claseRepository = claseRepository;
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/clases")
    public List<Clase> listar() {
        return claseRepository.findAll();
    }

    @PostMapping("/admin/clases")
    public ResponseEntity<Clase> crear(@Valid @RequestBody ClaseRequest request) {
        Clase clase = new Clase(request.getNombre(), request.getDescripcion(), request.getCapacidad(), request.getFechaHora());
        return ResponseEntity.ok(claseRepository.save(clase));
    }

    @PutMapping("/admin/clases/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody ClaseRequest request) {
        return claseRepository.findById(id)
                .map(c -> {
                    c.setNombre(request.getNombre());
                    c.setDescripcion(request.getDescripcion());
                    c.setCapacidad(request.getCapacidad());
                    c.setFechaHora(request.getFechaHora());
                    return ResponseEntity.ok(claseRepository.save(c));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/clases/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!claseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        claseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/clases/{id}/usuarios")
    public ResponseEntity<?> usuariosDeClase(@PathVariable Long id) {
        List<String> usuarios = reservaRepository.findAll().stream()
                .filter(r -> r.getClase().getId().equals(id))
                .map(Reserva::getUsuario)
                .map(u -> u.getUsername())
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }
}
