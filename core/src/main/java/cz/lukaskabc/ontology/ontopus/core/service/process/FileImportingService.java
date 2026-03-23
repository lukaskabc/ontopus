package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.DataFileImportingService;
import cz.lukaskabc.ontology.ontopus.api.service.core.FileToDatabaseImportingService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileImportingService implements FileToDatabaseImportingService {
    private static final Logger log = LogManager.getLogger(FileImportingService.class);
    private final List<DataFileImportingService> dataFileImportingServices;

    public FileImportingService(List<DataFileImportingService> dataFileImportingServices) {
        this.dataFileImportingServices = dataFileImportingServices;
        if (dataFileImportingServices.isEmpty()) {
            throw new IllegalStateException("No data file importing service found!");
        }
    }

    @Override
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
            log.trace(
                    "Failed to import files from cloned repository: {}", ArrayUtils.toString(remainingFiles.toArray()));
        }
        return importedFiles;
    }
}
