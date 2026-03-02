package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.lukaskabc.ontology.ontopus.core_model.model.Triple;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface RdfSupplier extends Supplier<Stream<Triple>> {
    default void forEach(Consumer<? super Triple> action) {
        get().forEach(action);
    }
}
