package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ControllerURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.Controller;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.Controller_;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class MappingControllerUriGenerator extends AbstractIdentifierGenerator<ControllerURI, Controller> {
    public MappingControllerUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public @Nullable ControllerURI generate(Controller entity) {
        Objects.requireNonNull(entity);

        String baseId = Controller_.entityClassIRI + StringUtils.sanitize(entity.getClassName());

        int attempt = 0;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(baseId + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new ControllerURI(generated);
            }

            attempt++;
        }

        throw new IllegalStateException("Unable to generate identifier for " + entity);
    }
}
