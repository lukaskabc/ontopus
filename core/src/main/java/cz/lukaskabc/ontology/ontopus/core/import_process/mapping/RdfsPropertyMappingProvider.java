package cz.lukaskabc.ontology.ontopus.core.import_process.mapping;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractEntityIdentifier;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class RdfsPropertyMappingProvider extends PropertyMapper implements ArtifactPropertyMappingProvider {
    private static final String RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String RDFS_PREFIX = "http://www.w3.org/2000/01/rdf-schema#";

    public RdfsPropertyMappingProvider(
            EntityManager entityManager, @Nullable URI subjectURI, AbstractEntityIdentifier contextURI) {
        super(entityManager, subjectURI, contextURI);
    }

    @Override
    public @Nullable MultilingualString resolveDescription() {
        return findMultilingualProperty(Set.of(URI.create(RDFS_PREFIX + "comment")));
    }

    @Override
    public @Nullable Set<String> resolveLanguages() {
        return new HashSet<>(findProperties(Set.of(URI.create(RDF_PREFIX + "language")), String.class));
    }

    @Override
    public @Nullable MultilingualString resolveTitle() {
        return findMultilingualProperty(Set.of(URI.create(RDFS_PREFIX + "label")));
    }
}
