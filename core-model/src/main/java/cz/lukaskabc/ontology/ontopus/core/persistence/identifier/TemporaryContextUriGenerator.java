package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import org.springframework.stereotype.Component;

@Component
public class TemporaryContextUriGenerator extends AbstractIdentifierGenerator<TemporaryContextURI, TemporaryContext> {
    public TemporaryContextUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public TemporaryContextURI generate(TemporaryContext entity) {
        return null;
    }
}
