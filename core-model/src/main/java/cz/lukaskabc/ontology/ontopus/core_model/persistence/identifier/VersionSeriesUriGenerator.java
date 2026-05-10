package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.util.DcatIdentifierProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class VersionSeriesUriGenerator extends AbstractIdentifierGenerator<VersionSeriesURI, VersionSeries> {
    private final DcatIdentifierProvider dcatIdentifierProvider;

    public VersionSeriesUriGenerator(
            EntityManager entityManager, OntopusConfig config, DcatIdentifierProvider dcatIdentifierProvider) {
        super(entityManager, config);
        this.dcatIdentifierProvider = dcatIdentifierProvider;
    }

    @NonNull @Override
    public VersionSeriesURI generate(VersionSeries entity) {
        Objects.requireNonNull(entity);
        final String label = sanitizeString(entity.getTitle());
        final VersionSeriesURI generated = dcatIdentifierProvider.getVersionSeriesUri(entity.getOntologyURI(), label);

        // Not trying more attempts, the ID should be unique for the given ontology
        if (isUnique(generated.toURI())) {
            return generated;
        }

        throw failedToGenerate(entity);
    }
}
