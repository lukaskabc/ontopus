package cz.lukaskabc.ontology.ontopus.core.rest.mapper;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;

import java.net.URI;

@Mapper
public abstract class IdentifierMapper {
    public @Nullable URI mapTypedIdentifier(TypedIdentifier identifier) {
        if (identifier == null) {
            return null;
        }
        return identifier.toURI();
    }
}
