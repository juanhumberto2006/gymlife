package com.proyecto.GymLife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.GymLife.model.Reserva;
import com.proyecto.GymLife.model.Usuario;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario(Usuario usuario);
    long countByClaseId(Long claseId);
    
    // CAMBIA ESTA LÍNEA:
    boolean existsByUsuarioAndClaseId(Usuario usuario, Long claseId);
}