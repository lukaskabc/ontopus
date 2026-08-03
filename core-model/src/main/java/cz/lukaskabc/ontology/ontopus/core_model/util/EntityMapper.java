package cz.lukaskabc.ontology.ontopus.core_model.util;

import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import org.mapstruct.*;

@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface EntityMapper {
    // When mapping an entity, review the entity source for large collections!

    @Mapping(target = "types", ignore = true)
    void copy(VersionArtifact from, @MappingTarget VersionArtifact to);
}
