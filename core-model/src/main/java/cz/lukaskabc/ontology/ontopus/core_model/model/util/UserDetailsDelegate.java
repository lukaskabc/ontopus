package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import cz.lukaskabc.ontology.ontopus.core_model.model.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.Set;

/**
 * Delegates calls to the {@link User} entity.
 *
 * <p>Holds a set of authorities associated with the user.
 */
public class UserDetailsDelegate implements UserDetails {
    private final Set<GrantedAuthority> authorities;
    private final User user;

    public UserDetailsDelegate(Set<GrantedAuthority> authorities, User user) {
        Objects.requireNonNull(authorities, "Authorities cannot be null");
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(user.getUsername(), "User's username cannot be null");
        this.authorities = authorities;
        this.user = user;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Nullable @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
