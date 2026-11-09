package cz.lukaskabc.ontology.ontopus.tests.util;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class EntityGenerator {

    public static URIAliasMapping aliasMapping() {
        return new URIAliasMapping(randomUri("resource"), randomUri("alias"));
    }

    public static List<URIAliasMapping> aliasMappings() {
        return IntStream.range(0, 10)
                .mapToObj(_ -> EntityGenerator.aliasMapping())
                .toList();
    }

    public static VersionArtifact artifact(@Nullable VersionArtifactURI identifier) {
        if (identifier == null) {
            identifier = new VersionArtifactURI(randomUri("artifact"));
        }
        VersionArtifact artifact = new VersionArtifact();
        artifact.setIdentifier(identifier);
        artifact.setVersionUri(new OntologyVersionURI(randomUri("version")));
        artifact.setTitle(MultilingualString.create("Version artifact for testing - " + UUID.randomUUID(), "en"));
        artifact.setReleaseDate(Instant.now());
        artifact.setModifiedDate(Instant.now());
        return artifact;
    }

    private static URI randomUri(String name) {
        return URI.create("http://example.com/" + name + "/" + UUID.randomUUID());
    }

    public static VersionSeries series(@Nullable VersionSeriesURI identifier) {
        if (identifier == null) {
            identifier = new VersionSeriesURI(randomUri("series"));
        }
        VersionSeries series = new VersionSeries();
        series.setIdentifier(identifier);
        series.setOntologyURI(new OntologyURI(randomUri("ontology")));
        series.setTitle(MultilingualString.create("Version series for testing - " + UUID.randomUUID(), "en"));
        return series;
    }

    private EntityGenerator() {
        throw new AssertionError();
    }
}
