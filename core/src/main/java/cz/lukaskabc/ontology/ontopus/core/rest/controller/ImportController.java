package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tools.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.concurrent.Future;

@NullMarked
@Tag(name = "Import", description = "Endpoints for importing new ontologies and their versions")
@RestController
@RequestMapping(path = "/import")
public class ImportController extends AbstractJsonController {

    private final ImportService importService;

    public ImportController(ImportService importService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.importService = importService;
    }

    @GetMapping
    public ResponseEntity<JsonForm> getJsonForm() throws Throwable {
        Future<@Nullable JsonForm> futureForm = importService.getCurrentJsonForm();
        return ImportProcessMediatorFutureHandler.handleFuture(futureForm);
    }

    /**
     * Initializes a new import process. The ontology will be published as a version in existing version series when the
     * identifier is supplied. New version series are created otherwise.
     *
     * @param versionSeries The identifier of existing version series for publishing a new version.
     */
    @Operation(
            summary = "Initialize new import process",
            description = "Initialize a new import process for the given version series. "
                    + "If the version series is not specified, a new one will be created.")
    @PostMapping("initialize")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void initialize(@Nullable @RequestParam(required = false, name = "series") URI versionSeries) {
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
    @PostMapping(path = "combined", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> onCombinedFormSubmit(
            @Parameter(description = "Import context") @RequestParam(name = "context") @Valid ImportProcessContextRequest contextRequest)
            throws Throwable {
        initialize(contextRequest.getVersionSeriesURI().toURI());

        return ImportProcessMediatorFutureHandler.handleFuture(importService.submitCombinedData(contextRequest));
    }

    @Operation(
            summary = "Submit data from the JSON form",
            description = "Submit the data from the dynamic JSON form. "
                    + "The data are expected to be sent as multipart form data, where the parameters "
                    + "containing JSON data should have their content type set to application/json. "
                    + "The files should be sent as file parameters in the same multipart request.")
    @ApiResponse(responseCode = "202", description = "Import started successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> onFormSubmit(MultipartHttpServletRequest request) throws Throwable {
        FormJsonDataDto jsonData = parseData(request);
        MultiValueMap<String, MultipartFile> files = request.getMultiFileMap();
        // TODO how to pass async error back to the FE?
        return ImportProcessMediatorFutureHandler.handleFuture(importService.submitData(jsonData, files));
    }
}
