package cz.lukaskabc.ontology.ontopus.tests.plugin.rdf.publishing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Agent;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.*;
import cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing.DcatRDFController;
import cz.lukaskabc.ontology.ontopus.tests.config.TestJsonConfig;
import cz.lukaskabc.ontology.ontopus.tests.integration.BaseIntegrationTest;
import cz.lukaskabc.ontology.ontopus.tests.util.EntityAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.Instant;
import java.util.*;

@Import(DcatRDFController.class)
public class DcatRDFControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    DcatRDFController sut;

    final ObjectMapper jsonLdMapper = TestJsonConfig.jsonLdMapper();

    final GraphURI catalogGraph = new GraphURIImpl(OntopusCatalog_.entityClassIRI.toURI());
    final GraphURI seriesGraph = new GraphURIImpl(VersionSeries_.entityClassIRI.toURI());
    final GraphURI artifactGraph = new GraphURIImpl(VersionArtifact_.entityClassIRI.toURI());

    final MediaType jsonLd = MediaType.valueOf("application/ld+json");

    VersionArtifact artifactFirst;
    VersionArtifact artifactLast;
    VersionSeries series;

    @Test
    void getCatalogReturnsRequestedCatalog() throws Exception {
        final Agent agent = new Agent();
        agent.setIdentifier(new AgentURI("http://example.com/agent/" + UUID.randomUUID()));
        agent.setName(MultilingualString.create("Gandalf", "en"));
        agent.setTypes(new HashSet<>());
        agent.getTypes().add(Vocabulary.u_c_foaf_Person);

        final OntopusCatalog catalog = new OntopusCatalog();
        catalog.setIdentifier(new OntopusCatalogURI("http://example.com/catalog/" + UUID.randomUUID()));
        catalog.addVersionSeries(series.getIdentifier());
        catalog.setHomepage(new URI("http://example.com/catalog/homepage"));
        catalog.setDescription(MultilingualString.create("Example Description", "en"));
        catalog.setTitle(MultilingualString.create("Example Catalog", "en"));
        catalog.setModifiedDate(Instant.now());
        catalog.setReleaseDate(Instant.now());
        catalog.setPublisher(agent);

        withEntities(catalog);

        DcatEntityRequest<OntopusCatalogURI> request =
                new DcatEntityRequest<>(catalog.getIdentifier(), catalogGraph, jsonLd);

        ResponseEntity<StreamingResponseBody> response = sut.getCatalog(request);
        OntopusCatalog result = readResponse(response, OntopusCatalog.class);

        assertNotNull(result);
        EntityAssertions.assertCatalogEquals(catalog, result);
    }

    @Test
    void getVersionArtifactReturnsRequestedVersionArtifact() throws Exception {
        DcatEntityRequest<VersionArtifactURI> request =
                new DcatEntityRequest<>(artifactFirst.getIdentifier(), artifactGraph, jsonLd);

        ResponseEntity<StreamingResponseBody> response = sut.getVersionArtifact(request);
        VersionArtifact result = readResponse(response, VersionArtifact.class);

        assertNotNull(result);
        EntityAssertions.assertArtifactEquals(artifactFirst, result);
    }

    @Test
    void getVersionSeriesReturnsRequestedVersionSeries() throws Exception {
        DcatEntityRequest<VersionSeriesURI> request =
                new DcatEntityRequest<>(series.getIdentifier(), seriesGraph, jsonLd);

        ResponseEntity<StreamingResponseBody> response = sut.getVersionSeries(request);
        VersionSeries result = readResponse(response, VersionSeries.class);

        assertNotNull(result);
        EntityAssertions.assertSeriesEquals(series, result);
    }

    <T> T readResponse(ResponseEntity<StreamingResponseBody> response, Class<T> type) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        response.getBody().writeTo(os);
        String responseBody = os.toString();

        assertNotNull(responseBody);
        assertFalse(responseBody.isBlank());

        OWLClass owlClass = type.getAnnotation(OWLClass.class);

        if (owlClass != null) {
            Object jsonObject = JsonUtils.fromString(responseBody);

            Map<String, Object> frame = new HashMap<>();
            frame.put("@type", owlClass.iri());

            JsonLdOptions options = new JsonLdOptions();
            Map<String, Object> framedResult = JsonLdProcessor.frame(jsonObject, frame, options);

            List<Object> expandedResult = JsonLdProcessor.expand(framedResult, options);

            if (expandedResult != null && !expandedResult.isEmpty()) {
                String expandedJson = JsonUtils.toString(expandedResult.get(0));
                return jsonLdMapper.readValue(expandedJson, type);
            }
        }

        return jsonLdMapper.readValue(responseBody, type);
    }

    @BeforeEach
    void setUp() {
        UUID uuid = UUID.randomUUID();
        Instant now = Instant.now();

        series = new VersionSeries();
        series.setIdentifier(new VersionSeriesURI("http://example.com/series/" + uuid));
        series.setOntologyURI(new OntologyURI("http://example.com/ontology/" + uuid));
        series.setTitle(MultilingualString.create("Example Ontology Series", "en"));
        series.setReleaseDate(now);
        series.setModifiedDate(now);
        series.setVersion("1.0.x");

        artifactFirst = new VersionArtifact();
        artifactFirst.setIdentifier(new VersionArtifactURI("http://example.com/artifact/1-" + uuid));
        artifactFirst.setVersionUri(new OntologyVersionURI("http://example.com/ontology/v1-" + uuid));
        artifactFirst.setVersion("1.0.0");
        artifactFirst.setSeries(series.getIdentifier());
        artifactFirst.setTitle(MultilingualString.create("Example Ontology v1", "en"));
        artifactFirst.setReleaseDate(now);
        artifactFirst.setModifiedDate(now);

        artifactLast = new VersionArtifact();
        artifactLast.setIdentifier(new VersionArtifactURI("http://example.com/artifact/2-" + uuid));
        artifactLast.setVersionUri(new OntologyVersionURI("http://example.com/ontology/v2-" + uuid));
        artifactLast.setVersion("2.0.0");
        artifactLast.setSeries(series.getIdentifier());
        artifactLast.setTitle(MultilingualString.create("Example Ontology v2", "en"));
        artifactLast.setReleaseDate(now);
        artifactLast.setModifiedDate(now);
        artifactLast.setPreviousVersion(artifactFirst.getIdentifier());

        series.addMember(artifactFirst.getIdentifier());
        series.addMember(artifactLast.getIdentifier());

        series.setFirst(artifactFirst.getIdentifier());
        series.setLast(artifactLast.getIdentifier());

        withEntities(series, artifactFirst, artifactLast);
    }
}
