package cz.lukaskabc.ontology.ontopus.core.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.StagedJsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyImporter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(path = ImportController.PREFIX)
public class ImportController {
    static final String PREFIX = "/import";
    private static final String IMPORT_FORM_SUBMIT_ENDPOINT = PREFIX + "/source";
    private final List<OntologyImporter<?>> ontologyImporters;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Autowired
    public ImportController(
            List<OntologyImporter<?>> ontologyImporters, ObjectMapper objectMapper, Validator validator) {
        this.ontologyImporters = Collections.unmodifiableList(ontologyImporters);
        this.objectMapper = objectMapper;
        this.validator = validator;
        long uniqueImporters = ontologyImporters.stream()
                .map(OntologyImporter::getSourceName)
                .distinct()
                .count();
        if (uniqueImporters != ontologyImporters.size()) {
            throw new IllegalStateException("Ontology importer name duplicated"); // TODO
        }
    }

    @GetMapping("/source/form")
    public StagedJsonForm getOntologyImportFormScheme(@RequestParam("source") String importName) {
        for (OntologyImporter<?> ontologyImporter : ontologyImporters) {
            if (importName.equals(ontologyImporter.getSourceName())) {
                return new StagedJsonForm(
                        ontologyImporter.getImportFormSchema(),
                        ontologyImporter.getImportFormUiSchema(),
                        IMPORT_FORM_SUBMIT_ENDPOINT,
                        ontologyImporter.getNextImportFormPath());
            }
        }

        throw new IllegalStateException("Ontology importer name not found"); // TODO
    }

    @GetMapping(value = "/source")
    public List<String> getOntologyImportSources() {
        List<String> names = new ArrayList<>(ontologyImporters.size());
        for (OntologyImporter<?> ontologyImporter : ontologyImporters) {
            names.add(ontologyImporter.getSourceName());
        }
        return names;
    }

    @SuppressWarnings("unchecked")
    private <D> OntologyImporter<D> resolveImporter(String importName) {
        for (OntologyImporter<?> ontologyImporter : ontologyImporters) {
            if (importName.equals(ontologyImporter.getSourceName())) {
                return (OntologyImporter<D>) ontologyImporter;
            }
        }
        throw new IllegalStateException("Ontology importer name not found"); // TODO
    }

    @Async
    @PostMapping(value = "/source", consumes = MediaType.APPLICATION_JSON_VALUE)
    public <D> void startImport(@RequestParam("source") String importName, @RequestBody JsonNode body)
            throws IOException {
        log.info("Starting import from {}", importName);
        final OntologyImporter<D> importer = resolveImporter(importName);
        final D formResult = objectMapper.treeToValue(body, importer.getImportFormDataClass());
        validator.validateObject(formResult).failOnError(RuntimeException::new); // TODO
        importer.importOntology(formResult);
    }
}
