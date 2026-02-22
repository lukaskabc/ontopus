package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.rest.dto.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.core.rest.request.FormFileRequest;
import cz.lukaskabc.ontology.ontopus.core.rest.request.ImportProcessContextRequest;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessMediator;
import cz.lukaskabc.ontology.ontopus.core.util.RequestFileResolver;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

@NullMarked
@Service
public class ImportService {

    private final ImportProcessMediator mediator;

    private final ObjectMapper objectMapper;
    private final RequestFileResolver requestFileResolver;

    public ImportService(
            ImportProcessMediator mediator, ObjectMapper objectMapper, RequestFileResolver requestFileResolver) {
        this.mediator = mediator;
        this.objectMapper = objectMapper;
        this.requestFileResolver = requestFileResolver;
    }

    public Future<@Nullable JsonForm> getCurrentJsonForm() {
        return mediator.getCurrentForm(); // TODO include form data when publishing new version of
    }

    public void initializeImport(@Nullable VersionSeriesURI uri) {
        mediator.initialize(uri);
    }

    private Map<FormFileRequest, InputStreamSource> resolveCombinedFiles(
            ImportProcessContextRequest contextRequest, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> result = new HashMap<>(files.size());
        contextRequest.getServiceToFormResultMap().values().forEach((formResult) -> {
            Iterator<JsonNode> jsonDataIterator =
                    formResult.values().stream().map(objectMapper::readTree).iterator();
            result.putAll(requestFileResolver.resolveAndCopyFiles(jsonDataIterator, files));
        });
        return result;
    }

    public Future<@Nullable Void> submitCombinedData(
            ImportProcessContextRequest context, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> reusableFiles = resolveCombinedFiles(context, files);
        // return mediator.submitCombinedFormResult(context, reusableFiles);
        // TODO implement submitting combined data
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Future<@Nullable Void> submitData(FormJsonDataDto jsonData, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> reusableFiles =
                requestFileResolver.resolveAndCopyFiles(jsonData.values().iterator(), files);
        return mediator.submitFormResult(jsonData, reusableFiles);
    }
}
