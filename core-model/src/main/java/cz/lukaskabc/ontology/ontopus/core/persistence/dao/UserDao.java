package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.User;
import cz.lukaskabc.ontology.ontopus.core.model.User_;
import cz.lukaskabc.ontology.ontopus.core.model.id.UserURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import cz.lukaskabc.ontology.ontopus.core.persistence.identifier.UserUriGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class UserDao extends AbstractDao<UserURI, User> {
    @Autowired
    public UserDao(
            EntityManager em, Validator validator, DescriptorFactory descriptorFactory, UserUriGenerator uriGenerator) {
        super(User.class, User_.entityClassIRI.toURI(), em, validator, descriptorFactory.user(), uriGenerator);
    }

    @Nullable public User findByUsername(String username) {
        return resultOrNull(em.createNativeQuery(
                        """
				SELECT ?user FROM ?graph WHERE {
				    ?user a ?userType ;
				        ?withUsername ?username .
				}
				""",
                        User.class)
                .setParameter("graph", entityGraphContext)
                .setParameter("userType", User_.entityClassIRI)
                .setParameter("withUsername", User_.username.getIRI())
                .setParameter("username", username)::getSingleResult);
    }

    public boolean userAccountExists(@Nullable String username) {
        final var query = em.createNativeQuery(
                        """
				ASK FROM ?graph {
				    ?user a ?userType;
				        ?hasUsername ?username .
				}
				""",
                        Boolean.class)
                .setParameter("graph", entityGraphContext)
                .setParameter("userType", User_.entityClassIRI)
                .setParameter("hasUsername", User_.username.getIRI());

        if (username != null) {
            query.setParameter("username", username);
        }

        return Boolean.TRUE.equals(resultOrNull(query::getSingleResult));
    }
}
