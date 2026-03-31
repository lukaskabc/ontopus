package cz.lukaskabc.ontology.ontopus.plugin.widoco.config;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public class WidocoArguments implements Map<String, String> {
    private final HashMap<String, String> delegate = new HashMap<>();

    @Override
    public void clear() {
        delegate.clear();
    }

    public boolean containsKey(Argument key) {
        return delegate.containsKey(key.name());
    }

    @Override
    public boolean containsKey(Object key) {
        Argument arg = validateAndParseKey(key);
        return containsKey(arg);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public @NonNull Set<Entry<String, String>> entrySet() {
        return delegate.entrySet();
    }

    public void forEachArgument(BiConsumer<Argument, String> action) {
        Objects.requireNonNull(action);
        for (Argument arg : Argument.values()) {
            if (containsKey(arg)) {
                action.accept(arg, get(arg));
            }
        }
    }

    public @Nullable String get(Argument key) {
        return delegate.get(key.name());
    }

    @Nullable @Override
    public String get(Object key) {
        Argument arg = validateAndParseKey(key);
        return get(arg);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    public @Nullable String put(Argument key, String value) {
        return delegate.put(key.name(), value);
    }

    @Override
    public String put(String key, String value) {
        Argument arg = validateAndParseKey(key);

        if (arg != null && value != null) {
            // throws ClassCastException if the type is wrong
            return delegate.put(arg.argument(), value);
        }

        throw new IllegalArgumentException("Key is not a valid Argument");
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (Map.Entry<? extends String, ? extends String> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String remove(Object key) {
        Argument arg = validateAndParseKey(key);
        return delegate.remove(arg.name());
    }

    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * Ensures the raw Object key is a valid String that matches a static known Argument. Throws an exception if it's
     * completely invalid.
     */
    private Argument validateAndParseKey(Object key) {
        if (key instanceof Argument) {
            return (Argument) key;
        }
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a String representing an Argument enum name.");
        }
        // Throws IllegalArgumentException if the String doesn't match an Enum value
        return Argument.valueOf((String) key);
    }

    @Override
    public @NonNull Collection<String> values() {
        return delegate.values();
    }

    public enum Argument {
        CONF_FILE("confFile", "string"),
        ONT_FILE("ontFile", "string"),
        UNITE_SECTIONS("uniteSections", "boolean"),
        ANALYTICS("analytics", "string"),
        CROSS_REF("crossRef", "boolean"),
        DISPLAY_DIRECT_IMPORTS_ONLY("displayDirectImportsOnly", "boolean"),
        DO_NOT_DISPLAY_SERIALIZATIONS("doNotDisplaySerializations", "boolean"),
        ;

        private final String argument;
        /** Type for JSON schema */
        private final String schemaType;

        Argument(String argument, String schemaType) {
            this.argument = argument;
            this.schemaType = schemaType;
        }

        public String argument() {
            return "-" + argument;
        }

        public String schemaType() {
            return schemaType;
        }

        /**
         * Variable expression using the name of the argument
         *
         * @return String: {@code ${name()}} e.g. {@code ${CONF_FILE}}
         */
        public String variable() {
            return "${" + name() + "}";
        }
    }
}
