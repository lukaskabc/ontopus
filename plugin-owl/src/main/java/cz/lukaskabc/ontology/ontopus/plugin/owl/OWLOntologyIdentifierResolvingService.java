package cz.lukaskabc.ontology.ontopus.plugin.owl;

import cz.cvut.kbss.jopa.exceptions.OWLPersistenceException;
import cz.cvut.kbss.jopa.vocabulary.OWL;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyIdentifierResolvingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OWLOntologyIdentifierResolvingService implements OntologyIdentifierResolvingService {
    static final URI OWL_ONTOLOGY = URI.create(OWL.ONTOLOGY);
    private final GraphService graphService;

    public OWLOntologyIdentifierResolvingService(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public Set<URI> resolve(TemporaryContextURI databaseContext) {
        try {
            return graphService
                    .findAllSubjectsOfType(OWL_ONTOLOGY, databaseContext)
                    .collect(Collectors.toSet());
        } catch (OWLPersistenceException e) {
            return Set.of();
        }
    }
}
