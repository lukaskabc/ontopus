package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core.model.id.OntopusCatalogURI;
import org.springframework.stereotype.Component;

@Component
public class CatalogUriUriGenerator implements IdentifierGenerator<OntopusCatalogURI, OntopusCatalog> {
    private final OntopusCatalogURI uri;

    public CatalogUriUriGenerator(OntopusConfig ontopusConfig) {
        this.uri = new OntopusCatalogURI(ontopusConfig.getDcatCatalog().getUri());
    }

    @Override
    public OntopusCatalogURI generate(OntopusCatalog entity) {
        return uri;
    }
}
