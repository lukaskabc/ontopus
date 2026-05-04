package cz.lukaskabc.ontology.ontopus.plugin.owl;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Set;

public class OwlPropertyMappingProvider extends PropertyMapper implements ArtifactPropertyMappingProvider {
    public OwlPropertyMappingProvider(
            EntityManager entityManager, @Nullable ResourceURI subjectURI, TemporaryContextURI contextURI) {
        super(entityManager, subjectURI, contextURI);
    }

    @Override
    public @Nullable URI resolveVersionURI() {
        return findSingleProperty(Set.of(URI.create("http://www.w3.org/2002/07/owl#versionIRI")), URI.class);
    }
}
