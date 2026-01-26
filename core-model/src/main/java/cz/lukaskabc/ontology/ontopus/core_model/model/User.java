package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.UserURI;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@OWLClass(iri = Vocabulary.s_c_UserAccount)
public class User extends PersistenceEntity<UserURI> {
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private UserURI identifier;

    @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_password, simpleLiteral = true)
    private String password;

    @Pattern(regexp = "[a-zA-Z0-9][a-zA-Z0-9_-]{3,}[a-zA-Z0-9]") // TODO move to constants
    @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_org_name, simpleLiteral = true)
    private String username;

    @Override
    public UserURI getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void setIdentifier(UserURI identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
