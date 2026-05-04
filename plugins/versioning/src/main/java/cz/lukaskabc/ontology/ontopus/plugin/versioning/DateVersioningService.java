package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.format.DateTimeFormatter;

@Service
public class DateVersioningService implements OntologyVersioningService {
    private final TimeProvider timeProvider;

    public DateVersioningService(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OntologyVersioningService.DateVersioningService.title";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        final String version = timeProvider.getCurrentDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE);
        context.getVersionArtifact().setVersion(version);
        return null;
    }
}
