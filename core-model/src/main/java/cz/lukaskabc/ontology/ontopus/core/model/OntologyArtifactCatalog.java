package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import java.util.List;

/** A collection of {@link OntologyArtifact}s available on the OntoPuS instance */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifactCatalog)
public class OntologyArtifactCatalog extends PersistenceEntity {

    @OWLObjectProperty(iri = Vocabulary.s_p_containsOntologyArtifact, fetch = FetchType.LAZY)
    private List<OntologyArtifact> ontologyArtifacts;
}
