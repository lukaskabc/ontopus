package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifactCatalog;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactCatalogURI;
import org.springframework.stereotype.Component;

@Component
public class CatalogUriUriGenerator implements IdentifierGenerator<ArtifactCatalogURI, OntologyArtifactCatalog> {
    private final ArtifactCatalogURI uri;

    public CatalogUriUriGenerator(OntopusConfig ontopusConfig) {
        this.uri = new ArtifactCatalogURI(ontopusConfig.getDcatCatalog().getUri());
    }

    @Override
    public ArtifactCatalogURI generate(OntologyArtifactCatalog entity) {
        return uri;
    }
}
