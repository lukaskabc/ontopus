package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.DcatPublishingPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.CatalogController;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.DistributionController;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.VersionArtifactController;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.VersionSeriesController;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.request.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.service.DcatResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
@ConditionalOnClass(DcatPublishingPlugin.class)
public class DcatRDFController
        implements CatalogController, VersionSeriesController, VersionArtifactController, DistributionController {
    private final DcatResourceService dcatResourceService;

    public DcatRDFController(DcatResourceService dcatResourceService) {
        this.dcatResourceService = dcatResourceService;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getCatalog(DcatEntityRequest<OntopusCatalogURI> request) {
        return null;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDistribution(DcatEntityRequest<DistributionURI> request) {
        return null;
    }

    @Override
    public Set<MediaType> getSupportedMediaTypes() {
        return Set.of();
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getVersionArtifact(DcatEntityRequest<VersionArtifactURI> request) {
        return null;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getVersionSeries(DcatEntityRequest<VersionSeriesURI> request) {
        return null;
    }
}
