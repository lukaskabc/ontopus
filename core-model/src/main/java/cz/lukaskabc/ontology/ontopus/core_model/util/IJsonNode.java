package cz.lukaskabc.ontology.ontopus.core_model.util;

import org.jspecify.annotations.Nullable;
import tools.jackson.core.*;
import tools.jackson.databind.JacksonSerializable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsontype.TypeSerializer;

import java.util.Collection;
import java.util.Collections;

/**
 * Delegating <b>immutable</b> implementation of {@link TreeNode} that wraps a {@link JsonNode} and delegates all method
 * calls to it.
 */
public class IJsonNode extends JacksonSerializable.Base implements TreeNode {
    private static @Nullable TreeNode wrap(JsonNode node) {
        if (node == null) {
            return null;
        }
        return new IJsonNode(node);
    }

    private final JsonNode delegate;

    public IJsonNode(JsonNode delegate) {
        this.delegate = delegate;
    }

    @Override
    public JsonToken asToken() {
        return delegate.asToken();
    }

    @Override
    public TreeNode at(JsonPointer ptr) {
        return wrap(delegate.at(ptr));
    }

    @Override
    public TreeNode at(String ptrExpr) throws IllegalArgumentException {
        return wrap(delegate.at(ptrExpr));
    }

    public JsonNode deepCopy() {
        return delegate.deepCopy();
    }

    @Override
    public TreeNode get(String propertyName) {
        return wrap(delegate.get(propertyName));
    }

    @Override
    public TreeNode get(int index) {
        return wrap(delegate.get(index));
    }

    @Override
    public boolean isArray() {
        return delegate.isArray();
    }

    @Override
    public boolean isContainer() {
        return delegate.isContainer();
    }

    @Override
    public boolean isEmbeddedValue() {
        return delegate.isEmbeddedValue();
    }

    @Override
    public boolean isMissingNode() {
        return delegate.isMissingNode();
    }

    @Override
    public boolean isNull() {
        return delegate.isNull();
    }

    @Override
    public boolean isObject() {
        return delegate.isObject();
    }

    @Override
    public boolean isValueNode() {
        return delegate.isValueNode();
    }

    @Override
    public JsonParser.NumberType numberType() {
        return delegate.numberType();
    }

    @Override
    public TreeNode path(String propertyName) {
        return wrap(delegate.path(propertyName));
    }

    @Override
    public TreeNode path(int index) {
        return wrap(delegate.path(index));
    }

    @Override
    public Collection<String> propertyNames() {
        return Collections.unmodifiableCollection(delegate.propertyNames());
    }

    @Override
    public void serialize(JsonGenerator gen, SerializationContext serializers) throws JacksonException {
        delegate.serialize(gen, serializers);
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializationContext serializers, TypeSerializer typeSer)
            throws JacksonException {
        delegate.serializeWithType(gen, serializers, typeSer);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public JsonParser traverse(ObjectReadContext readCtxt) {
        return delegate.traverse(readCtxt);
    }
}
