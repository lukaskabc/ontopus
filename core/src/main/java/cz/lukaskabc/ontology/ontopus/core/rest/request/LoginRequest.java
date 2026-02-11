package cz.lukaskabc.ontology.ontopus.core.rest.request;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

/** Request object for submitting login */
public record LoginRequest(
        @NotEmpty String username, @NotEmpty String password) implements Serializable {}
