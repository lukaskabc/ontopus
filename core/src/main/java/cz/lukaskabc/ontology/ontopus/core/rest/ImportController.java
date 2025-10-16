package cz.lukaskabc.ontology.ontopus.core.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessTaskConflictException;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.service.ImportService;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping(path = "/import")
public class ImportController {
    private final ImportService importService;
    private final ObjectMapper objectMapper;

    public ImportController(ImportService importService, ObjectMapper objectMapper) {
        this.importService = importService;
        this.objectMapper = objectMapper;
    }

    /**
     * Initializes a new import process. The ontology will be published as a version in existing version series when the
     * identifier is supplied. New version series are created otherwise.
     *
     * @param versionSeries The identifier of existing version series for publishing a new version.
     */
    @PostMapping("initialize")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void initialize(@Nullable @RequestParam(required = false, name = "versionSeries") URI versionSeries) {
        VersionSeriesURI uri = null;
        if (versionSeries != null) {
            uri = new VersionSeriesURI(versionSeries);
        }
        importService.initializeImport(uri);
    }

    @GetMapping
    public ResponseEntity<JsonForm> getJsonForm() throws Throwable {
        // TODO test and setup LOG4J2 logging
        return handleFuture(importService.getCurrentJsonForm());
    }

    /**
     * // TODO replace with open api docs
     *
     * <p>Expects several JSONs with form data separated into parts, each for a single service in the pipeline.
     *
     * @param request
     */
    @PostMapping(path = "combined", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> onCombinedFormSubmit(MultipartHttpServletRequest request) throws Throwable {
        Map<String, JsonNode> combinedData = parseData(request);
        MultiValueMap<String, MultipartFile> files = request.getMultiFileMap();
        return handleFuture(importService.submitCombinedData(combinedData, files));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> onFormSubmit(MultipartHttpServletRequest request) throws Throwable {
        Map<String, JsonNode> jsonData = parseData(request);
        MultiValueMap<String, MultipartFile> files = request.getMultiFileMap();
        // TODO how to pass async error back to the FE?
        return handleFuture(importService.submitData(jsonData, files));
    }

    /**
     * Immediately returns a response entity based on the current future status.
     *
     * @param future the future to handle
     * @return Response entity based on the future status.
     * @param <T> The type of the result value
     */
    private <T> ResponseEntity<T> handleFuture(Future<T> future) throws Throwable {
        return switch (future.state()) {
            case SUCCESS -> {
                T value = future.resultNow();
                if (value != null) {
                    yield ResponseEntity.ok(value);
                }
                yield ResponseEntity.noContent().build();
            }
            case RUNNING -> ResponseEntity.accepted().build();
            case FAILED -> throw future.exceptionNow();
            case CANCELLED -> throw new ImportProcessTaskConflictException();
        };
    }

    private Map<String, JsonNode> parseData(MultipartHttpServletRequest request) throws JsonProcessingException {
        Map<String, JsonNode> result = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (entry.getValue().length == 0) {
                continue;
            }
            if (MediaType.APPLICATION_JSON_VALUE.equals(request.getMultipartContentType(entry.getKey()))) {
                result.put(entry.getKey(), readJsonValues(entry.getValue()));
            } else {
                result.put(entry.getKey(), readStringValues(entry.getValue()));
            }
        }
        return result;
    }

    private JsonNode readJsonValues(String[] values) {
        try {
            if (values.length > 1) {
                ArrayNode node = objectMapper.createArrayNode();

                for (String value : values) {
                    node.add(objectMapper.readTree(value));
                }
                return node;
            } else {
                return objectMapper.readTree(values[0]);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode readStringValues(String[] values) throws JsonProcessingException {
        if (values.length > 1) {
            ArrayNode node = objectMapper.createArrayNode();
            for (String value : values) {
                node.add(value);
            }
            return node;
        } else {
            return objectMapper.readTree(values[0]);
        }
    }
}
