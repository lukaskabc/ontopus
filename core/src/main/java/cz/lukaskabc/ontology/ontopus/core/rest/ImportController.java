package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.factory.ImportProcessContextHolder;
import cz.lukaskabc.ontology.ontopus.core.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core.service.ImportProcessMediator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping(path = "/import")
public class ImportController {
    private static final Logger log = LogManager.getLogger(ImportController.class);
    private final ImportProcessContextHolder contextHolder;
    private final ImportFinalizingService importFinalizingService;
    private final ImportProcessMediator mediator;

    public ImportController(
            ImportProcessContextHolder contextHolder,
            ImportFinalizingService importFinalizingService,
            ImportProcessMediator mediator) {
        this.contextHolder = contextHolder;
        this.importFinalizingService = importFinalizingService;
        this.mediator = mediator;
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

    @PostMapping(path = "combined")
    public void onCombinedFormSubmit(MultipartHttpServletRequest request) {}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void onFormSubmit(MultipartHttpServletRequest request) {
        // TODO: this stuff should be in a service
        // withLock(() -> {
        // if (context().getPendingServicesStack().isEmpty()) {
        // return; // TODO error
        // }
        // context().handleResult(new FormResult(request));
        // postFormSubmit();
        // });
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
