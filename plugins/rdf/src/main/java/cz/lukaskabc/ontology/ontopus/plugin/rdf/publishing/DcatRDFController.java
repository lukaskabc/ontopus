package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
public class DcatRDFController
        implements CatalogController, VersionSeriesController, VersionArtifactController, DistributionController {

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
