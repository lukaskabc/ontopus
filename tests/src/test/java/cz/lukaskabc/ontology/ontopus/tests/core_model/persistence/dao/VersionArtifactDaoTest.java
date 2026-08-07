package cz.lukaskabc.ontology.ontopus.tests.core_model.persistence.dao;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.Rdf4JAbstractNamespaceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.VersionArtifactDao;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import cz.lukaskabc.ontology.ontopus.tests.util.EntityGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class VersionArtifactDaoTest extends BaseDaoTest {
    @Autowired
    private VersionArtifactDao sut;

    private VersionArtifact artifact;
    private VersionSeries series;

    @Test
    void findPrefixDeclarationsReturnsSortedList() {
        AtomicInteger expectedCount = new AtomicInteger(0);
        Stream.of(
                        new PrefixDeclaration("aaa", URI.create("http://example.com/aa")),
                        new PrefixDeclaration("ab", URI.create("http://example.com/ab")),
                        new PrefixDeclaration("cd", URI.create("http://example.com/cd")),
                        new PrefixDeclaration("aa", URI.create("http://example.com/aa")),
                        new PrefixDeclaration("abb", URI.create("http://example.com/abb")))
                .forEach(prefix -> {
                    prefix.setIdentifier(
                            new Rdf4JAbstractNamespaceURI("http://example.com/prefix/" + UUID.randomUUID()));
                    artifact.addPrefixDeclaration(prefix);
                    expectedCount.incrementAndGet();
                });
        withEntities(List.of(series, artifact));

        List<PrefixDeclaration> result = sut.findPrefixDeclarations(artifact.getVersionUri());

        Assertions.assertEquals(expectedCount.get(), result.size());
        int i = 0;
        Assertions.assertEquals("aa", result.get(i++).getPrefix());
        Assertions.assertEquals("aaa", result.get(i++).getPrefix());
        Assertions.assertEquals("ab", result.get(i++).getPrefix());
        Assertions.assertEquals("abb", result.get(i++).getPrefix());
        Assertions.assertEquals("cd", result.get(i++).getPrefix());
        Assertions.assertEquals(expectedCount.get(), i);
    }

    @BeforeEach
    void setUp() {
        series = EntityGenerator.series(null);
        artifact = EntityGenerator.artifact(null);
        artifact.setSeries(series.getIdentifier());
        series.addMember(artifact.getIdentifier());
    }
}
