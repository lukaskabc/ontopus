package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesListEntry;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionSeries;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DtoMapper {
    VersionSeriesListEntry versionSeriesToListEntry(VersionSeries versionSeries);
}
