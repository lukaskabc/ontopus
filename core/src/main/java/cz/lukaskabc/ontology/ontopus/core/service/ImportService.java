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

import java.util.Map;
import java.util.concurrent.Future;

@NullMarked
@Service
public class ImportService {

    private final ImportProcessMediator mediator;
    private final RequestFileResolver requestFileResolver;

    public ImportService(ImportProcessMediator mediator, RequestFileResolver requestFileResolver) {
        this.mediator = mediator;
        this.requestFileResolver = requestFileResolver;
    }

    public Future<@Nullable JsonForm> getCurrentJsonForm() {
        return mediator.getCurrentForm(); // TODO include form data when publishing new version of
    }

    public void initializeImport(@Nullable VersionSeriesURI uri) {
        mediator.initialize(uri);
    }

    public Future<@Nullable Void> submitCombinedData(ImportProcessContextRequest context) {
        return mediator.submitCombinedFormResult(context.getSerializableImportProcessContext());
    }

    public Future<@Nullable Void> submitData(FormJsonDataDto jsonData, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> reusableFiles =
                requestFileResolver.resolveAndCopyFiles(jsonData.values().iterator(), files);
        return mediator.submitFormResult(jsonData, reusableFiles);
    }
}
