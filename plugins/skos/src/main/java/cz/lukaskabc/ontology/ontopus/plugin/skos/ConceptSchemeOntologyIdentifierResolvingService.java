package cz.lukaskabc.ontology.ontopus.plugin.skos;

import cz.cvut.kbss.jopa.vocabulary.SKOS;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyIdentifierResolvingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConceptSchemeOntologyIdentifierResolvingService implements OntologyIdentifierResolvingService {
    static final URI CONCEPT_SCHEME = URI.create(SKOS.CONCEPT_SCHEME);
    private final GraphService graphService;

    public ConceptSchemeOntologyIdentifierResolvingService(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public Set<URI> resolve(TemporaryContextURI databaseContext) {
        try {
            return graphService
                    .findAllSubjectsOfType(CONCEPT_SCHEME, databaseContext)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Set.of();
        }
    }
}
