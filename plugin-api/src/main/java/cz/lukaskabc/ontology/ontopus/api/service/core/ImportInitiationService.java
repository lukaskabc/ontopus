package cz.lukaskabc.ontology.ontopus.api.service.core;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.ImportProcessContextRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.concurrent.Future;

@Validated
public interface ImportInitiationService {
    void initializeImport(@Nullable VersionSeriesURI uri);

    Future<@Nullable Void> submitCombinedData(@Valid ImportProcessContextRequest context);

    Future<@Nullable Void> submitData(FormJsonDataDto jsonData, MultiValueMap<String, MultipartFile> files);
}
