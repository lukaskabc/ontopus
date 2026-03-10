package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.VersionArtifactDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.VersionArtifactUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.List;

@Repository
public class VersionArtifactRepository
        extends AbstractRepository<VersionArtifactURI, VersionArtifact, VersionArtifactDao> {
    public VersionArtifactRepository(
            VersionArtifactDao dao,
            Validator validator,
            // TODO replace URI generator with specific type in all repositories
            VersionArtifactUriGenerator identifierGenerator) {
        super(dao, validator, identifierGenerator);
    }

    @Transactional(readOnly = true)
    public Page<VersionArtifact> find(VersionSeriesURI seriesURI, Pageable pageable, List<String> filter) {
        if (pageable.isUnpaged()) {
            pageable = PageRequest.of(0, 100); // TODO config
        }
        List<VersionArtifact> content = dao.find(seriesURI, pageable, filter);
        long totalCount = dao.count(seriesURI, filter);
        return new PageImpl<>(content, pageable, totalCount);
    }
}
