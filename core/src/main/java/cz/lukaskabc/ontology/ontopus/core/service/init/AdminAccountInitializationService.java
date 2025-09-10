package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.persistance.UserDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminAccountInitializationService implements InitService {
    private static final Logger log = LogManager.getLogger(AdminAccountInitializationService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

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
        log.warn(
            "\n\n\nNo user account found. Generated new account: admin, password: {}\nMake sure to change the password after the first login!\n",
            password);
    }
}
