package cz.lukaskabc.ontology.ontopus.core.config;

import com.github.ledsoft.jopa.spring.transaction.DelegatingEntityManager;
import com.github.ledsoft.jopa.spring.transaction.JopaTransactionManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.lukaskabc.ontology.ontopus.core.util.JopaEntityPackagesHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Set;

@Configuration
@EnableTransactionManagement
public class PersistenceConfig {
    @Bean
    public DelegatingEntityManager entityManager() {
        return new DelegatingEntityManager();
    }

    @Bean
    public JopaEntityPackagesHolder jopaEntityPackagesHolder() {
        return new JopaEntityPackagesHolder(Set.of("cz.lukaskabc.ontology.ontopus"));
    }

    @Bean(name = "txManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf, DelegatingEntityManager emProxy) {
        return new JopaTransactionManager(emf, emProxy);
    }
}
