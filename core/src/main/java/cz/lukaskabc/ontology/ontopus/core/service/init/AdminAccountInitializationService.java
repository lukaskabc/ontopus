package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.persistence.UserDao;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Log4j2
@Component
@Scope(SCOPE_PROTOTYPE)
public class AdminAccountInitializationService implements InitService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminAccountInitializationService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void init() {
        if (userDao.userAccountExists(null)) {
            return;
        }

        final String password = RandomStringUtils.secure().nextAlphabetic(16);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode(password));
        userDao.persist(admin);
        log.warn("\n\n\nNo user account found. Generated new account: admin, password: {}\nMake sure to change the password after the first login!\n", password);
    }
}
