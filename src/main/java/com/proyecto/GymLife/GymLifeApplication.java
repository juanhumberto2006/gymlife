package com.proyecto.GymLife;

import com.proyecto.GymLife.model.Rol;
import com.proyecto.GymLife.model.Usuario;
import com.proyecto.GymLife.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GymLifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymLifeApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
		return args -> {
			try {
				if (usuarioRepository.findByUsername("admin").isEmpty()) {
					Usuario admin = new Usuario("admin", encoder.encode("admin123"));
					admin.addRol(new Rol("ROLE_ADMIN"));
					usuarioRepository.save(admin);
				}
			} catch (Exception ignored) {
			}
		};
	}
}
