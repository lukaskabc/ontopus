package cz.lukaskabc.ontology.ontopus.tests.util;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class EntityGenerator {

    public static VersionArtifact artifact(@Nullable VersionArtifactURI identifier) {
        if (identifier == null) {
            identifier = new VersionArtifactURI("http://example.com/artifact/" + UUID.randomUUID());
        }
        VersionArtifact artifact = new VersionArtifact();
        artifact.setIdentifier(identifier);
        artifact.setVersionUri(new OntologyVersionURI("http://example.com/version/" + UUID.randomUUID()));
        artifact.setTitle(MultilingualString.create("Version artifact for testing - " + UUID.randomUUID(), "en"));
        artifact.setReleaseDate(Instant.now());
        artifact.setModifiedDate(Instant.now());
        return artifact;
    }

    public static VersionSeries series(@Nullable VersionSeriesURI identifier) {
        if (identifier == null) {
            identifier = new VersionSeriesURI("http://example.com/series/" + UUID.randomUUID());
        }
        VersionSeries series = new VersionSeries();
        series.setIdentifier(identifier);
        series.setOntologyURI(new OntologyURI("http://example.com/ontology/" + UUID.randomUUID()));
        series.setTitle(MultilingualString.create("Version series for testing - " + UUID.randomUUID(), "en"));
        return series;
    }

    private EntityGenerator() {
        throw new AssertionError();
    }
}
