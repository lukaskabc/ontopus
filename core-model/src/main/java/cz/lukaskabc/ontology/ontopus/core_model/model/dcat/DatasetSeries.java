package cz.lukaskabc.ontology.ontopus.core_model.model.dcat;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.DocumentedOWLClass;
import java.util.Set;

@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_DatasetSeries)
public abstract class DatasetSeries<
                MembersIdentifier extends EntityIdentifier,
                SeriesDistributionIdentifier extends EntityIdentifier,
                ID extends EntityIdentifier>
        extends Dataset<SeriesDistributionIdentifier, ID> {

    public void addMember(MembersIdentifier member) {
        getMembers().add(member);
    }

    public abstract MembersIdentifier getFirst();

    public abstract MembersIdentifier getLast();

    public abstract Set<MembersIdentifier> getMembers();

    public boolean hasMember(MembersIdentifier member) {
        return getMembers().contains(member);
    }

    public void removeMember(MembersIdentifier member) {
        getMembers().remove(member);
    }

    public abstract void setFirst(MembersIdentifier first);

    public abstract void setLast(MembersIdentifier last);

    public abstract void setMembers(Set<MembersIdentifier> members);
}
