package cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.Collection;

public class RdfResponseWriter implements StreamingResponseBody {
    static WriterConfig prettyPrintConfig() {
        WriterConfig writerConfig = new WriterConfig().useDefaults();
        final boolean enablePrettyPrint = true;
        writerConfig.set(BasicWriterSettings.PRETTY_PRINT, enablePrettyPrint);
        // merge lines: (consumes memory!)
        writerConfig.set(BasicWriterSettings.INLINE_BLANK_NODES, enablePrettyPrint);
        return writerConfig;
    }

    private final RDFWriterFactory writerFactory;

    private final RdfSupplier rdfSupplier;

    private final Collection<PrefixDeclaration> namespaces;

    public RdfResponseWriter(
            RDFWriterFactory writerFactory, RdfSupplier rdfSupplier, Collection<PrefixDeclaration> namespaces) {
        this.writerFactory = writerFactory;
        this.rdfSupplier = rdfSupplier;
        this.namespaces = namespaces;
    }

    @Transactional
    @Override
    public void writeTo(OutputStream outputStream) {
        RDFWriter writer = writerFactory.getWriter(outputStream);
        writer.setWriterConfig(prettyPrintConfig());

        // TODO handle namespaces?
        writer.startRDF();
        namespaces.forEach(ns -> writer.handleNamespace(ns.getPrefix(), ns.getName()));
        rdfSupplier.forEach(writer::handleStatement);
        writer.endRDF();
    }
}
