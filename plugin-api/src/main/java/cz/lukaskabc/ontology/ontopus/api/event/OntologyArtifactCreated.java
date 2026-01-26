package cz.lukaskabc.ontology.ontopus.api.event;

import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact;
import org.springframework.context.ApplicationEvent;

/** Indicates that a new ontology artifact was fully constructed and is ready for further processing. */
public class OntologyArtifactCreated extends ApplicationEvent {
    private final VersionArtifact ontologyArtifact;

    public OntologyArtifactCreated(VersionArtifact ontologyArtifact, Object source) {
        super(source);
        this.ontologyArtifact = ontologyArtifact;
    }

    public VersionArtifact getOntologyArtifact() {
        return ontologyArtifact;
    }
}
