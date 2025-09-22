package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.Distribution;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyDistribution_;
import cz.lukaskabc.ontology.ontopus.core.model.id.DistributionURI;
import java.net.URI;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class DistributionUriGenerator extends AbstractIdentifierGenerator<DistributionURI, Distribution> {
    public DistributionUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public DistributionURI generate(Distribution entity) {
        Objects.requireNonNull(entity);
        String title = sanitizeString(entity::getTitle);
        String format = sanitizeString(entity.getFormat());

        int attempt = 0;
        final String base = OntologyDistribution_.entityClassIRI + "_" + title + "_" + format;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(base + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new DistributionURI(generated);
            }

            attempt++;
        }
        throw new IllegalStateException("Unable to generate identifier for " + entity.toString());
    }
}
