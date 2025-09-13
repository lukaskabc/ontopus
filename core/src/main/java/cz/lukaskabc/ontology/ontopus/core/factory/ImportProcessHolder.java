package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import cz.lukaskabc.ontology.ontopus.core.model.ImportProcess;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@NullMarked
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ImportProcessHolder {
    private static final String TEMPORARY_FOLDER_PREFIX = "OntoPuS-import-process-";
    /**
     * Creates a temporary folder prefixed with {@link #TEMPORARY_FOLDER_PREFIX} and the {@code uuid}. The folder will
     * be automatically deleted on JVM exit.
     *
     * @param uuid The UUID of the import process
     * @return The path of the newly created temporary folder
     * @see File#deleteOnExit()
     */
    private static Path createTempFolder(UUID uuid) {
        try {
            final Path folder = Files.createTempDirectory(TEMPORARY_FOLDER_PREFIX + uuid + "_");
            folder.toFile().deleteOnExit();
            return folder;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    private ImportProcess instance = create();

    private final TemporaryContextGenerator contextGenerator;
    private final ListableBeanFactory beanFactory;

    @Autowired
    public ImportProcessHolder(TemporaryContextGenerator contextGenerator, ListableBeanFactory beanFactory) {
        this.contextGenerator = contextGenerator;
        this.beanFactory = beanFactory;
    }

    /** Creates a new import process with context. */
    private ImportProcess create() {
        final UUID uuid = UUID.randomUUID();
        final Path tempFolder = createTempFolder(uuid);
        final URI databaseContext = this.contextGenerator.generate();
        final OntologyArtifact artifact = new OntologyArtifact();

        final ImportProcessContext context = new ImportProcessContext(uuid, databaseContext, tempFolder, artifact);
        return new ImportProcess(context, getProcessingServices());
    }

    private Stack<ImportProcessingService<?>> getProcessingServices() {
        Stack<ImportProcessingService<?>> stack = new Stack<>();

        return stack;
    }

    public ImportProcess getSessionImportProcess() {
        return instance;
    }

    public void resetSessionImportProcess() {
        this.instance = create();
    }
}
