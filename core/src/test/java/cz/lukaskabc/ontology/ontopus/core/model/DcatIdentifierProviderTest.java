package cz.lukaskabc.ontology.ontopus.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

class DcatIdentifierProviderTest {
    private cz.lukaskabc.ontology.ontopus.core_model.util.DcatIdentifierProvider provider;

    @Test
    void getVersionArtifactUriReturnsStableIdentifier() {
        final OntologyVersionURI versionURI = new OntologyVersionURI("http://example.com/ontology/v1");
        final String label = "Very\\/Interesting\\\\_óntologý #- n4m3";
        final String version = "v.1";

        final String ontologyUriUUID = "92514e73-060d-3c7f-9b1c-b1320a283422";
        final String sanitizedLabel = "VeryInteresting_óntologý-n4m3";
        final String sanitizedVersion = "v1";
        final URI expected = URI.create("http://example.com/my/path/version-artifact/" + sanitizedLabel + "/"
                + sanitizedVersion + "/" + ontologyUriUUID);

        final VersionArtifactURI generated = provider.getVersionArtifactUri(versionURI, label, version);
        assertEquals(expected, generated.toURI());
    }

    @Test
    void getVersionSeriesUriReturnsStableIdentifier() {
        final OntologyURI ontologyURI = new OntologyURI("http://example.com/ontology");
        final String label = "Very\\/Interesting\\\\_óntologý #- n4m3";

        final String ontologyUriUUID = "9d514926-fa50-3e9d-a26f-0af72da73bfe";
        final String sanitizedLabel = "VeryInteresting_óntologý-n4m3";
        final URI expected =
                URI.create("http://example.com/my/path/version-series/" + sanitizedLabel + "/" + ontologyUriUUID);

        final VersionSeriesURI generated = provider.getVersionSeriesUri(ontologyURI, label);
        assertEquals(expected, generated.toURI());
    }

    @BeforeEach
    void setUp() {
        OntopusConfig config = new OntopusConfig();
        config.getDcatCatalog().setBaseUri(URI.create("http://example.com/my/path"));
        provider = new cz.lukaskabc.ontology.ontopus.core_model.util.DcatIdentifierProvider(config);
    }
}
