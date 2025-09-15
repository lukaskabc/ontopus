package cz.lukaskabc.ontology.ontopus.core.model;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OntoBuilS implements OrderedImportPipelineService<Void> {
    @Override
    public @Nullable JsonForm getJsonForm() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Result<Void> handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }
}
