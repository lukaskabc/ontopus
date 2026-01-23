package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.model.User_;
import cz.lukaskabc.ontology.ontopus.core.model.id.UserURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends AbstractDao<UserURI, User> {
    @Autowired
    public UserDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(User.class, User_.entityClassIRI.toURI(), em, descriptorFactory.user());
    }

    @Nullable public User findByUsername(String username) {
        Objects.requireNonNull(username);
        return resultOrNull(em.createNativeQuery("""
				SELECT ?user FROM ?graph WHERE {
				    ?user a ?userType ;
				        ?withUsername ?username .
				}
				""", User.class)
                .setParameter("graph", entityGraphContext)
                .setParameter("userType", User_.entityClassIRI)
                .setParameter("withUsername", User_.username.getIRI())
                .setParameter("username", username)::getSingleResult);
    }

    /**
     * Checks whether a user account with the given username exists. Checks whether any user account exists when no
     * username is given.
     *
     * @param username The username or null
     * @return whether an account with the given username exists
     */
    public boolean userAccountExists(@Nullable String username) {
        final var query = em.createNativeQuery("""
				ASK FROM ?graph {
				    ?user a ?userType;
				        ?hasUsername ?username .
				}
				""", Boolean.class)
                .setParameter("graph", entityGraphContext)
                .setParameter("userType", User_.entityClassIRI)
                .setParameter("hasUsername", User_.username.getIRI());

        if (username != null) {
            query.setParameter("username", username);
        }

        return Boolean.TRUE.equals(resultOrNull(query::getSingleResult));
    }
}
