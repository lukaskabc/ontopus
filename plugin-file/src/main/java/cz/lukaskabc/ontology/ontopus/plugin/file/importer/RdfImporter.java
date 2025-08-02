package cz.lukaskabc.ontology.ontopus.plugin.file.importer;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.service.FileImporter;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@NullMarked
@Log4j2
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RdfImporter implements FileImporter {
    private final Model model = new LinkedHashModel();
    private final TemporaryContextGenerator contextGenerator;
    private final EntityManager em;

    @Autowired
    public RdfImporter(TemporaryContextGenerator contextGenerator, EntityManager em) {
        this.contextGenerator = contextGenerator;
        this.em = em;
    }

    @Transactional
    @Override
    public void importFiles(File[] files) throws IOException {
        loadModel(files);
        final URI tempContext = contextGenerator.generate();
        // TODO: consider ejecting from jopa and using pure RDF4J
        final Repository repository = em.unwrap(org.eclipse.rdf4j.repository.Repository.class);
        try (final RepositoryConnection conn = repository.getConnection()) {
            conn.begin();
            final IRI context = repository.getValueFactory().createIRI(tempContext.toString());
            log.debug("Importing ontology model into temporary context <{}>", tempContext);
            conn.add(model, context);
            conn.commit();
        }
    }

    private void loadModel(File[] files) throws IOException {
        RDFFormat rdfFormat = resolveFormat(files[0]);
        if (rdfFormat == null) {
            throw new IllegalStateException("Unable to resolve file type to supported RDF format"); // TODO exception
        }

        final RDFParser parser = Rio.createParser(rdfFormat);
        parser.setRDFHandler(new StatementCollector(model));

        for (File file : files) {
            parser.parse(new FileInputStream(file));
        }
    }

    @Nullable private RDFFormat resolveFormat(File file) throws IOException {
        RDFFormat format = null;
        String contentType = Files.probeContentType(file.toPath());
        if (contentType != null) {
            format = Rio.getParserFormatForMIMEType(contentType).orElse(null);
        }
        if (format == null) {
            format = Rio.getParserFormatForFileName(file.getName()).orElse(null);
        }
        return format;
    }

    @Override
    public boolean supports(File file) {
        try {
            return resolveFormat(file) != null;
        } catch (IOException e) {
            return false;
        }
    }
}
