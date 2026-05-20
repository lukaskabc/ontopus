package cz.lukaskabc.ontology.ontopus.api.rest;

import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.springframework.http.ResponseEntity;

public interface VersionSeriesController extends NegotiableController {
    /**
     * Provides the requested version series in the requested media type.
     *
     * @param request the request information
     * @return the serialized entity in the requested media type.
     */
    ResponseEntity<StreamingResponseBody> getVersionSeries(DcatEntityRequest<VersionSeriesURI> request);
}
