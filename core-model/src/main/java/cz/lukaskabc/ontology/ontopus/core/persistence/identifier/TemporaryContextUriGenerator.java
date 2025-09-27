package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import java.net.URI;
import java.util.Objects;
import org.springframework.stereotype.Component;

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
