package cz.lukaskabc.ontology.ontopus.api.event;

import cz.lukaskabc.ontology.ontopus.api.model.Ontology;
import org.springframework.context.ApplicationEvent;

/** Indicates that a new ontology was imported into the database and is ready for further processing. */
public class OntologyImportFinished extends ApplicationEvent {
    private final Ontology ontology;

    public OntologyImportFinished(Ontology importedOntology, Object source) {
        super(source);
        this.ontology = importedOntology;
    }

    public Ontology getOntology() {
        return ontology;
    }
}
