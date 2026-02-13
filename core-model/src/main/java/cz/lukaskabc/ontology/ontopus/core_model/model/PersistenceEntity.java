package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

/** A persistence (JOPA) entity with an identifier - {@link #uri} */
@MappedSuperclass
public abstract class PersistenceEntity<ID extends TypedIdentifier> {
    @Id
    @NotNull private URI uri;

    @SuppressWarnings("unused,FieldCanBeLocal")
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private URI identifier;

    public ID getIdentifier() {
        if (getUri() == null) {
            return null;
        }
        return wrapUri(getUri());
    }

    protected URI getUri() {
        return uri;
    }

    public void setIdentifier(ID identifier) {
        setUri(identifier.toURI());
    }

    protected void setUri(URI uri) {
        this.uri = uri;
        this.identifier = uri;
    }

    protected abstract ID wrapUri(URI uri);
}
