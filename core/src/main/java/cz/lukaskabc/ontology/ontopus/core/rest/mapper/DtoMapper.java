package cz.lukaskabc.ontology.ontopus.core.rest.mapper;

import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionArtifactResponse;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {VersionArtifactMapper.class, IdentifierMapper.class})
public interface DtoMapper {

    VersionArtifactListEntry versionArtifactToListEntry(VersionArtifact versionArtifact);

    @Mapping(target = "uri", source = "identifier")
    VersionArtifactResponse versionArtifactToResponse(VersionArtifact artifact);

    VersionSeriesListEntry versionSeriesToListEntry(VersionSeries versionSeries);

    @Mapping(target = "uri", source = "identifier")
    VersionSeriesResponse versionSeriesToResponse(VersionSeries versionSeries);
}
