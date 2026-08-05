package cz.lukaskabc.ontology.ontopus.tests.persistence;

import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDaoTest extends DaoTestRunner {
    @Autowired
    private EntityManager em;
}
