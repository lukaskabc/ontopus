package cz.lukaskabc.ontology.ontopus.plugin.rdf.importing;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.DataFileImportingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RDFDataImportingService implements DataFileImportingService {
    private static final Logger LOG = LogManager.getLogger(RDFDataImportingService.class);

    public static Model loadModel(List<File> files) throws IOException {
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

    @Nullable private static RDFFormat resolveFormat(File file) throws IOException {
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

    private static void saveNamespaces(VersionArtifact versionArtifact, Model model) {
        model.getNamespaces().stream().map(PrefixDeclaration::new).forEach(versionArtifact::addPrefixDeclaration);
    }

    private final GraphDao graphDao;

    public RDFDataImportingService(GraphDao graphDao) {
        this.graphDao = graphDao;
    }

    @Transactional
    @Override
    public void importFiles(List<File> files, ImportProcessContext importContext) throws IOException {
        if (files.isEmpty()) {
            return;
        }
        final Model model = loadModel(files);
        saveNamespaces(importContext.getVersionArtifact(), model);
        final TemporaryContextURI context = importContext.getTemporaryDatabaseContext();
        graphDao.persistModel(context, model);
    }

    @Override
    public boolean supports(File file) {
        try {
            final RDFFormat format = resolveFormat(file);
            if (format != null) {
                LOG.debug("Resolved RDF format <{}> for file <{}>", format, file.getName());
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
