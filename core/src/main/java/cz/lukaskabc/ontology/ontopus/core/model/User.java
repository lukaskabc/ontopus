package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.lukaskabc.ontology.ontopus.generated.Vocabulary;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.URI;


@Setter
@Getter
@Accessors(chain = true)
@OWLClass(iri = User.Meta.TYPE_STRING)
public class User extends PersistenceEntity {

    @NotNull
    @NotEmpty
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.USERNAME_STRING, simpleLiteral = true)
    private String username;

    @NotNull
    @NotEmpty
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.PASSWORD_STRING, simpleLiteral = true)
    private String password;

    public static class Meta {
        public static final String TYPE_STRING = Vocabulary.s_c_UserAccount;
        public static final URI TYPE = URI.create(TYPE_STRING);
        public static final String USERNAME_STRING = Vocabulary.s_p_org_name;
        public static final URI USERNAME = URI.create(USERNAME_STRING);
        public static final String PASSWORD_STRING = Vocabulary.s_p_password;
        public static final URI PASSWORD = URI.create(PASSWORD_STRING);
    }

}
