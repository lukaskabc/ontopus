package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class TemporaryContextUriGenerator extends AbstractIdentifierGenerator<TemporaryContextURI, TemporaryContext> {
    public TemporaryContextUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public TemporaryContextURI generate(TemporaryContext entity) {
        Objects.requireNonNull(entity);
        String baseId = VersionArtifact_.entityClassIRI.toString();

        int attempt = 0;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(baseId + attempt);

            if (isUnique(generated)) {
                return new TemporaryContextURI(generated);
            }

            attempt++;
        }

        throw new IllegalStateException("Unable to generate identifier for " + entity);
    }
}
