package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.UserURI;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.net.URI;

@OWLClass(iri = Vocabulary.s_c_UserAccount)
public class User extends PersistenceEntity<UserURI> {

    @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_password, simpleLiteral = true)
    private String password;

    @Pattern(regexp = "[a-zA-Z0-9][a-zA-Z0-9_-]{3,}[a-zA-Z0-9]") // TODO move to constants
    @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_org_name, simpleLiteral = true)
    private String username;

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected UserURI wrapUri(URI uri) {
        return new UserURI(uri);
    }
}
