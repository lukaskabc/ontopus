package cz.lukaskabc.ontology.ontopus.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessMediator;
import java.util.Map;
import java.util.concurrent.Future;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportService {
    private final ImportProcessMediator mediator;
    private final ObjectMapper objectMapper;

    public ImportService(ImportProcessMediator mediator, ObjectMapper objectMapper) {
        this.mediator = mediator;
        this.objectMapper = objectMapper;
    }

    public Future<JsonForm> getCurrentJsonForm() {
        return mediator.getCurrentForm(); // TODO include form data when publishing new version of
    }

    public void initializeImport(@Nullable VersionSeriesURI uri) {
        mediator.initialize(uri);
    }

    public Future<?> submitCombinedData(
            Map<String, JsonNode> combinedData, MultiValueMap<String, MultipartFile> files) {
        return mediator.submitCombinedFormResult(new FormResult(combinedData, files));
    }

    public Future<?> submitData(Map<String, JsonNode> jsonData, MultiValueMap<String, MultipartFile> files) {
        return mediator.submitFormResult(new FormResult(jsonData, files));
    }
}
