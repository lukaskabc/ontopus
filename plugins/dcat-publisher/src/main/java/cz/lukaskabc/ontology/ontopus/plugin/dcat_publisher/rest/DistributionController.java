package cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest;

import cz.lukaskabc.ontology.ontopus.api.rest.NegotiableController;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.request.DcatEntityRequest;
import org.springframework.http.ResponseEntity;

public interface DistributionController extends NegotiableController {
    /**
     * Provides the requested distribution in the requested media type.
     *
     * @param request the request information
     * @return the serialized entity in the requested media type.
     */
    ResponseEntity<StreamingResponseBody> getDistribution(DcatEntityRequest<DistributionURI> request);
}
