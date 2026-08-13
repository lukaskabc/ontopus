package cz.lukaskabc.ontology.ontopus.api.rest;

import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import org.springframework.http.ResponseEntity;

/**
 * Controller capable of providing the internal Catalog DCAT entity in a supported format
 *
 * @see cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog
 */
public interface CatalogController extends NegotiableController {
    /**
     * Provides the catalog in the requested media type.
     *
     * @param request the request information
     * @return the serialized entity in the requested media type.
     */
    ResponseEntity<StreamingResponseBody> getCatalog(DcatEntityRequest<OntopusCatalogURI> request);
}
