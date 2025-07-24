package cz.lukaskabc.ontology.ontopus.core.service;

import static cz.lukaskabc.ontology.ontopus.core.util.SecurityConstants.DEFAULT_AUTHORITIES;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.persistence.UserDao;
import cz.lukaskabc.ontology.ontopus.core.util.UserDetailsDelegate;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Log4j2
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

        return new UserDetailsDelegate(DEFAULT_AUTHORITIES, user);
    }
}
