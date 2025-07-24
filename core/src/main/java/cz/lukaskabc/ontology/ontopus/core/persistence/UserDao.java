package cz.lukaskabc.ontology.ontopus.core.persistence;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.User;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.net.URI;

@Component
public class UserDao extends BaseDao<User> {
    @Autowired
    public UserDao(EntityManager em, Validator validator) {
        super(User.class, URI.create("ontology/system-user"), em, validator);
    }

    @Nullable
    public User findByUsername(String username) {
        return handleExceptions(
                em.createNativeQuery("""
                                SELECT ?user WHERE {
                                    ?user a ?userType ;
                                        ?withUsername ?username .
                                }
                                """, User.class)
                        .setParameter("userType", User.Meta.TYPE)
                        .setParameter("withUsername", User.Meta.USERNAME)
                        .setParameter("username", username)
                        ::getSingleResult
        );
    }

    public boolean userAccountExists(@Nullable String username) {
        final var userMeta = em.getMetamodel().entity(User.class);
        final var query = em.createNativeQuery("ASK { ?user a ?userType; ?hasUsername ?username }", Boolean.class)
                .setParameter("userType", userMeta.getIRI().toURI())
                .setParameter("hasUsername", userMeta.getAttribute("username").getIRI().toURI());

        if (username != null) {
            query.setParameter("username", username);
        }

        return Boolean.TRUE.equals(handleExceptions(query::getSingleResult));
    }

}
