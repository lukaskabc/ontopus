package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core.rest.request.FormFileRequest;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/** Utility component capable of resolving uploaded files for {@link FormFileRequest} */
@NullMarked
@Component
public class RequestFileResolver {
    /** The prefix of temporary files created for uploaded files. */
    static final String UPLOAD_TEMP_FILE_PREFIX = "ontopus-uploaded-file_";
    /**
     * Transfers the provided uploaded file to a temporary file and returns an {@link InputStreamSource} for it. The
     * temporary file will be automatically deleted when the returned {@link InputStreamSource} is garbage collected.
     *
     * @param uploadedFile the uploaded file to transfer
     * @return {@link InputStreamSource} providing the input stream with file contents.
     * @see ConsumableInputStreamSource
     */
    public static InputStreamSource transferToTempFile(MultipartFile uploadedFile) {
        Objects.requireNonNull(uploadedFile, "Uploaded file must not be null");
        try {
            File tempFile = Files.createTempFile(UPLOAD_TEMP_FILE_PREFIX, null).toFile();
            uploadedFile.transferTo(tempFile);
            return new ConsumableInputStreamSource(tempFile);
        } catch (IOException e) {
            throw InternalException.builder()
                    .errorType(Vocabulary.u_i_file_processing)
                    .internalMessage("Failed to transfer multipart file to a new temporary file")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .cause(e)
                    .build();
        }
    }

    private final Validator validator;

    private final ObjectMapper objectMapper;

    public RequestFileResolver(Validator validator, ObjectMapper objectMapper) {
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    /**
     * Finds all {@link FormFileRequest} instances in the provided JSON data, including nested objects.
     *
     * @param jsonNodeIterator Iterator over JSON nodes to search for {@link FormFileRequest} instances.
     * @return List of found {@link FormFileRequest} instances.
     */
    private List<FormFileRequest> findFileRequests(Iterator<JsonNode> jsonNodeIterator) {
        ArrayList<FormFileRequest> reusableFiles = new ArrayList<>();
        findFileRequests(jsonNodeIterator, reusableFiles);
        reusableFiles.trimToSize();
        return Collections.unmodifiableList(reusableFiles);
    }

    /**
     * Recursively searches for {@link FormFileRequest} instances in the provided JSON nodes and adds them to the list.
     *
     * @param iterator Iterator over JSON nodes to search for {@link FormFileRequest} instances.
     * @param fileRequests List to collect found {@link FormFileRequest} instances.
     */
    private void findFileRequests(Iterator<JsonNode> iterator, List<FormFileRequest> fileRequests) {
        while (iterator.hasNext()) {
            final JsonNode node = iterator.next();
            if (FormFileRequest.matches(node)) {
                fileRequests.add(objectMapper.convertValue(node, FormFileRequest.class));
            } else {
                findFileRequests(node.iterator(), fileRequests);
            }
        }
    }

    /**
     * Finds the uploaded file corresponding to the provided {@link FormFileRequest} in the uploaded files map.
     *
     * @param files Map of uploaded files, the key is the form field name, the value is a list of uploaded files for
     *     that field.
     * @param formFileRequest The {@link FormFileRequest} to search for
     * @return The {@link MultipartFile} corresponding to the provided {@link FormFileRequest}.
     * @throws IllegalStateException if no matching file is found in the uploaded files map.
     */
    private MultipartFile findUploadedFile(
            MultiValueMap<String, MultipartFile> files, FormFileRequest formFileRequest) {
        final String formFieldName = formFileRequest.getFormFieldName();
        final String fileName = formFileRequest.getFileName();
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
     * Resolves files from the request data. Resolved files are copied to temporary files and provided as
     * {@link InputStreamSource} to allow their asynchronous processing. The temporary files will be automatically
     * deleted when the corresponding {@link InputStreamSource} instances are garbage collected.
     *
     * @param jsonData JSON objects that may contain {@link FormFileRequest}
     * @param files Uploaded files from the request, mapped by form field name.
     * @return Mapping of {@link FormFileRequest} to {@link InputStreamSource} for the corresponding uploaded files.
     */
    public Map<FormFileRequest, InputStreamSource> resolveAndCopyFiles(
            Iterator<JsonNode> jsonData, MultiValueMap<String, MultipartFile> files) {
        final List<FormFileRequest> reusableFiles = findFileRequests(jsonData);
        validator.validateObject(reusableFiles).failOnError(ValidationException::fromValidationError);
        final Map<FormFileRequest, InputStreamSource> result = new HashMap<>(reusableFiles.size());

        for (final FormFileRequest fileRequest : reusableFiles) {
            MultipartFile multipartFile = findUploadedFile(files, fileRequest);
            InputStreamSource streamSource = transferToTempFile(multipartFile);

            Objects.requireNonNull(streamSource);
            result.put(fileRequest, streamSource);
        }
        return result;
    }
}
