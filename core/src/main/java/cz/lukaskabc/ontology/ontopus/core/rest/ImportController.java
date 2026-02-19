package cz.lukaskabc.ontology.ontopus.core.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.rest.request.ImportProcessContextRequest;
import cz.lukaskabc.ontology.ontopus.core.service.ImportService;
import cz.lukaskabc.ontology.ontopus.core.util.ImportProcessMediatorFutureHandler;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Import", description = "Endpoints for importing new ontologies and their versions")
@RestController
@RequestMapping(path = "/import")
public class ImportController {

    private final ImportService importService;

    private final ObjectMapper objectMapper;

    public ImportController(ImportService importService, ObjectMapper objectMapper) {
        this.importService = importService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<JsonForm> getJsonForm() throws Throwable {
        // TODO test and setup LOG4J2 logging
        return ImportProcessMediatorFutureHandler.handleFuture(importService.getCurrentJsonForm());
    }

    /**
     * Initializes a new import process. The ontology will be published as a version in existing version series when the
     * identifier is supplied. New version series are created otherwise.
     *
     * @param versionSeries The identifier of existing version series for publishing a new version.
     */
    @Operation(
            summary = "Initialize new import process",
            description =
                    "Initialize a new import process for the given version series. If the version series is not specified, a new one will be created.")
    @PostMapping("initialize")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void initialize(@Nullable @RequestParam(required = false, name = "versionSeries") URI versionSeries) {
        VersionSeriesURI uri = null;
        if (versionSeries != null) {
            uri = new VersionSeriesURI(versionSeries);
        }
        importService.initializeImport(uri);
    }

    /**
     * Consumes a single {@code context} parameter, which is expected to be a JSON of
     * {@link ImportProcessContextRequest} and files as multipart form data.
     */
    @Operation(
            summary = "Submit combined import data",
            description = "Performs the whole import process asynchronously from a single request.")
    @ApiResponse(responseCode = "202", description = "Import started successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    @PostMapping(
            path = "combined",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> onCombinedFormSubmit(
            @Parameter(description = "Import context") @RequestParam(name = "context")
                    ImportProcessContextRequest contextRequest,
            MultipartHttpServletRequest request)
            throws Throwable {
        MultiValueMap<String, MultipartFile> files = request.getMultiFileMap();
        initialize(contextRequest.getVersionSeriesURI().toURI());
        return ImportProcessMediatorFutureHandler.handleFuture(importService.submitCombinedData(contextRequest, files));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> onFormSubmit(MultipartHttpServletRequest request) throws Throwable {
        Map<String, JsonNode> jsonData = parseData(request);
        MultiValueMap<String, MultipartFile> files = request.getMultiFileMap();
        // TODO how to pass async error back to the FE?
        return ImportProcessMediatorFutureHandler.handleFuture(importService.submitData(jsonData, files));
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
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode readStringValues(String[] values) {
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
