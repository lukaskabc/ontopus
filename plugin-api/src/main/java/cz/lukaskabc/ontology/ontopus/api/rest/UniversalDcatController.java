package cz.lukaskabc.ontology.ontopus.api.rest;

import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.*;
import org.springframework.http.ResponseEntity;

/** Entity type independent controller capable of providing an internal DCAT entity in a supported format */
public interface UniversalDcatController extends CatalogController, VersionArtifactController, VersionSeriesController {
    @Override
    default ResponseEntity<StreamingResponseBody> getCatalog(DcatEntityRequest<OntopusCatalogURI> request) {
        return handleRequest(request);
    }

    @Override
    default ResponseEntity<StreamingResponseBody> getVersionArtifact(DcatEntityRequest<VersionArtifactURI> request) {
        return handleRequest(request);
    }

    @Override
    default ResponseEntity<StreamingResponseBody> getVersionSeries(DcatEntityRequest<VersionSeriesURI> request) {
        return handleRequest(request);
    }

    /**
     * Handles the request for an DCAT entity
     *
     * @param request the request
     * @return the entity in the requested format
     */
    ResponseEntity<StreamingResponseBody> handleRequest(DcatEntityRequest<? extends ResourceURI> request);
}
