package cz.lukaskabc.ontology.ontopus.core_model.model.dcat;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.DocumentedOWLClass;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_Catalog)
public abstract class Catalog<ID extends TypedIdentifier> extends Resource<ID> {
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_homepage)
    private URI homepage;

    @OWLObjectProperty(iri = Vocabulary.s_i_dcat_publisher, cascade = CascadeType.ALL)
    private Agent publisher;

    public URI getHomepage() {
        return homepage;
    }

    public Agent getPublisher() {
        return publisher;
    }

    public void setHomepage(URI homepage) {
        this.homepage = homepage;
    }

    public void setPublisher(Agent publisher) {
        this.publisher = publisher;
    }
}
