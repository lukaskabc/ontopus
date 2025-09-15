package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.factory.ImportProcessContextHolder;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController("/import")
@RequestMapping(path = ImportController.PREFIX)
public class ImportController {
    private static final Logger log = LogManager.getLogger(ImportController.class);
    static final String PREFIX = "/import";
    private static final String IMPORT_FORM_SUBMIT_ENDPOINT = PREFIX + "/source";
    private final ImportProcessContextHolder contextHolder;

    public ImportController(ImportProcessContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    private ImportProcessContext context() {
        return contextHolder.getImportProcessContext();
    }

    @Nullable @GetMapping
    public JsonForm getJsonForm() {
        return withLock(() -> {
            if (context().getPendingServicesStack().isEmpty()) {
                return null; // TODO: null? change to some HTTP repsonse code?
            }
            return context().peekService().getJsonForm();
        });
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void onFormSubmit(MultipartHttpServletRequest request) {
        withLock(() -> {
            if (context().getPendingServicesStack().isEmpty()) {
                return; // TODO error
            }
            context().handleResult(new FormResult(request));
        });
    }

    private void withLock(Runnable lambda) {
        contextHolder.getLock().lock();
        try {
            lambda.run();
        } finally {
            contextHolder.getLock().unlock();
        }
    }

    private <T> T withLock(Supplier<T> lambda) {
        contextHolder.getLock().lock();
        try {
            return lambda.get();
        } finally {
            contextHolder.getLock().unlock();
        }
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
