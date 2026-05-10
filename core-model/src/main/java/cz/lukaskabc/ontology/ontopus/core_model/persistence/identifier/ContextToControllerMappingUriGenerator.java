package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class ContextToControllerMappingUriGenerator
        extends AbstractIdentifierGenerator<ContextToControllerMappingURI, ContextToControllerMapping> {

    public ContextToControllerMappingUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    @Override
    public @Nullable ContextToControllerMappingURI generate(ContextToControllerMapping entity) {
        Objects.requireNonNull(entity);
        final URI generated =
                URI.create(entity.getSubject() + "_" + entity.getMappingType().name());

        if (isUnique(generated)) {
            return new ContextToControllerMappingURI(generated);
        }

        throw failedToGenerate(entity);
    }
}
