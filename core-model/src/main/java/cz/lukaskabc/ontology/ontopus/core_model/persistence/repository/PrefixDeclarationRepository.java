package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.Rdf4JAbstractNamespaceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.PrefixDeclarationDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Repository
public class PrefixDeclarationRepository
        extends AbstractRepository<Rdf4JAbstractNamespaceURI, PrefixDeclaration, PrefixDeclarationDao> {

    public PrefixDeclarationRepository(PrefixDeclarationDao dao, Validator validator, OntopusConfig ontopusConfig) {
        super(dao, validator, null, ontopusConfig);
    }

    /**
     * Replaces all prefix declarations with a managed entity object if the declaration for the same prefix and
     * namespace already exists.
     *
     * @param declarations declarations to deduplicate against the database
     */
    @Transactional(readOnly = true)
    public void deduplicate(Collection<PrefixDeclaration> declarations) {
        Iterator<PrefixDeclaration> it = declarations.iterator();
        while (it.hasNext()) {
            final PrefixDeclaration declaration = it.next();
            findByPrefixAndNamespace(declaration.getPrefix(), declaration.getName())
                    .ifPresent(existing -> {
                        it.remove();
                        declarations.add(existing);
                    });
        }
    }

    @Transactional
    @Override
    public void deleteById(Rdf4JAbstractNamespaceURI id) {
        super.deleteById(id);
        removeOrphans();
    }

    @Transactional(readOnly = true)
    public Optional<PrefixDeclaration> findByPrefixAndNamespace(String prefix, String namespace) {
        return Optional.ofNullable(dao.findByPrefixAndNamespace(prefix, namespace));
    }

    @Transactional
    public void removeOrphans() {
        dao.removeOrphans();
    }
}
