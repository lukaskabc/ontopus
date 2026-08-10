package cz.lukaskabc.ontology.ontopus.api.rest;

import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import org.springframework.http.ResponseEntity;

/**
 * Controller capable of providing an internal Version Artifact DCAT entity in a supported format
 *
 * @see cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact
 */
public interface VersionArtifactController extends NegotiableController {
    /**
     * Provides the requested version artifact in the requested media type.
     *
     * @param request the request information
     * @return the serialized entity in the requested media type.
     */
    ResponseEntity<StreamingResponseBody> getVersionArtifact(DcatEntityRequest<VersionArtifactURI> request);
}
