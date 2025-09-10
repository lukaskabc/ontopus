package cz.lukaskabc.ontology.ontopus.core.persistance;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.model.User_;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class UserDao extends AbstractDao<User> {
    @Autowired
    public UserDao(EntityManager em, Validator validator) {
        super(User.class, User_.entityClassIRI.toURI(), em, validator);
    }

    @Nullable public User findByUsername(String username) {
        return resultOrNull(em.createNativeQuery(
                        """
				SELECT ?user WHERE {
				    ?user a ?userType ;
				        ?withUsername ?username .
				}
				""",
                        User.class)
                .setParameter("userType", User_.entityClassIRI)
                .setParameter("withUsername", User_.username.getIRI())
                .setParameter("username", username)::getSingleResult);
    }

    public boolean userAccountExists(@Nullable String username) {
        final var query = em.createNativeQuery("ASK { ?user a ?userType; ?hasUsername ?username }", Boolean.class)
                .setParameter("userType", User_.entityClassIRI)
                .setParameter("hasUsername", User_.username.getIRI());

        if (username != null) {
            query.setParameter("username", username);
        }

        return Boolean.TRUE.equals(resultOrNull(query::getSingleResult));
    }
}
