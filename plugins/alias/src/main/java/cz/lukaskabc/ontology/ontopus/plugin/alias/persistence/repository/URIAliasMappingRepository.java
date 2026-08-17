package cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.repository;

import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.dao.URIAliasMappingDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

@Repository
public class URIAliasMappingRepository {
    private final URIAliasMappingDao dao;

    public URIAliasMappingRepository(URIAliasMappingDao dao) {
        this.dao = dao;
    }

    @Transactional
    public Optional<URI> findAliasFor(URI resource) {
        return Optional.ofNullable(dao.findAliasFor(resource));
    }

    @Transactional
    public void save(URIAliasMapping aliasMapping) {
        dao.save(aliasMapping);
    }
}
