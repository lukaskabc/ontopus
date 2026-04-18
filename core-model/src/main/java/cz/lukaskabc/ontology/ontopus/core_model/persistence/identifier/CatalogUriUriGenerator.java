package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.IdentifierGenerationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CatalogUriUriGenerator implements IdentifierGenerator<OntopusCatalogURI, OntopusCatalog> {
    private static final Logger log = LogManager.getLogger(CatalogUriUriGenerator.class);
    private final OntopusCatalogURI uri;

    public CatalogUriUriGenerator(OntopusConfig ontopusConfig) {
        this.uri = ontopusConfig.getDcatCatalog().getUri();
    }

    @Override
    public OntopusCatalogURI generate(OntopusCatalog entity) {
        return uri;
    }

    @Override
    public void setIdentifierIfMissing(OntopusCatalog entity) {
        if (entity.getIdentifier() == null) {
            OntopusCatalogURI uri = generate(entity);
            if (uri == null) {
                throw log.throwing(new IdentifierGenerationException("Failed to generate ontopus catalog URI"));
            }
            entity.setIdentifier(uri);
        }
    }
}
