package cz.lukaskabc.ontology.ontopus.tests.util;

import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Agent;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Dataset;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Resource;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import org.junit.jupiter.api.Assertions;

public class EntityAssertions {
    public static void assertAgentEquals(Agent expected, Agent actual) {
        // not asserting identifier
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertTrue(actual.getTypes().containsAll(expected.getTypes()));
        Assertions.assertEquals(expected.getTypes().size(), actual.getTypes().size());
    }

    public static void assertArtifactEquals(VersionArtifact expected, VersionArtifact actual) {
        Assertions.assertTrue(actual.getTypes().containsAll(expected.getTypes()));
        Assertions.assertEquals(expected.getVersionUri(), actual.getVersionUri());
        Assertions.assertEquals(expected.getPrefixDeclarations(), actual.getPrefixDeclarations());
        Assertions.assertEquals(expected.getSeries(), actual.getSeries());
        assertDatasetEquals(expected, actual);
    }

    public static void assertCatalogEquals(OntopusCatalog expected, OntopusCatalog actual) {
        Assertions.assertEquals(expected.getHomepage(), actual.getHomepage());
        assertResourceEquals(expected, actual);
        assertAgentEquals(expected.getPublisher(), actual.getPublisher());
    }

    public static <D extends TypedIdentifier, I extends TypedIdentifier> void assertDatasetEquals(
            Dataset<D, I> expected, Dataset<D, I> actual) {
        Assertions.assertEquals(expected.getLanguages(), actual.getLanguages());
        Assertions.assertEquals(expected.getVersion(), actual.getVersion());
        Assertions.assertEquals(expected.getPreviousVersion(), actual.getPreviousVersion());
        assertResourceEquals(expected, actual);
    }

    public static <I extends TypedIdentifier> void assertResourceEquals(Resource<I> expected, Resource<I> actual) {
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getReleaseDate(), actual.getReleaseDate());
        Assertions.assertEquals(expected.getModifiedDate(), actual.getModifiedDate());
    }

    public static void assertSeriesEquals(VersionSeries expected, VersionSeries actual) {
        Assertions.assertTrue(actual.getTypes().containsAll(expected.getTypes()));
        Assertions.assertEquals(
                expected.getSerializableImportProcessContext(), actual.getSerializableImportProcessContext());
        Assertions.assertEquals(expected.getOntologyURI(), actual.getOntologyURI());
        Assertions.assertEquals(expected.getLast(), actual.getLast());
        Assertions.assertEquals(expected.getFirst(), actual.getFirst());
        Assertions.assertTrue(actual.getMembers().containsAll(expected.getMembers()));
        Assertions.assertEquals(
                expected.getMembers().size(), actual.getMembers().size());
    }

    private EntityAssertions() {
        throw new AssertionError();
    }
}
