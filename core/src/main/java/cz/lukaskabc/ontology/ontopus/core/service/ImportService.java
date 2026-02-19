package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.rest.request.FormFileRequest;
import cz.lukaskabc.ontology.ontopus.core.rest.request.ImportProcessContextRequest;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessMediator;
import cz.lukaskabc.ontology.ontopus.core.util.ConsumableInputStreamSource;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Future;

@Service
public class ImportService {
    private static final String UPLOAD_TEMP_FILE_PREFIX = "ontopus-uploaded-file_";

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

    /**
     * {@link MultipartFile MultipartFile} to transfer to a system temporary file.
     *
     * @param uploadedFile the file to transfer
     * @return {@link InputStreamSource} providing the input stream with file contents. The temporary file will be
     *     automatically deleted when this object is garbage collected.
     * @see ConsumableInputStreamSource
     */
    private static InputStreamSource transferUploadedFile(MultipartFile uploadedFile) {
        try {
            File tempFile = Files.createTempFile(UPLOAD_TEMP_FILE_PREFIX, null).toFile();
            uploadedFile.transferTo(tempFile);
            return new ConsumableInputStreamSource(tempFile);
        } catch (IOException e) {
            throw new OntopusException(e); // TODO exception
        }
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

    /**
     * Finds all reusable files inside {@code jsonData}
     *
     * @param jsonData the data to search
     * @return the list
     */
    private List<FormFileRequest> findReusableFiles(Iterator<JsonNode> jsonData) {
        ArrayList<FormFileRequest> reusableFiles = new ArrayList<>();
        findReusableFiles(jsonData, reusableFiles);
        reusableFiles.trimToSize();
        return reusableFiles;
    }

    private void findReusableFiles(Iterator<JsonNode> iterator, List<FormFileRequest> reusableFiles) {
        while (iterator.hasNext()) {
            final JsonNode node = iterator.next();
            if (FormFileRequest.matches(node)) {
                reusableFiles.add(objectMapper.convertValue(node, FormFileRequest.class));
            } else {
                findReusableFiles(node.iterator(), reusableFiles);
            }
        }
    }

    public Future<JsonForm> getCurrentJsonForm() {
        return mediator.getCurrentForm(); // TODO include form data when publishing new version of
    }

    public void initializeImport(@Nullable VersionSeriesURI uri) {
        mediator.initialize(uri);
    }

    private Map<FormFileRequest, InputStreamSource> resolveCombinedFiles(
            ImportProcessContextRequest contextRequest, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> result = new HashMap<>(files.size());
        contextRequest.getServiceToReusableFormResultMap().forEach((serviceId, formResult) -> {
            Iterator<JsonNode> jsonDataIterator =
                    formResult.values().stream().map(objectMapper::readTree).iterator();
            result.putAll(resolveFiles(jsonDataIterator, files));
        });
        return result;
    }

    /**
     * Finds reusable files in the jsonData
     *
     * @param files
     */
    private Map<FormFileRequest, InputStreamSource> resolveFiles(
            Iterator<JsonNode> jsonData, MultiValueMap<String, MultipartFile> files) {
        final List<FormFileRequest> reusableFiles = findReusableFiles(jsonData); // TODO rename from reusable files
        validator.validateObject(reusableFiles).failOnError(ValidationException::new);
        final Map<FormFileRequest, InputStreamSource> result = new HashMap<>(reusableFiles.size());

        for (final FormFileRequest fileRequest : reusableFiles) {
            MultipartFile multipartFile =
                    findUploadedFile(files, fileRequest.getFormFieldName(), fileRequest.getFileName());

            InputStreamSource streamSource = transferUploadedFile(multipartFile);

            Objects.requireNonNull(streamSource);
            result.put(fileRequest, streamSource);
        }
        return result;
    }

    public Future<?> submitCombinedData(
            ImportProcessContextRequest context, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> reusableFiles = resolveCombinedFiles(context, files);
        // return mediator.submitCombinedFormResult(context, reusableFiles);
        // TODO implement submitting combined data
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Future<?> submitData(Map<String, JsonNode> jsonData, MultiValueMap<String, MultipartFile> files) {
        Map<FormFileRequest, InputStreamSource> reusableFiles =
                resolveFiles(jsonData.values().iterator(), files);
        return mediator.submitFormResult(jsonData, reusableFiles);
    }
}
