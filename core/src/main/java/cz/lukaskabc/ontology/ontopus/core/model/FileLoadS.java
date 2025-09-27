package cz.lukaskabc.ontology.ontopus.core.model;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.FileLoadingService;
import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class FileLoadS implements FileLoadingService {
    @Override
    public @Nullable JsonForm getJsonForm() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Result<Path> handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }
}
