package cz.lukaskabc.ontology.ontopus.core_model.model.dcat;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.DocumentedOWLClass;

import java.util.Set;

@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_DatasetSeries)
public abstract class DatasetSeries<
                MembersIdentifier extends TypedIdentifier,
                SeriesDistributionIdentifier extends TypedIdentifier,
                ID extends TypedIdentifier>
        extends Dataset<SeriesDistributionIdentifier, ID> {

    public abstract void addMember(MembersIdentifier member);

    public abstract MembersIdentifier getFirst();

    public abstract MembersIdentifier getLast();

    /** @return unmodifiable set of member identifiers */
    public abstract Set<MembersIdentifier> getMembers();

    public abstract boolean hasMember(MembersIdentifier member);

    public abstract void removeMember(MembersIdentifier member);

    public abstract void setFirst(MembersIdentifier first);

    public abstract void setLast(MembersIdentifier last);
}
