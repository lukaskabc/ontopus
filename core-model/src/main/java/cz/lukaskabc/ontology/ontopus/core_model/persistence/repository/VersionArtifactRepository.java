package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.VersionArtifactDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Validator;

import java.util.Collection;
import java.util.stream.Stream;

@Repository
public class VersionArtifactRepository
        extends AbstractRepository<VersionArtifactURI, VersionArtifact, VersionArtifactDao> {
    public VersionArtifactRepository(
            VersionArtifactDao dao,
            Validator validator,
            IdentifierGenerator<VersionArtifactURI, VersionArtifact> identifierGenerator) {
        super(dao, validator, identifierGenerator);
    }

    public Stream<VersionArtifact> findAllByIds(Collection<VersionArtifactURI> identifiers) {
        return dao.findAllRecentByIds(identifiers); // TODO: optimize fetching paged artifacts for series
    }
}
