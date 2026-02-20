package cz.lukaskabc.ontology.ontopus.api.service;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

/**
 * Object constructed with {@link ArtifactPropertyMappingProviderFactory} capable of resolving attributes for the
 * {@link VersionArtifact VersionArtifact}
 */
public interface ArtifactPropertyMappingProvider {
    default @Nullable MultilingualString resolveDescription() {
        return null;
    }

    default @Nullable Set<String> resolveLanguages() {
        return null;
    }

    default @Nullable Instant resolveModifiedDate() {
        return null;
    }

    default @Nullable Instant resolveReleaseDate() {
        return null;
    }

    default @Nullable MultilingualString resolveTitle() {
        return null;
    }

    default @Nullable String resolveVersion() {
        return null;
    }

    default @Nullable URI resolveVersionURI() {
        return null;
    }
}
