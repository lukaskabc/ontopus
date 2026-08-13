package cz.lukaskabc.ontology.ontopus.tests.util;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.jspecify.annotations.Nullable;

import java.net.URI;

public record Triple(
        URI subject, URI predicate, URI object, @Nullable URI context) {
    public static Triple of(URI subject, URI predicate, URI object) {
        return of(subject, predicate, object, null);
    }

    private static Triple of(URI subject, URI predicate, URI object, @Nullable URI context) {
        return new Triple(subject, predicate, object, context);
    }

    public static Triple of(String subject, String predicate, String object) {
        return of(subject, predicate, object, null);
    }

    public static Triple of(String subject, String predicate, String object, @Nullable String context) {
        return new Triple(
                URI.create(subject),
                URI.create(predicate),
                URI.create(object),
                context != null ? URI.create(context) : null);
    }

    public Statement toStatement(ValueFactory vf) {
        if (context == null) {
            return vf.createStatement(
                    vf.createIRI(subject.toString()),
                    vf.createIRI(predicate.toString()),
                    vf.createIRI(object.toString()));
        } else {
            return vf.createStatement(
                    vf.createIRI(subject.toString()),
                    vf.createIRI(predicate.toString()),
                    vf.createIRI(object.toString()),
                    vf.createIRI(context.toString()));
        }
    }
}
