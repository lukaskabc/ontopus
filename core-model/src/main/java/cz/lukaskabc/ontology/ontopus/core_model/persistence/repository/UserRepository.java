package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.User;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.UserURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.UserDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

@Repository
public class UserRepository extends AbstractRepository<UserURI, User, UserDao> {

    public UserRepository(UserDao dao, Validator validator, IdentifierGenerator<UserURI, User> identifierGenerator) {
        super(dao, validator, identifierGenerator);
    }

    @Transactional(readOnly = true)
    @Nullable public User findByUsername(@Nullable String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return dao.findByUsername(username);
    }

    /**
     * Checks whether a user account with the given username exists. Checks whether any user account exists when no
     * username is given.
     *
     * @param username The username or null
     * @return whether an account with the given username exists
     */
    @Transactional(readOnly = true)
    public boolean userAccountExists(@Nullable String username) {
        return dao.userAccountExistsWithUsername(username);
    }
}
