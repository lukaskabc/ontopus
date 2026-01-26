package cz.lukaskabc.ontology.ontopus.core_model.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.UserURI;

@Converter(autoApply = true)
public class UserUriConverter extends AbstractEntityIdentifierConverter<UserURI>
        implements AttributeConverter<UserURI, String> {
    @Override
    public UserURI convertToAttribute(String value) {
        return new UserURI(value);
    }
}
