package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.Objects;

/** Uses constant version for the ontology {@code "latest"}, use the ontology URI for the version IRI */
@Service
public class NonVersioningService implements OntologyVersioningService {
    private static final String TRANSLATION_ROOT =
            "ontopus.core.service.OntologyVersioningService.NonVersioningService";

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".title";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        context.getVersionArtifact().setVersion("latest");
        OntologyURI ontologyURI = context.getVersionSeries().getOntologyURI();
        Objects.requireNonNull(ontologyURI, "Ontology URI must not be null");
        context.getVersionArtifact().setVersionUri(new OntologyVersionURI(ontologyURI.toURI()));
        return null;
    }
}
