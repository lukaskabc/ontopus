package cz.lukaskabc.ontology.ontopus.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.rest.dto.ReusableFileDto;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessMediator;
import java.io.File;
import java.util.*;
import java.util.concurrent.Future;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportService {
    private static MultipartFile findUploadedFile(
            MultiValueMap<String, MultipartFile> files, String formFieldName, String fileName) {
        List<MultipartFile> fieldFiles = files.get(formFieldName);
        if (fieldFiles != null) {
            for (MultipartFile file : fieldFiles) {
                if (fileName.equals(file.getOriginalFilename())) {
                    return file;
                }
            }
        }
        throw new IllegalStateException("Could not find uploaded file for " + fileName);
    }

    private final ImportProcessMediator mediator;

    private final ObjectMapper objectMapper;

    private final Validator validator; // TODO use FileUtils to check paths of uploaded files and prevent path
    // traversal

    public ImportService(ImportProcessMediator mediator, ObjectMapper objectMapper, Validator validator) {
        this.mediator = mediator;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    private void findReusableFiles(Iterator<JsonNode> iterator, List<ReusableFileDto> reusableFiles) {
        while (iterator.hasNext()) {
            final JsonNode node = iterator.next();
            if (ReusableFileDto.matches(node)) {
                reusableFiles.add(objectMapper.convertValue(node, ReusableFileDto.class));
            } else {
                findReusableFiles(node.iterator(), reusableFiles);
            }
        }
    }

    /**
     * Finds all reusable files inside {@code jsonData}
     *
     * @param jsonData the data to search
     * @return the list
     */
    private List<ReusableFileDto> findReusableFiles(Map<String, JsonNode> jsonData) {
        ArrayList<ReusableFileDto> reusableFiles = new ArrayList<>();
        findReusableFiles(jsonData.values().iterator(), reusableFiles);
        reusableFiles.trimToSize();
        return reusableFiles;
    }

    public Future<JsonForm> getCurrentJsonForm() {
        return mediator.getCurrentForm(); // TODO include form data when publishing new version of
    }

    public void initializeImport(@Nullable VersionSeriesURI uri) {
        mediator.initialize(uri);
    }

    /**
     * Finds reusable files in the jsonData
     *
     * @param jsonData
     * @param files
     */
    private Map<ReusableFileDto, InputStreamSource> resolveFiles(
            Map<String, JsonNode> jsonData, MultiValueMap<String, MultipartFile> files) {
        final List<ReusableFileDto> reusableFiles = findReusableFiles(jsonData);
        validator.validateObject(reusableFiles).failOnError(ValidationException::new);
        final Map<ReusableFileDto, InputStreamSource> result = new HashMap<>(reusableFiles.size());

        for (final ReusableFileDto reusableFile : reusableFiles) {
            InputStreamSource streamSource =
                    switch (reusableFile.getType()) {
                        case UPLOAD ->
                            findUploadedFile(files, reusableFile.getFormFieldName(), reusableFile.getFileName());
                        case SERVER -> {
                            // TODO resolve server cached file
                            File cachedFile = null;
                            yield new FileSystemResource(cachedFile);
                        }
                        default ->
                            throw new IllegalStateException("Unknown reusable file type: " + reusableFile.getType());
                    };

            Objects.requireNonNull(streamSource);
            result.put(reusableFile, streamSource);
        }
        return result;
    }

    public Future<?> submitCombinedData(
            Map<String, JsonNode> combinedData, MultiValueMap<String, MultipartFile> files) {
        Map<ReusableFileDto, InputStreamSource> reusableFiles = resolveFiles(combinedData, files);
        return mediator.submitCombinedFormResult(combinedData, reusableFiles);
    }

    public Future<?> submitData(Map<String, JsonNode> jsonData, MultiValueMap<String, MultipartFile> files) {
        Map<ReusableFileDto, InputStreamSource> reusableFiles = resolveFiles(jsonData, files);
        return mediator.submitFormResult(jsonData, reusableFiles);
    }
}
