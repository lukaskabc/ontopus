package cz.lukaskabc.ontology.ontopus.core.rest.mapper;

import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import org.jspecify.annotations.NullUnmarked;
import org.mapstruct.Mapper;

@NullUnmarked
@Mapper(uses = {IdentifierMapper.class})
public abstract class VersionArtifactMapper {

    /*
     * public VersionArtifactListEntry
     * identifierToVersionArtifactResponse(VersionArtifactURI identifier) { final
     * VersionArtifact artifact =
     * versionArtifactRepository.findRequired(identifier); return
     * versionArtifactToListEntry(artifact); }
     */

    abstract VersionArtifactListEntry versionArtifactToListEntry(VersionArtifact versionArtifact);

    abstract VersionArtifactResponse versionArtifactToResponse(VersionArtifact versionArtifact);
}
