package cz.lukaskabc.ontology.ontopus.core_model.model.ontology;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import org.eclipse.rdf4j.model.Namespace;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

@OWLClass(iri = Vocabulary.s_c_PrefixDeclaration)
public class PrefixDeclaration extends Rdf4JAbstractNamespace {
    @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_prefix, simpleLiteral = true)
    private String prefix;

    @NotNull @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_namespace)
    private URI namespace;

    public PrefixDeclaration() {}

    public PrefixDeclaration(Namespace namespace) {
        this.prefix = namespace.getPrefix();
        this.namespace = URI.create(namespace.getName());
    }

    /**
     * Gets the name of the current namespace (i.e. its IRI).
     *
     * @return name of namespace
     */
    @Override
    public String getName() {
        return namespace.toString();
    }

    public OntologyURI getNamespace() {
        return new OntologyURI(namespace);
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    public void setNamespace(OntologyURI namespace) {
        this.namespace = namespace.toURI();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
