package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class VersionSeriesUriGenerator extends AbstractIdentifierGenerator<VersionSeriesURI, VersionSeries> {
    public VersionSeriesUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @NonNull @Override
    public VersionSeriesURI generate(VersionSeries entity) {
        Objects.requireNonNull(entity);
        String title = sanitizeString(entity.getTitle());

        int attempt = 0;
        final String base = VersionSeries_.entityClassIRI + "/" + title;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(base + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new VersionSeriesURI(generated);
            }

            attempt++;
        }
        throw failedToGenerate(entity);
    }
}
