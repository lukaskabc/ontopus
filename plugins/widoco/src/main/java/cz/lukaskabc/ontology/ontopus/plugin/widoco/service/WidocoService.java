package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import com.google.common.collect.MapMaker;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.WidocoConstants;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.Argument;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.exception.WidocoExecutionException;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.rest.WidocoLogController;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Service
public class WidocoService {
    private static final Logger log = LogManager.getLogger(WidocoService.class);
    private final ConcurrentMap<UUID, Path> logFiles =
            new MapMaker().weakValues().makeMap();

    private final OntologyToFileSerializationService ontologyToFileSerializationService;

    private final WidocoProcessExecutionService exceptionService;

    private final WidocoPluginConfig config;
    private final URI systemUri;

    public WidocoService(
            OntologyToFileSerializationService ontologyToFileSerializationService,
            WidocoProcessExecutionService exceptionService,
            WidocoPluginConfig widocoConfig,
            OntopusConfig ontopusConfig) {
        this.ontologyToFileSerializationService = ontologyToFileSerializationService;
        this.exceptionService = exceptionService;
        this.config = widocoConfig;
        this.systemUri = ontopusConfig.getSystemUri();
    }
    /**
     * Awaits competition of the future with twice as long {@link WidocoPluginConfig#getExecutionTimeout() execution
     * timeout}. Rethrows wrapped {@link WidocoExecutionException} any wraps any other exceptions in
     * {@link InternalException}
     *
     * @param future the future to await
     * @throws WidocoExecutionException when unwrapped from the future
     */
    private <T> T awaitFuture(Future<T> future) throws WidocoExecutionException {
        try {
            return future.get(config.getExecutionTimeout().toMillis() * 2, TimeUnit.MILLISECONDS);
        } catch (CancellationException | InterruptedException | TimeoutException e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_widoco)
                    .internalMessage("Exception during Widoco execution")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .cause(e)
                    .build());
        } catch (ExecutionException e) {
            if (e.getCause() instanceof WidocoExecutionException we) {
                throw we;
            } else {
                throw log.throwing(InternalException.builder()
                        .errorType(Vocabulary.u_i_widoco)
                        .internalMessage("Widoco execution error")
                        .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                        .cause(e)
                        .build());
            }
        }
    }

    private void executionFailed(Path logFile, WidocoExecutionException e, ImportProcessContext context) {
        final UUID logUUID = UUID.randomUUID();
        logFiles.put(logUUID, logFile);
        // binds logFile object lifetime to context or until its overwritten
        // the log file is already in Context's directory
        context.setAdditionalProperty(WidocoConstants.CONTEXT_ADDITIONAL_PROPERTY_LOG_FILE, logFile);

        final URI logUri = UriComponentsBuilder.fromUri(systemUri)
                .path("/")
                .path(WidocoLogController.PATH)
                .path("/")
                .path(logUUID.toString())
                .build()
                .toUri();

        final String message = Objects.requireNonNullElse(e.getCause(), e).getMessage();

        throw log.throwing(InternalException.builder()
                .errorType(Vocabulary.u_i_widoco)
                .internalMessage("Failure during Widoco execution")
                .detailMessageArguments(new Object[] {message, logUri})
                .detailMessageCode("ontopus.plugin.widoco.error.widocoExecution.detailLog")
                .titleMessageCode("ontopus.plugin.widoco.error.widocoExecution.title")
                .cause(e)
                .build());
    }

    @Nullable public Path getLogFile(UUID logUUID) {
        final Path path = logFiles.get(logUUID);
        if (path != null && Files.isRegularFile(path)) {
            return path;
        }
        return null;
    }

    private void persistOutput(Path widocoOutput, ImportProcessContext context) {
        final String persistentContext = StringUtils.sanitizeUriAsComponent(
                context.getFinalDatabaseContext().toString());
        final Path filesDestination = FileUtils.resolvePath(config.getFilesDirectory(), Path.of(persistentContext));
        try {
            FileSystemUtils.deleteRecursively(filesDestination);
            FileSystemUtils.copyRecursively(widocoOutput, filesDestination);
        } catch (IOException e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_file_processing)
                    .internalMessage("Failed to persist Widoco output")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .titleMessageCode("ontopus.plugin.widoco.error.persistOutput")
                    .cause(e)
                    .build());
        }
    }

    private Path resolveWidocoOutputRoot(Path output) {
        try (Stream<Path> filePaths = FileUtils.listRecursively(output)) {
            return filePaths
                    .filter(path -> WidocoConstants.WIDOCO_OUTPUT_DIRECTORIES.contains(
                            path.getFileName().toString()))
                    .findAny()
                    .map(Path::getParent)
                    .orElseThrow(() -> log.throwing(InternalException.builder()
                            .errorType(Vocabulary.u_i_file_processing)
                            .internalMessage("Failed to resolve Widoco root output folder")
                            .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                            .titleMessageCode("ontopus.plugin.widoco.error.noWidocoOutputFolder")
                            .build()));
        }
    }

    public void runWidoco(WidocoArguments arguments, ImportProcessContext context) {
        final Path workDir = context.createTempFolder(Path.of("widoco-work-dir-" + StringUtils.randomString(5)));
        final Path logFile = workDir.resolve("output.log");
        final Path ontologyFile = serializeOntology(workDir, context);
        arguments.put(Argument.ONT_FILE, ontologyFile.toString());

        try (FileOutputStream fos = new FileOutputStream(logFile.toFile());
                PrintStream printStream = new PrintStream(fos)) {
            final PumpStreamHandler streamHandler = new PumpStreamHandler(printStream);

            final Future<Path> future = exceptionService.execute(arguments, workDir, streamHandler);
            final Path output = awaitFuture(future);
            final Path widocoOutputRoot = resolveWidocoOutputRoot(output);
            persistOutput(widocoOutputRoot, context);

        } catch (IOException e) {
            throw log.throwing(InternalException.fileProcessingException("Error while writing Widoco log file", e));
        } catch (WidocoExecutionException e) {
            executionFailed(logFile, e, context);
        }
    }

    /**
     * Serializes the ontology from the import context to {@code ontology.ttl} file in the destination directory.
     *
     * @param directory the destination directory
     * @param context the import context
     */
    private Path serializeOntology(Path directory, ImportProcessContext context) {
        final Path ontologyFile = directory.resolve("ontology.ttl");
        ontologyToFileSerializationService.serializeOntologyToFile(
                context.getVersionArtifact().getPrefixDeclarations(),
                context.getTemporaryDatabaseContext(),
                ontologyFile);
        return ontologyFile;
    }
}
