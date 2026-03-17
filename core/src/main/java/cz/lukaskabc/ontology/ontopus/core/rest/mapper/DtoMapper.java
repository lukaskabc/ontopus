package cz.lukaskabc.ontology.ontopus.core.rest.mapper;

import cz.lukaskabc.ontology.ontopus.api.util.EntityOptionsEntry;
import cz.lukaskabc.ontology.ontopus.api.util.GlobalSettingsEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {VersionArtifactMapper.class, IdentifierMapper.class})
public interface DtoMapper {

    @Mapping(target = "optionIdentifier", source = "identifier")
    @Mapping(target = "label", source = "label")
    MenuOptionResponse toMenuOption(EntityOptionsEntry<?> entry);

    @Mapping(target = "optionIdentifier", source = "identifier")
    @Mapping(target = "label", source = "label")
    MenuOptionResponse toMenuOption(GlobalSettingsEntry entry);

    VersionArtifactListEntry versionArtifactToListEntry(VersionArtifact versionArtifact);

    @Mapping(target = "uri", source = "identifier")
    VersionArtifactResponse versionArtifactToResponse(VersionArtifact artifact);

    VersionSeriesListEntry versionSeriesToListEntry(VersionSeries versionSeries);

    @Mapping(target = "uri", source = "identifier")
    VersionSeriesResponse versionSeriesToResponse(VersionSeries versionSeries);
}
