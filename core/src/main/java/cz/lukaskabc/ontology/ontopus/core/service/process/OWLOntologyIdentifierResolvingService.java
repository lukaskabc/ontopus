package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.cvut.kbss.jopa.exceptions.OWLPersistenceException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyIdentifierResolvingService;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class OWLOntologyIdentifierResolvingService implements OntologyIdentifierResolvingService {
    static final URI OWL_ONTOLOGY = URI.create("http://www.w3.org/2002/07/owl#Ontology");
    private final EntityManager entityManager;

    public OWLOntologyIdentifierResolvingService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private List<URI> findIdentifier(TemporaryContextURI databaseContext) {
        return entityManager
                .createNativeQuery("""
				SELECT ?identifier FROM ?context WHERE {
				    ?identifier a ?ontology .
				}
				""", URI.class)
                .setParameter("context", databaseContext.toURI())
                .setParameter("ontology", OWL_ONTOLOGY)
                .getResultList();
    }

    @Override
    public Set<URI> resolve(TemporaryContextURI databaseContext) {
        try {
            return new HashSet<>(findIdentifier(databaseContext));
        } catch (OWLPersistenceException e) {
            return Set.of();
        }
    }
}
