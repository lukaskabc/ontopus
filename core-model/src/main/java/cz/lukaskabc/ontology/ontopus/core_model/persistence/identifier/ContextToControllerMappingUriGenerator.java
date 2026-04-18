package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Iterator;
import java.util.Objects;

@Component
public class ContextToControllerMappingUriGenerator
        extends AbstractIdentifierGenerator<ContextToControllerMappingURI, ContextToControllerMapping> {
    private static final Logger log = LogManager.getLogger(ContextToControllerMappingUriGenerator.class);

    public ContextToControllerMappingUriGenerator(EntityManager entityManager, OntopusConfig config) {
        super(entityManager, config);
    }

    public GraphURI anySubject(ContextToControllerMapping entity) {
        final Iterator<GraphURI> it = entity.getSubjects().iterator();
        if (!it.hasNext()) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_internal_error)
                    .internalMessage("Entity has no subjects: " + entity)
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build());
        }
        return it.next();
    }

    @Override
    public @Nullable ContextToControllerMappingURI generate(ContextToControllerMapping entity) {
        Objects.requireNonNull(entity);

        String baseId = anySubject(entity) + "_" + entity.getMappingType().name();

        int attempt = 0;
        while (attempt < MAX_GENERATION_ATTEMPTS) {
            URI generated = URI.create(baseId + (attempt > 0 ? attempt : ""));

            if (isUnique(generated)) {
                return new ContextToControllerMappingURI(generated);
            }

            attempt++;
        }

        throw failedToGenerate(entity);
    }
}
