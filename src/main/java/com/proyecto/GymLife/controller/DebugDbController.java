package com.proyecto.GymLife.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugDbController {
    private final JdbcTemplate jdbcTemplate;

    public DebugDbController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db")
    public Map<String, Object> dbInfo() {
        Map<String, Object> result = new HashMap<>();
        try {
            String currentUser = jdbcTemplate.queryForObject("select current_user", String.class);
            Integer usuarios = jdbcTemplate.queryForObject("select count(*) from public.usuarios", Integer.class);
            Integer roles = jdbcTemplate.queryForObject("select count(*) from public.roles", Integer.class);
            result.put("current_user", currentUser);
            result.put("usuarios", usuarios);
            result.put("roles", roles);
            result.put("status", "ok");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }
}
