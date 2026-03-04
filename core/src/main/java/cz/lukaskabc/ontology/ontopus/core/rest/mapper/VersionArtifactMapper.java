package cz.lukaskabc.ontology.ontopus.core.rest.mapper;

import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import org.jspecify.annotations.NullUnmarked;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@NullUnmarked
@Mapper(uses = {IdentifierMapper.class})
public abstract class VersionArtifactMapper {
    @Autowired
    private VersionArtifactRepository versionArtifactRepository;

    public Set<VersionArtifactListEntry> versionArtifactIdCollectionToVersionArtifactListEntrySet(
            Collection<VersionArtifactURI> artifacts) {
        return versionArtifactRepository
                .findAllByIds(artifacts)
                .map(this::versionArtifactToListEntry)
                .collect(Collectors.toSet());
    }
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
