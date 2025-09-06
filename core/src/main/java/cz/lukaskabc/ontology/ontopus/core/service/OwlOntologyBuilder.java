package cz.lukaskabc.ontology.ontopus.core.service;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.Ontology;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyBuilder;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class OwlOntologyBuilder implements OntologyBuilder {
    private final EntityManager entityManager;

    public OwlOntologyBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void build(Ontology partialOntology, URI databaseContext) {
        if (partialOntology.getOntologyUri().equals(databaseContext)) {}
    }

    public void resolveOntologyIRI(Ontology partialOntology, URI databaseContext) {}
}
