package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@NullMarked
@Component
public class ImportProcessContextHolder {
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

    private ImportProcessContext instance;
    private final TemporaryContextGenerator contextGenerator;
    private final ListableBeanFactory beanFactory;

    @Autowired
    public ImportProcessContextHolder(TemporaryContextGenerator contextGenerator, ListableBeanFactory beanFactory) {
        this.contextGenerator = contextGenerator;
        this.beanFactory = beanFactory;
        this.instance = create();
    }

    /** Creates a new import process with context. */
    private ImportProcessContext create() {
        final UUID uuid = UUID.randomUUID();
        final Path tempFolder = createTempFolder(uuid);
        final URI databaseContext = this.contextGenerator.generate();
        final OntologyArtifact artifact = new OntologyArtifact();

        final ImportProcessContext context = new ImportProcessContext(uuid, databaseContext, tempFolder, artifact);
        createServiceStack(context);
        return context;
    }

    private void createServiceStack(ImportProcessContext context) {
        beanFactory.getBeansOfType(OrderedImportPipelineService.class).values().forEach(service -> {
            context.getServiceStack().push(service);
            service.afterStackPush(context);
        });
    }

    public ImportProcessContext getSessionImportProcessContext() {
        return instance;
    }

    public void resetSessionImportProcess() {
        this.instance = create();
    }
}
