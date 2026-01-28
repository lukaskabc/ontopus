package cz.lukaskabc.ontology.ontopus.plugin.skos;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import java.net.URI;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class SkosPropertyMappingProvider extends PropertyMapper implements ArtifactPropertyMappingProvider {
    private static final String SKOS_PREFIX = "http://www.w3.org/2004/02/skos/core#";

    public SkosPropertyMappingProvider(
            EntityManager entityManager, @Nullable URI subjectURI, TemporaryContextURI contextURI) {
        super(entityManager, subjectURI, contextURI);
    }

    @Override
    public @Nullable MultilingualString resolveDescription() {
        return findMultilingualProperty(
                Set.of(URI.create(SKOS_PREFIX + "definition"), URI.create(SKOS_PREFIX + "note")));
    }

    @Override
    public @Nullable MultilingualString resolveTitle() {
        return findMultilingualProperty(
                Set.of(URI.create(SKOS_PREFIX + "prefLabel"), URI.create(SKOS_PREFIX + "altLabel")));
    }
}
