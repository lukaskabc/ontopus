package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public class UserDetailsDelegate implements UserDetails {
    private final Set<GrantedAuthority> authorities;
    private final User user;

    public UserDetailsDelegate(Set<GrantedAuthority> authorities, User user) {
        this.authorities = authorities;
        this.user = user;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Nullable
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Nullable
    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
