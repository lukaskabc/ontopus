package cz.lukaskabc.ontology.ontopus.core.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessTaskConflictException;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.service.ImportProcessMediator;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping(path = "/import")
public class ImportController {
    private static final Logger log = LogManager.getLogger(ImportController.class);
    private final ImportProcessMediator mediator;
    private final ObjectMapper objectMapper;

    public ImportController(ImportProcessMediator mediator, ObjectMapper objectMapper) {
        this.mediator = mediator;
        this.objectMapper = objectMapper;
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
            case FAILED -> throw future.exceptionNow();
            case RUNNING, CANCELLED -> throw new ImportProcessTaskConflictException();
        };
    }

    /**
     * Initializes a new import process. The ontology will be published as a version in existing version series when the
     * identifier is supplied. New version series are created otherwise.
     *
     * @param versionSeries The identifier of existing version series for publishing a new version.
     */
    @PostMapping("initialize")
    public void initialize(@Nullable @RequestParam(required = false) URI versionSeries) {
        VersionSeriesURI uri = null;
        if (versionSeries != null) {
            uri = new VersionSeriesURI(versionSeries);
        }
        mediator.initialize(uri);
    }

    @GetMapping
    public ResponseEntity<JsonForm> getJsonForm() throws Throwable {
        Future<JsonForm> future = mediator.getCurrentForm();
        return handleFuture(future);
    }

    private JsonNode readStringValues(String[] values) {
        if (values.length > 1) {
            ArrayNode node = objectMapper.createArrayNode();
            for (String value : values) {
                node.add(value);
            }
            return node;
        } else {
            return TextNode.valueOf(values[0]);
        }
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

    private Map<String, JsonNode> parseData(MultipartHttpServletRequest request) {
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

        return handleFuture(mediator.submitCombinedFormResult(new FormResult(combinedData, files)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> onFormSubmit(
            @RequestParam Map<String, JsonNode> data,
            @RequestParam(required = false) MultiValueMap<String, MultipartFile> files)
            throws Throwable {
        return handleFuture(mediator.submitFormResult(new FormResult(data, files)));
    }
}
