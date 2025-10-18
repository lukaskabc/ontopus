package cz.lukaskabc.ontology.ontopus.plugin.dcatmapper;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact_;
import java.net.URI;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class StandardDatasetMapper extends PropertyMapper {
    public static void mapAll(
            EntityManager entityManager, @Nullable URI subjectURI, URI contextURI, VersionArtifact artifact) {
        new StandardDatasetMapper(entityManager, subjectURI, contextURI, artifact).mapAll();
    }

    private final VersionArtifact artifact;

    protected StandardDatasetMapper(
            EntityManager entityManager, @Nullable URI subjectURI, URI contextURI, VersionArtifact artifact) {
        super(entityManager, subjectURI, contextURI);
        this.artifact = artifact;
    }

    public void mapAll() {
        mapDescription();
        mapTitle();
        mapReleaseDate();
        mapModifiedDate();
        mapLanguages();
        mapVersion();
    }

    protected void mapDescription() {
        MultilingualString object =
                findSingleProperty(property(VersionArtifact_.description), MultilingualString.class);
        if (object != null) {
            mergeMultilingualString(artifact::getDescription, artifact::setDescription, object);
        }
    }

    protected void mapLanguages() {
        List<String> languages = findProperties(property(VersionArtifact_.languages), String.class);
        if (artifact.getLanguages() == null) {
            artifact.setLanguages(new HashSet<>());
        }
        artifact.getLanguages().addAll(languages);
    }

    protected void mapModifiedDate() {
        Instant date = findSingleProperty(property(VersionArtifact_.modifiedDate), Instant.class);
        if (date != null) {
            artifact.setModifiedDate(date);
        }
    }

    protected void mapReleaseDate() {
        Instant date = findSingleProperty(property(VersionArtifact_.releaseDate), Instant.class);
        if (date != null) {
            artifact.setReleaseDate(date);
        }
    }

    protected void mapTitle() {
        MultilingualString object = findSingleProperty(property(VersionArtifact_.title), MultilingualString.class);
        if (object != null) {
            mergeMultilingualString(artifact::getTitle, artifact::setTitle, object);
        }
    }

    protected void mapVersion() {
        String version = findSingleProperty(property(VersionArtifact_.version), String.class);
        if (version != null) {
            artifact.setVersion(version);
        }
    }

    protected Set<URI> property(Attribute<?, ?> attribute) {
        return Set.of(attribute.getIRI().toURI());
    }
}
