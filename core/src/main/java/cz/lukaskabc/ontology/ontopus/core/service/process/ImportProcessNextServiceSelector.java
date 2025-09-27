package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import java.util.Collection;
import org.jspecify.annotations.Nullable;

public class ImportProcessNextServiceSelector<S extends ImportProcessingService<?>>
        implements ImportProcessingService<S> {

    private final Collection<S> services;

    public ImportProcessNextServiceSelector(Collection<S> services) {
        this.services = services;
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.ImportProcessingService.ImportProcessNextServiceSelector.name";
    }

    @Override
    public Result<S> handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }
}
