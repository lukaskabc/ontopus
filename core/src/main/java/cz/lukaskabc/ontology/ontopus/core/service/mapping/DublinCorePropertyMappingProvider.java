package cz.lukaskabc.ontology.ontopus.core.service.mapping;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractEntityIdentifier;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class DublinCorePropertyMappingProvider extends PropertyMapper implements ArtifactPropertyMappingProvider {
    private static final String DC_TERMS_PREFIX = "http://purl.org/dc/terms/";
    private static final String DC_ELEMENTS_PREFIX = "http://purl.org/dc/elements/1.1/";

    public DublinCorePropertyMappingProvider(
            EntityManager entityManager, @Nullable URI subjectURI, AbstractEntityIdentifier contextURI) {
        super(entityManager, subjectURI, contextURI);
    }

    @Override
    public @Nullable MultilingualString resolveDescription() {
        return findMultilingualProperty(
                Set.of(URI.create(DC_TERMS_PREFIX + "description"), URI.create(DC_ELEMENTS_PREFIX + "description")));
    }

    @Override
    public @Nullable Set<String> resolveLanguages() {
        return new HashSet<>(findProperties(
                Set.of(URI.create(DC_TERMS_PREFIX + "language"), URI.create(DC_ELEMENTS_PREFIX + "language")),
                String.class));
    }

    @Override
    public @Nullable Instant resolveReleaseDate() {
        return findSingleProperty(Set.of(URI.create(DC_TERMS_PREFIX + "created")), Instant.class);
    }

    @Override
    public @Nullable MultilingualString resolveTitle() {
        return findMultilingualProperty(
                Set.of(URI.create(DC_TERMS_PREFIX + "title"), URI.create(DC_ELEMENTS_PREFIX + "title")));
    }
}
