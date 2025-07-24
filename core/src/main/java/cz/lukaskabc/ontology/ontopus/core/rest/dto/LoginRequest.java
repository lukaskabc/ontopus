package cz.lukaskabc.ontology.ontopus.core.rest.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(@NotEmpty String username, @NotEmpty String password) {}
