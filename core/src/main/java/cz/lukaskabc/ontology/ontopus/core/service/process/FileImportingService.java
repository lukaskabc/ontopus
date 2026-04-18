package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.DataFileImportingService;
import cz.lukaskabc.ontology.ontopus.api.service.core.FileToDatabaseImportingService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.FileImportException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileImportingService implements FileToDatabaseImportingService {
    private static final Logger log = LogManager.getLogger(FileImportingService.class);
    private final List<DataFileImportingService> dataFileImportingServices;

    public FileImportingService(List<DataFileImportingService> dataFileImportingServices) {
        this.dataFileImportingServices = dataFileImportingServices;
        if (dataFileImportingServices.isEmpty()) {
            throw new InitializationException("No data file importing service found!");
        }
    }

    @Override
    @Transactional
    public List<File> importFiles(List<File> filesToImport, ImportProcessContext context) throws IOException {
        final ArrayList<File> importedFiles = new ArrayList<>(filesToImport.size());
        final ArrayList<File> remainingFiles = new ArrayList<>(filesToImport);
        final ArrayList<File> toImport = new ArrayList<>(filesToImport.size());
        for (DataFileImportingService importingService : dataFileImportingServices) {
            for (File file : remainingFiles) {
                if (importingService.supports(file)) {
                    toImport.add(file);
                }
            }
            if (!toImport.isEmpty()) {
                importingService.importFiles(toImport, context);
                importedFiles.addAll(toImport);
                remainingFiles.removeAll(toImport);
                toImport.clear();
            }
        }
        if (!remainingFiles.isEmpty()) {
            final Path rootDir = context.getTempFolder();
            final String unimportedFiles = remainingFiles.stream()
                    .map(File::toPath)
                    .map(rootDir::relativize)
                    .map(Path::toString)
                    .collect(Collectors.joining(", "));
            throw FileImportException.builder()
                    .errorType(Vocabulary.u_i_unsupported_format)
                    .internalMessage("Failed to import files into the database: Unsupported format")
                    .detailMessageArguments(new Object[] {unimportedFiles})
                    .detailMessageCode("ontopus.core.error.fileProcessing.unsupportedFormat")
                    .titleMessageCode("ontopus.core.error.fileProcessing.importFailed")
                    .build();
        }
        return importedFiles;
    }

    public boolean supports(File file) {
        for (DataFileImportingService importingService : dataFileImportingServices) {
            if (!importingService.supports(file)) {
                return false;
            }
        }
        return true;
    }

    public boolean supports(Path path) {
        return supports(path.toFile());
    }
}
