package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntologyDistribution;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntologyDistribution_;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class DistributionUriGenerator extends AbstractIdentifierGenerator<DistributionURI, OntologyDistribution> {
    public DistributionUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public DistributionURI generate(OntologyDistribution entity) {
        Objects.requireNonNull(entity);
        String title = sanitizeString(entity.getTitle());
        String format = StringUtils.sanitize(entity.getFormat());

        int attempt = 0;
        final String base = OntologyDistribution_.entityClassIRI + "/" + title + "/" + format;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(base + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new DistributionURI(generated);
            }

            attempt++;
        }
        throw failedToGenerate(entity);
    }
}
