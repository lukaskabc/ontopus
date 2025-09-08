package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.persistence.UserDao;
import cz.lukaskabc.ontology.ontopus.core.util.UserDetailsDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static cz.lukaskabc.ontology.ontopus.core.util.SecurityConstants.DEFAULT_AUTHORITIES;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LogManager.getLogger(UserService.class);

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

        return new UserDetailsDelegate(DEFAULT_AUTHORITIES, user);
    }
}
