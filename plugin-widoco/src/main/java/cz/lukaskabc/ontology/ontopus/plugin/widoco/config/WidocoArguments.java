package cz.lukaskabc.ontology.ontopus.plugin.widoco.config;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class WidocoArguments implements Map<String, Object> {
    private final HashMap<String, Object> delegate = new HashMap<>();

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
    public Set<Entry<String, Object>> entrySet() {
        return delegate.entrySet();
    }

    public void forEachArgument(Consumer<Argument> action) {
        Objects.requireNonNull(action);
        for (Argument arg : Argument.values()) {
            if (containsKey(arg)) {
                action.accept(arg);
            }
        }
    }

    public <T> @Nullable T get(Key<T> key) {
        Object value = delegate.get(key.argument().name());
        if (value == null) {
            return null;
        }
        return key.cast(value);
    }

    @Nullable @Override
    public Object get(Object key) {
        Argument arg = validateAndParseKey(key);
        return delegate.get(arg.name());
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    public <T> @Nullable Object put(Key<T> key, T value) {
        return delegate.put(key.argument().name(), value);
    }

    @Override
    public Object put(String key, Object value) {
        Argument arg = validateAndParseKey(key);
        Key<?> expectedKey = Key.from(arg);

        if (expectedKey != null && value != null) {
            // throws ClassCastException if the type is wrong
            expectedKey.cast(value);
        }

        return delegate.put(arg.name(), value);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object remove(Object key) {
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
    public Collection<Object> values() {
        return delegate.values();
    }

    public enum Argument {
        CONF_FILE("-confFile"),
        ONT_FILE("-ontFile");

        private final String name;

        Argument(String name) {
            this.name = name;
        }

        /**
         * Variable expression using the name of the argument
         *
         * @return String: {@code ${argumentName}}
         */
        public String variable() {
            return "${" + name + "}";
        }
    }

    public static class Key<T> {
        private static final Map<Argument, Key<?>> REGISTRY = new HashMap<>();
        public static final Key<File> CONF_FILE = makeRegistered(Argument.CONF_FILE, File.class);
        public static final Key<File> ONT_FILE = makeRegistered(Argument.ONT_FILE, File.class);

        /** Retrieves the registered Key for a given argument */
        public static Key<?> from(Argument argument) {
            return Objects.requireNonNull(REGISTRY.get(argument));
        }

        private static <T> Key<T> makeRegistered(Argument argument, Class<T> type) {
            final Key<T> key = new Key<>(argument, type);
            REGISTRY.put(argument, key);
            return key;
        }

        private final Argument argument;

        private final Class<T> type;

        private Key(Argument argument, Class<T> type) {
            this.argument = argument;
            this.type = type;
        }

        public Argument argument() {
            return argument;
        }

        public T cast(Object value) {
            return type.cast(value);
        }

        public Class<T> type() {
            return type;
        }
    }
}
