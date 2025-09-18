package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.model.User_;
import cz.lukaskabc.ontology.ontopus.core.model.id.UserURI;
import org.springframework.stereotype.Component;

@Component
public class UserUriGenerator implements IdentifierGenerator<UserURI, User> {
    // TODO implement abstract generator with existence check
    @Override
    public UserURI generate(User entity) {
        return new UserURI(User_.entityClassIRI + "_" + entity.getUsername());
    }
}
