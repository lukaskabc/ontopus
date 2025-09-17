package cz.lukaskabc.ontology.ontopus.core.model.dcat;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core.model.util.DocumentedOWLClass;
import java.net.URI;
import java.util.Set;

@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_DatasetSeries)
public abstract class DatasetSeries<ID extends EntityIdentifier> extends Dataset<ID> {

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_last)
    private URI last;

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_first)
    private URI first;

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_seriesMember)
    private Set<URI> members;

    public URI getFirst() {
        return first;
    }

    public URI getLast() {
        return last;
    }

    public Set<URI> getMembers() {
        return members;
    }

    public void setFirst(URI first) {
        this.first = first;
    }

    public void setLast(URI last) {
        this.last = last;
    }

    public void setMembers(Set<URI> members) {
        this.members = members;
    }
}
