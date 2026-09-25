package cz.lukaskabc.ontology.ontopus.plugin.git.model;

import cz.cvut.kbss.jopa.model.annotations.EnumType;
import cz.cvut.kbss.jopa.model.annotations.Enumerated;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;

import java.util.regex.Pattern;

@MappedSuperclass
public abstract class GitWebhook<I extends TypedIdentifier> extends Webhook<I> {
    @OWLDataProperty(iri = Vocabulary.s_p_ontopus_regexPattern, simpleLiteral = true)
    private Pattern ref;

    @Enumerated(EnumType.STRING)
    @OWLDataProperty(iri = Vocabulary.s_p_sioc_about, simpleLiteral = true)
    private RefType refType;

    public Pattern getRef() {
        return ref;
    }

    public RefType getRefType() {
        return refType;
    }

    public void setRef(Pattern ref) {
        this.ref = ref;
    }

    public void setRefType(RefType refType) {
        this.refType = refType;
    }
}
