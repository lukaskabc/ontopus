package cz.lukaskabc.ontology.ontopus.plugin.git.form;

import org.jspecify.annotations.Nullable;

import jakarta.validation.constraints.NotEmpty;

public record GitRepositoryClonningRequest(
        @NotEmpty String repositoryUrl,
        @Nullable String branch,
        @Nullable String username,
        @Nullable String password) {}
