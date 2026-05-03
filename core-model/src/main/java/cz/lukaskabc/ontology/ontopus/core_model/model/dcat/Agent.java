package cz.lukaskabc.ontology.ontopus.core_model.model.dcat;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.Types;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractGeneratedPersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.AgentURI;

import java.net.URI;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_sioc_Agent)
public class Agent extends AbstractGeneratedPersistenceEntity<AgentURI> {
    @OWLDataProperty(iri = Vocabulary.s_p_vocab_name)
    private MultilingualString name = new MultilingualString();

    @Types
    private Set<URI> types;

    public MultilingualString getName() {
        return name;
    }

    public Set<URI> getTypes() {
        return types;
    }

    public Agent setName(MultilingualString name) {
        this.name = name;
        return this;
    }

    public Agent setTypes(Set<URI> types) {
        this.types = types;
        return this;
    }

    @Override
    protected AgentURI wrapUri(URI uri) {
        return new AgentURI(uri);
    }
}
