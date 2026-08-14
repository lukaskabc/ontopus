package cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.repository.OntologyRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OntologyService {
    private final OntologyRepository ontologyRepository;

    public OntologyService(OntologyRepository ontologyRepository) {
        this.ontologyRepository = ontologyRepository;
    }

    public Set<String> findValue(GraphURI graphURI, OntologyURI subject, ResourceURI predicate) {
        return ontologyRepository.findValue(graphURI, subject, predicate);
    }
}
