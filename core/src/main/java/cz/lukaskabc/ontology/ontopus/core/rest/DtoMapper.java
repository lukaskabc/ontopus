package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.net.URI;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DtoMapper {
    default @Nullable URI mapTypedIdentifier(EntityIdentifier identifier) {
        if (identifier == null) {
            return null;
        }
        return identifier.toURI();
    }

    VersionSeriesListEntry versionSeriesToListEntry(VersionSeries versionSeries);

    @Mapping(target = "uri", source = "identifier")
    VersionSeriesResponse versionSeriesToResponse(VersionSeries versionSeries);
}
