package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyAnnotationInjectionService;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class WidocoAnnotationsInjectionService implements OntologyAnnotationInjectionService {
    private static final Map<String, @Nullable String> WIDOCO_SERIALIZATION_TO_FILE_EXTENSION_MAP =
            Map.<String, @Nullable String>of(
                    Vocabulary.s_p_ntSerialization,
                    RDFFormat.NTRIPLES.getDefaultFileExtension(),
                    Vocabulary.s_p_jsonldSerialization,
                    RDFFormat.JSONLD.getDefaultFileExtension(),
                    Vocabulary.s_p_rdfxmlSerialization,
                    RDFFormat.RDFXML.getDefaultFileExtension(),
                    Vocabulary.s_p_turtleSerialization,
                    RDFFormat.TURTLE.getDefaultFileExtension());

    private final OntopusConfig ontopusConfig;

    public WidocoAnnotationsInjectionService(OntopusConfig ontopusConfig) {
        this.ontopusConfig = ontopusConfig;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    private String getOntologyURIString(OntologyURI ontologyURI) {
        if (ontopusConfig.getResource().isNoSlashFallsBackToTrailingSlash()) {
            return StringUtils.withoutTrailingSlash(ontologyURI.toString());
        }
        return ontologyURI.toString();
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public Model handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        Set<Statement> statements = new HashSet<>(WIDOCO_SERIALIZATION_TO_FILE_EXTENSION_MAP.size());
        final OntologyURI ontologyURI = context.getVersionSeries().getOntologyURI();
        final String ontologyURIString = getOntologyURIString(ontologyURI);

        final ValueFactory vf = SimpleValueFactory.getInstance();
        final IRI subject = vf.createIRI(ontologyURI.toString());
        final IRI graph = vf.createIRI(context.getTemporaryDatabaseContext().toString());

        WIDOCO_SERIALIZATION_TO_FILE_EXTENSION_MAP.forEach((serializationPredicate, fileExtension) -> {
            if (fileExtension != null) {
                final IRI predicate = vf.createIRI(serializationPredicate);
                final IRI object = vf.createIRI(ontologyURIString + "." + fileExtension);
                statements.add(vf.createStatement(subject, predicate, object, graph));
            }
        });

        return new LinkedHashModel(statements);
    }
}
