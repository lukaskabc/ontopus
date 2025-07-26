package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.lukaskabc.ontology.ontopus.generated.Vocabulary;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@OWLClass(iri = User.Meta.s_TYPE)
public class User extends PersistenceEntity {

    @NotNull @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_PASSWORD, simpleLiteral = true)
    private String password;

    @NotNull @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_USERNAME, simpleLiteral = true)
    private String username;

    public static class Meta {
        public static final String s_PASSWORD = Vocabulary.s_p_password;
        public static final URI PASSWORD = URI.create(s_PASSWORD);
        public static final String s_TYPE = Vocabulary.s_c_UserAccount;
        public static final URI TYPE = URI.create(s_TYPE);
        public static final String s_USERNAME = Vocabulary.s_p_org_name;
        public static final URI USERNAME = URI.create(s_USERNAME);

        private Meta() {
            throw new AssertionError();
        }
    }
}
