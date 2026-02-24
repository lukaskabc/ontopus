package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

@MappedSuperclass
public abstract class PersistenceEntityWithDcatIdentifier<I extends TypedIdentifier> extends PersistenceEntity<I> {
    @SuppressWarnings("unused,FieldCanBeLocal")
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private URI identifier;

    @Override
    protected void setUri(URI uri) {
        super.setUri(uri);
        this.identifier = uri;
    }
}
