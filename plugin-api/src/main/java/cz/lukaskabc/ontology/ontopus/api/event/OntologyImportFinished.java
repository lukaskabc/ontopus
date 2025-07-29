package cz.lukaskabc.ontology.ontopus.api.event;

import cz.lukaskabc.ontology.ontopus.api.model.Ontology;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/** Indicates that a new ontology was imported into the database and is ready for further processing. */
@Getter
public class OntologyImportFinished extends ApplicationEvent {
    private final Ontology ontology;

    public OntologyImportFinished(Ontology importedOntology, Object source) {
        super(source);
        this.ontology = importedOntology;
    }
}
