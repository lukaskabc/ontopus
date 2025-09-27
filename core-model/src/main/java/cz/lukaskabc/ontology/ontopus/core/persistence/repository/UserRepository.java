package cz.lukaskabc.ontology.ontopus.core.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.model.id.UserURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.UserDao;
import cz.lukaskabc.ontology.ontopus.core.persistence.identifier.IdentifierGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

@Repository
public class UserRepository extends AbstractRepository<UserURI, User, UserDao> {
    private final PasswordEncoder passwordEncoder;

    public UserRepository(
            UserDao dao,
            Validator validator,
            IdentifierGenerator<UserURI, User> identifierGenerator,
            PasswordEncoder passwordEncoder) {
        super(dao, validator, identifierGenerator);
        this.passwordEncoder = passwordEncoder;
    }

    public User create(String username, String plainPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(plainPassword));
        persist(user);
        return user;
    }

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
    public boolean userAccountExists(@Nullable String username) {
        return dao.userAccountExists(username);
    }
}
