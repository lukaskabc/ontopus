package cz.lukaskabc.ontology.ontopus.core.import_process.mapping;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class DcatPropertyMappingProvider extends PropertyMapper implements ArtifactPropertyMappingProvider {
    public DcatPropertyMappingProvider(
            EntityManager entityManager, @Nullable ResourceURI subjectURI, TemporaryContextURI contextURI) {
        super(entityManager, subjectURI, contextURI);
    }

    @Nullable @Override
    public MultilingualString resolveDescription() {
        return findMultilingualProperty(mapAttributes(VersionArtifact_.description));
    }

    @Nullable @Override
    public Set<String> resolveLanguages() {
        return new HashSet<>(findProperties(mapAttributes(VersionArtifact_.languages), String.class));
    }

    @Nullable @Override
    public Instant resolveModifiedDate() {
        return findSingleProperty(mapAttributes(VersionArtifact_.modifiedDate), Instant.class);
    }

    @Nullable @Override
    public Instant resolveReleaseDate() {
        return findSingleProperty(mapAttributes(VersionArtifact_.releaseDate), Instant.class);
    }

    @Nullable @Override
    public MultilingualString resolveTitle() {
        return findMultilingualProperty(mapAttributes(VersionArtifact_.title));
    }

    @Nullable @Override
    public String resolveVersion() {
        return findSingleProperty(mapAttributes(VersionArtifact_.version), String.class);
    }
}
