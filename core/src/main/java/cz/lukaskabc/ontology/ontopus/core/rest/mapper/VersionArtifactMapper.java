package cz.lukaskabc.ontology.ontopus.core.rest.mapper;

import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = {IdentifierMapper.class})
public abstract class VersionArtifactMapper {
    @Autowired
    private VersionArtifactRepository versionArtifactRepository;

    public VersionArtifactListEntry identifierToVersionArtifactResponse(VersionArtifactURI identifier) {
        final VersionArtifact artifact = versionArtifactRepository.findRequired(identifier);
        return versionArtifactToListEntry(artifact);
    }

    abstract VersionArtifactListEntry versionArtifactToListEntry(VersionArtifact versionArtifact);

    abstract VersionArtifactResponse versionArtifactToResponse(VersionArtifact versionArtifact);
}
