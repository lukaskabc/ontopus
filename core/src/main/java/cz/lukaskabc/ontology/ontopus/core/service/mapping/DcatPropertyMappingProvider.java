package cz.lukaskabc.ontology.ontopus.core.service.mapping;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class DcatPropertyMappingProvider extends PropertyMapper implements ArtifactPropertyMappingProvider {
    public DcatPropertyMappingProvider(
            EntityManager entityManager, @Nullable URI subjectURI, TemporaryContextURI contextURI) {
        super(entityManager, subjectURI, contextURI);
    }

    @Override
    public MultilingualString resolveDescription() {
        return findMultilingualProperty(mapAttributes(VersionArtifact_.description));
    }

    @Override
    public Set<String> resolveLanguages() {
        return new HashSet<>(findProperties(mapAttributes(VersionArtifact_.languages), String.class));
    }

    @Override
    public Instant resolveModifiedDate() {
        return findSingleProperty(mapAttributes(VersionArtifact_.modifiedDate), Instant.class);
    }

    @Override
    public Instant resolveReleaseDate() {
        return findSingleProperty(mapAttributes(VersionArtifact_.releaseDate), Instant.class);
    }

    @Override
    public MultilingualString resolveTitle() {
        return findMultilingualProperty(mapAttributes(VersionArtifact_.title));
    }

    @Override
    public String resolveVersion() {
        return findSingleProperty(mapAttributes(VersionArtifact_.version), String.class);
    }
}
