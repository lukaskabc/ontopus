package cz.lukaskabc.ontology.ontopus.core.service.importer;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.DataFileImportingService;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RDFDataImportingService implements DataFileImportingService {
    private static final Logger log = LogManager.getLogger(RDFDataImportingService.class);
    private final EntityManager em;

    @Autowired
    public RDFDataImportingService(EntityManager em) {
        this.em = em;
    }

    @Transactional
    @Override
    public void importFiles(List<File> files, ImportProcessContext importContext) throws IOException {
        if (files.isEmpty()) {
            return;
        }
        final Model model = loadModel(files);
        final TemporaryContextURI context = importContext.getDatabaseContext();
        final Repository repository = em.unwrap(org.eclipse.rdf4j.repository.Repository.class);
        try (final RepositoryConnection conn = repository.getConnection()) {
            conn.begin();
            final IRI graphContext = repository.getValueFactory().createIRI(context.toString());
            log.debug("Importing ontology model into temporary context <{}>", context.toString());
            conn.add(model, graphContext);
            conn.commit();
        }
    }

    private Model loadModel(List<File> files) throws IOException {
        RDFFormat rdfFormat = resolveFormat(files.getFirst());
        if (rdfFormat == null) {
            throw new IllegalStateException("Unable to resolve file type to supported RDF format"); // TODO exception
        }
        final Model model = new LinkedHashModel();
        final RDFParser parser = Rio.createParser(rdfFormat);
        parser.setRDFHandler(new StatementCollector(model));

        for (File file : files) {
            parser.parse(new FileInputStream(file));
        }
        return model;
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
