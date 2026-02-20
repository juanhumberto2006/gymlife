package com.proyecto.GymLife.dto;

import jakarta.validation.constraints.NotNull;

public class ReservaRequest {
    @NotNull
    private Long claseId;

    public Long getClaseId() {
        return claseId;
    }

    public void setClaseId(Long claseId) {
        this.claseId = claseId;
    }
}
