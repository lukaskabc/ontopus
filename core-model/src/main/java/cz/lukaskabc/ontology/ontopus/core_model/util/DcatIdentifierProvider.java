package cz.lukaskabc.ontology.ontopus.core_model.util;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.IdentifierGenerationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Component
public class DcatIdentifierProvider {
    private static void requireNotBlank(String string, String valueName) {
        if (!StringUtils.hasText(string)) {
            throw new IdentifierGenerationException("Given string value cannot be blank!", valueName);
        }
    }

    private static UUID uuidFor(TypedIdentifier identifier) {
        Objects.requireNonNull(identifier);
        return uuidFor(identifier.toURI());
    }

    private static UUID uuidFor(URI uri) {
        Objects.requireNonNull(uri);
        byte[] bytes = uri.toString().getBytes(StandardCharsets.UTF_8);
        return UUID.nameUUIDFromBytes(bytes);
    }

    private final URI baseUri;

    public DcatIdentifierProvider(OntopusConfig config) {
        this.baseUri = config.getDcatCatalog().getBaseUri();
    }

    public OntopusCatalogURI getCatalogUri() {
        final URI uri =
                UriComponentsBuilder.fromUri(baseUri).path("/catalog").build().toUri();
        return new OntopusCatalogURI(uri);
    }

    public VersionArtifactURI getVersionArtifactUri(OntologyVersionURI versionURI, String title, String version) {
        requireNotBlank(title, "VersionArtifact.title");
        requireNotBlank(version, "VersionArtifact.version");
        final UUID uuid = uuidFor(versionURI);
        final String safeLabel = StringUtils.sanitize(title);
        final String safeVersion = StringUtils.sanitize(version);
        final URI uri = UriComponentsBuilder.fromUri(baseUri)
                .path("/version-artifact/")
                .path(safeLabel)
                .path("/")
                .path(safeVersion)
                .path("/")
                .path(uuid.toString())
                .build()
                .toUri();
        return new VersionArtifactURI(uri);
    }

    public VersionSeriesURI getVersionSeriesUri(OntologyURI ontologyURI, String title) {
        requireNotBlank(title, "VersionSeries.title");
        final UUID uuid = uuidFor(ontologyURI);
        final String safeLabel = StringUtils.sanitize(title);
        final URI uri = UriComponentsBuilder.fromUri(baseUri)
                .path("/version-series/")
                .path(safeLabel)
                .path("/")
                .path(uuid.toString())
                .build()
                .toUri();
        return new VersionSeriesURI(uri);
    }
}
