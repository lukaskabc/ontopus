package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.core.ImportInitiationService;
import cz.lukaskabc.ontology.ontopus.core.import_process.ImportProcessMediator;
import cz.lukaskabc.ontology.ontopus.core.rest.request.FormFileRequest;
import cz.lukaskabc.ontology.ontopus.core.util.RequestFileResolver;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.ImportProcessContextRequest;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.concurrent.Future;

@Service
@Validated
@NullMarked
public class ImportService implements ImportInitiationService {

    private final ImportProcessMediator mediator;
    private final RequestFileResolver requestFileResolver;

    public ImportService(ImportProcessMediator mediator, RequestFileResolver requestFileResolver) {
        this.mediator = mediator;
        this.requestFileResolver = requestFileResolver;
    }

    public Future<@Nullable JsonForm> getCurrentJsonForm() {
        return mediator.getCurrentForm();
    }

    @Override
    public void initializeImport(@Nullable VersionSeriesURI uri) {
        mediator.initialize(uri);
    }

    @Override
    public Future<@Nullable Void> submitCombinedData(@Valid ImportProcessContextRequest context) {
        if (context.getVersionSeriesURI() == null) {
            throw JsonFormSubmitException.missingValue("version series URI");
        }
        initializeImport(context.getVersionSeriesURI());
        return mediator.submitCombinedFormResult(context.getSerializableImportProcessContext());
    }

    @Override
    public Future<@Nullable Void> submitData(FormJsonDataDto jsonData, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> reusableFiles =
                requestFileResolver.resolveAndCopyFiles(jsonData.values().iterator(), files);
        return mediator.submitFormResult(jsonData, reusableFiles);
    }
}
