package cz.lukaskabc.ontology.ontopus.core.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Set;

public class SecurityConstants {
    public static final Set<GrantedAuthority> DEFAULT_AUTHORITIES =
            Set.copyOf(AuthorityUtils.createAuthorityList("ROLE_USER"));

    private SecurityConstants() {
        throw new AssertionError();
    }
}
