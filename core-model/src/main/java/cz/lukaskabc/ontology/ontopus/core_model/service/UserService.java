package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.User;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UserDetailsDelegate;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.UserRepository;
import cz.lukaskabc.ontology.ontopus.core_model.util.SecurityConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(String username, String plainPassword) {
        return userRepository.create(username, plainPassword);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username);
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
        return userRepository.userAccountExists(username);
    }
}
