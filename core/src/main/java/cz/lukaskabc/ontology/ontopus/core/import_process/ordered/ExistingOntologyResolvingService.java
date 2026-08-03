package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.import_process.ExistingVersionArtifactResolvingService;
import cz.lukaskabc.ontology.ontopus.core.import_process.ImportProcessServiceOrder;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
import cz.lukaskabc.ontology.ontopus.core_model.util.EntityMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Delays pushing of {@link ExistingVersionArtifactResolvingService} to the service stack to the moment when this
 * service is auto-processed.
 *
 * @see ExistingVersionArtifactResolvingService
 */
@Service
@Order(ImportProcessServiceOrder.EXISTING_ONTOLOGY_RESOLVING_SERVICE)
public class ExistingOntologyResolvingService implements OrderedImportPipelineService<Void> {
    private static final Logger log = LogManager.getLogger(ExistingOntologyResolvingService.class);

    private final ImportProcessingService<?> service;

    public ExistingOntologyResolvingService(
            VersionArtifactService versionArtifactService, ObjectMapper objectMapper, EntityMapper entityMapper) {
        this.service = new ExistingVersionArtifactResolvingService(versionArtifactService, objectMapper, entityMapper);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        if (context.peekService() != this) {
            throw log.throwing(InternalException.unexpectedServiceStackState());
        }
        context.popService(); // pop self

        context.pushService(service);
        return null;
    }
}
