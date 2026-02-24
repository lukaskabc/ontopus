package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.User;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.UserURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UserDetailsDelegate;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.UserRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import cz.lukaskabc.ontology.ontopus.core_model.util.SecurityConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<UserURI, User, UserRepository> implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.passwordEncoder = passwordEncoder;
    }

    public User create(String username, String plainPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(plainPassword));
        repository.persist(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username");
        }

        return new UserDetailsDelegate(SecurityConstants.DEFAULT_AUTHORITIES, user);
    }

    /**
     * Checks whether a user account with the given username exists. Checks whether any user account exists when no
     * username is given.
     *
     * @param username The username or null
     * @return whether an account with the given username exists
     */
    public boolean userAccountExists(@Nullable String username) {
        return repository.userAccountExists(username);
    }
}
