package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Component
public class TemporaryContextUriGenerator extends AbstractIdentifierGenerator<TemporaryContextURI, TemporaryContext> {
    public TemporaryContextUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public TemporaryContextURI generate(TemporaryContext entity) {
        Objects.requireNonNull(entity);
        String baseId = TemporaryContext_.entityClassIRI + "/";

        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            URI generated = URI.create(baseId + UUID.randomUUID());

            if (isUnique(generated)) {
                return new TemporaryContextURI(generated);
            }
        }

        throw failedToGenerate(entity);
    }
}
