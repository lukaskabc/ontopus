package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyDistribution_;
import cz.lukaskabc.ontology.ontopus.core.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import java.net.URI;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class VersionSeriesUriGenerator extends AbstractIdentifierGenerator<VersionSeriesURI, VersionSeries> {
    public VersionSeriesUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public VersionSeriesURI generate(VersionSeries entity) {
        Objects.requireNonNull(entity);
        String title = sanitizeString(entity::getTitle);
        // TODO consider using the ontology identifier which should be unique?

        int attempt = 0;
        final String base = OntologyDistribution_.entityClassIRI + "_" + title;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(base + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new VersionSeriesURI(generated);
            }

            attempt++;
        }
        throw new IllegalStateException("Unable to generate identifier for " + entity.toString());
    }
}
