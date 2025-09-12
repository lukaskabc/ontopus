package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

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
