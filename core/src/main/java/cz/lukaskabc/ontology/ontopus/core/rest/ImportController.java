package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.api.model.StagedJsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyImporter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriUtils;

@Log4j2
@RestController
@RequestMapping(path = ImportController.PREFIX)
public class ImportController {
    static final String PREFIX = "/import";
    private static final String IMPORT_FORM_SUBMIT_ENDPOINT = PREFIX + "/source";
    private final List<OntologyImporter> ontologyImporters;

    @Autowired
    public ImportController(List<OntologyImporter> ontologyImporters) {
        this.ontologyImporters = Collections.unmodifiableList(ontologyImporters);
        long uniqueImporters = ontologyImporters.stream()
                .map(OntologyImporter::getSourceName)
                .distinct()
                .count();
        if (uniqueImporters != ontologyImporters.size()) {
            throw new IllegalStateException("Ontology importer name duplicated"); // TODO
        }
    }

    @GetMapping("/source/{source}/form")
    public StagedJsonForm getOntologyImportFormScheme(@PathVariable("source") String importName) {
        for (OntologyImporter ontologyImporter : ontologyImporters) {
            if (importName.equals(ontologyImporter.getSourceName())) {
                return new StagedJsonForm(
                        ontologyImporter.getImportFormSchema(),
                        ontologyImporter.getImportFormUiSchema(),
                        IMPORT_FORM_SUBMIT_ENDPOINT + "/" + UriUtils.encodePath(importName, StandardCharsets.UTF_8),
                        ontologyImporter.getNextImportFormPath());
            }
        }

        throw new IllegalStateException("Ontology importer name not found"); // TODO
    }

    @GetMapping(value = "/source")
    public List<String> getOntologyImportSources() {
        List<String> names = new ArrayList<>(ontologyImporters.size());
        for (OntologyImporter ontologyImporter : ontologyImporters) {
            names.add(ontologyImporter.getSourceName());
        }
        return names;
    }

    private OntologyImporter resolveImporter(String importName) {
        for (OntologyImporter ontologyImporter : ontologyImporters) {
            if (importName.equals(ontologyImporter.getSourceName())) {
                return ontologyImporter;
            }
        }
        throw new IllegalStateException("Ontology importer name not found"); // TODO
    }

    @Async
    @PostMapping(value = "/source/{importName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void startImport(@PathVariable("importName") String importName, MultipartHttpServletRequest request) {
        log.info("Starting import from {}", importName);

        final OntologyImporter importer = resolveImporter(importName);
        importer.importOntology(request.getParameterMap(), request.getFileMap());
    }
}
