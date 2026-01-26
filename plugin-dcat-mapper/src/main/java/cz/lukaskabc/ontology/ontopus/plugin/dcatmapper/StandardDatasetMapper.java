package cz.lukaskabc.ontology.ontopus.plugin.dcatmapper;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.ontodriver.model.LangString;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact_;
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
        LangString object = findSingleProperty(property(VersionArtifact_.description), LangString.class);
        if (object != null) {
            mergeMultilingualString(artifact::getDescription, artifact::setDescription, mapLangString(object));
        }
    }

    protected MultilingualString mapLangString(LangString langString) {
        return MultilingualString.create(langString.getLanguage().orElse(null), langString.getValue());
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
        LangString object = findSingleProperty(property(VersionArtifact_.title), LangString.class);
        if (object != null) {
            mergeMultilingualString(artifact::getTitle, artifact::setTitle, mapLangString(object));
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
