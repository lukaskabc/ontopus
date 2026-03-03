package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.lang.reflect.Constructor;
import java.util.Objects;

@NullMarked
public class TypedIdentifierDeserializer extends ValueDeserializer<AbstractTypedIdentifier> {
    @Nullable private final Class<? extends AbstractTypedIdentifier> targetType;

    public TypedIdentifierDeserializer() {
        targetType = null;
    }

    public TypedIdentifierDeserializer(Class<? extends AbstractTypedIdentifier> targetType) {
        this.targetType = targetType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        Class<?> rawClass = property.getType().getRawClass();
        if (!AbstractTypedIdentifier.class.isAssignableFrom(rawClass)) {
            throw new IllegalArgumentException("Cannot use TypedIdentifierDeserializer for type " + rawClass.getName());
        }
        if (Objects.equals(targetType, rawClass)) {
            return this;
        }
        Class<? extends AbstractTypedIdentifier> castType = (Class<? extends AbstractTypedIdentifier>) rawClass;
        return new TypedIdentifierDeserializer(castType);
    }

    @Override
    public AbstractTypedIdentifier deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String uriString = p.getValueAsString();
        Objects.requireNonNull(uriString);
        Objects.requireNonNull(
                targetType, "Target type must be set for deserialization, deserializer created without context!");

        try {
            Constructor<? extends AbstractTypedIdentifier> constructor = targetType.getConstructor(String.class);
            return constructor.newInstance(uriString);
        } catch (Exception e) {
            throw ctxt.instantiationException(targetType, e);
        }
    }
}
