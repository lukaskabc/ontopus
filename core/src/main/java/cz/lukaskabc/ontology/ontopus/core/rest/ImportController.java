package cz.lukaskabc.ontology.ontopus.core.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.service.ImportProcessMediator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@RestController
@RequestMapping(path = "/import")
public class ImportController {
    private static final Logger log = LogManager.getLogger(ImportController.class);
    private final ImportProcessMediator mediator;
    private final StandardServletMultipartResolver multipartResolver;
    private final ObjectMapper objectMapper;

    public ImportController(
            ImportProcessMediator mediator,
            StandardServletMultipartResolver multipartResolver,
            ObjectMapper objectMapper) {
        this.mediator = mediator;
        this.multipartResolver = multipartResolver;
        this.objectMapper = objectMapper;
    }

    private <T> ResponseEntity<T> handleFuture(Future<T> future) throws ExecutionException, InterruptedException {
        return switch (future.state()) {
            case SUCCESS -> {
                T value = future.get();
                if (value != null) {
                    yield ResponseEntity.ok(value);
                }
                yield ResponseEntity.noContent().build();
            }
            case FAILED -> ResponseEntity.internalServerError().build();
            case RUNNING, CANCELLED ->
                ResponseEntity.status(HttpStatus.CONFLICT).build();
        };
    }

    @GetMapping
    public ResponseEntity<JsonForm> getJsonForm() throws ExecutionException, InterruptedException {
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
     * // TODO replace with open api docs Expects several JSONs with form data separated into parts, each for a single
     * service in the pipeline.
     *
     * <p>// TODO what if two provided files have matching name? but the name is the name of the parameter, not the file
     * // so what if two parameters from different services matches? the name could be prefixed with full service class
     * // name
     *
     * @param request
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping(path = "combined", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> onCombinedFormSubmit(MultipartHttpServletRequest request)
            throws ExecutionException, InterruptedException {

        Map<String, JsonNode> combinedData = parseData(request);
        MultiValueMap<String, MultipartFile> files = request.getMultiFileMap();

        return handleFuture(mediator.submitCombinedFormResult(new FormResult(combinedData, files)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> onFormSubmit(
            @RequestParam Map<String, JsonNode> data,
            @RequestParam(required = false) MultiValueMap<String, MultipartFile> files)
            throws ExecutionException, InterruptedException {
        return handleFuture(mediator.submitFormResult(new FormResult(data, files)));
    }

    private void postFormSubmit() {
        // if (!context().getPendingServicesStack().isEmpty()) {
        // return;
        // }
        // // no more pending services on stack
        // importFinalizingService.finalize(context());
        // contextHolder.resetSessionImportProcess();
    }

    /*
     * private final List<OntologyImporter> ontologyImporters;
     *
     * @Autowired public ImportController(List<OntologyImporter> ontologyImporters)
     * { this.ontologyImporters = Collections.unmodifiableList(ontologyImporters);
     * long uniqueImporters = ontologyImporters.stream()
     * .map(OntologyImporter::getSourceName) .distinct() .count(); if
     * (uniqueImporters != ontologyImporters.size()) { throw new
     * IllegalStateException("Ontology importer name duplicated"); // TODO } }
     *
     * @GetMapping("/source/{importSource}") public StagedJsonForm
     * getOntologyImportForm(@PathVariable("importSource") String importSource) {
     * for (OntologyImporter ontologyImporter : ontologyImporters) { if
     * (importSource.equals(ontologyImporter.getSourceName())) { return new
     * StagedJsonForm( ontologyImporter.getImportFormSchema(),
     * ontologyImporter.getImportFormUiSchema(), IMPORT_FORM_SUBMIT_ENDPOINT + "/" +
     * UriUtils.encodePath(importSource, StandardCharsets.UTF_8),
     * ontologyImporter.getNextImportFormPath()); } }
     *
     * throw new IllegalStateException("Ontology importer name not found"); // TODO
     * }
     *
     * @GetMapping(value = "/source") public List<String> getOntologyImportSources()
     * { List<String> names = new ArrayList<>(ontologyImporters.size()); for
     * (OntologyImporter ontologyImporter : ontologyImporters) {
     * names.add(ontologyImporter.getSourceName()); } return names; }
     *
     * private OntologyImporter resolveImporter(String importSource) { for
     * (OntologyImporter ontologyImporter : ontologyImporters) { if
     * (importSource.equals(ontologyImporter.getSourceName())) { return
     * ontologyImporter; } } throw new
     * IllegalStateException("Ontology importer name not found"); // TODO }
     *
     * @PostMapping(value = "/source/{importSource}", consumes =
     * MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<String>
     * startImport(
     *
     * @PathVariable("importSource") String importSource,
     * MultipartHttpServletRequest request) { log.info("Starting import from {}",
     * importSource);
     *
     * final OntologyImporter importer = resolveImporter(importSource); final String
     * nextUrl = importer.importOntology(request.getParameterMap(),
     * request.getFileMap());
     *
     * final ResponseEntity.BodyBuilder builder = ResponseEntity.accepted(); if
     * (nextUrl != null) { builder.header("ONTOPUS-Next-Form-Location", nextUrl); //
     * TODO use constant }
     *
     * return builder.build(); }
     */
}
