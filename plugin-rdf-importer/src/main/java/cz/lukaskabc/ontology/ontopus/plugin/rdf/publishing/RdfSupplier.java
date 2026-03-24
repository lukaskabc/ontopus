package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import org.eclipse.rdf4j.model.Statement;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface RdfSupplier extends Supplier<List<Statement>> {
    default void forEach(Consumer<Statement> action) {
        get().forEach(action);
    }
}
