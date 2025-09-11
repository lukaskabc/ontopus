package cz.lukaskabc.ontology.ontopus.api.event;

import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import org.springframework.context.ApplicationEvent;

/** Indicates that a new ontology artifact was fully constructed and is ready for further processing. */
public class OntologyArtifactCreated extends ApplicationEvent {
    private final OntologyArtifact ontologyArtifact;

    public OntologyArtifactCreated(OntologyArtifact ontologyArtifact, Object source) {
        super(source);
        this.ontologyArtifact = ontologyArtifact;
    }

    public OntologyArtifact getOntologyArtifact() {
        return ontologyArtifact;
    }
}
