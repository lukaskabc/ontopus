package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.model.util.UserDetailsDelegate;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.UserDao;
import cz.lukaskabc.ontology.ontopus.core.util.SecurityConstants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService implements UserDetailsService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("Invalid username");
        }

        final User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username");
        }

        return new UserDetailsDelegate(SecurityConstants.DEFAULT_AUTHORITIES, user);
    }
}
