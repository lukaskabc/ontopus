package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class WidocoExecutionService {
    private static final Logger log = LogManager.getLogger(WidocoExecutionService.class);
    private static final String WIDOCO_OUTPUT_DIR = "widoco_output";
    private final WidocoPluginConfig config;
    private final ExecutorService executor;

    public WidocoExecutionService(WidocoPluginConfig config) {
        this.config = config;
        executor = Executors.newSingleThreadExecutor(
                Thread.ofPlatform().name("widoco-executor").factory());
    }

    private void ensureWidocoExists() {
        final File executable = config.getPath().toFile();
        if (!executable.exists()) {
            throw new IllegalStateException("Widoco executable not found at " + executable.getAbsolutePath());
        }
        if (!executable.isFile()) {
            throw new IllegalStateException("Widoco executable is not a file: " + executable.getAbsolutePath());
        }
    }

    private Path execute(CommandLine cmd, Path workingDirectory) {
        ensureWidocoExists();
        // TODO: run widoco in some jail/chroot to prevent it from accessing file out of
        // the working directory (firejail, bwrap) - but watch the licenses? or docker
        // container?
        final Path outputFolder = workingDirectory.resolve(WIDOCO_OUTPUT_DIR);

        cmd.addArgument("-outFolder").addArgument(outputFolder.toString());

        ExecuteWatchdog watchdog = ExecuteWatchdog.builder()
                .setTimeout(config.getExecutionTimeout())
                .get();

        DefaultExecutor executor =
                DefaultExecutor.builder().setWorkingDirectory(workingDirectory).get();
        executor.setExitValue(0);
        executor.setWatchdog(watchdog);

        log.debug("Running Widoco in {} with {}", workingDirectory, cmd.toString());

        try {
            executor.execute(cmd);
            return outputFolder;
        } catch (IOException e) {
            throw log.throwing(InternalException.fileProcessingException("IO failure during WIDOCO execution", e));
        } catch (Exception e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_widoco)
                    .internalMessage("Failure during WIDOCO execution")
                    .detailMessageArguments(new Object[] {e.getMessage()})
                    .detailMessageCode("ontopus.plugin.widoco.error.widocoExecution.detail")
                    .titleMessageCode("ontopus.plugin.widoco.error.widocoExecution.title")
                    .cause(e)
                    .build());
        }
    }

    public Future<Path> execute(WidocoArguments arguments, Path workDir) {
        CommandLine cmd = new CommandLine("java")
                .addArgument("-jar")
                .addArgument(config.getPath().toFile().getAbsolutePath());

        arguments.forEachArgument((arg, value) -> {
            cmd.addArgument(arg.argument());
            if (value != null && !value.isEmpty()) {
                cmd.addArgument(arg.variable());
            }
        });

        cmd.setSubstitutionMap(arguments);

        return executor.submit(() -> execute(cmd, workDir));
    }
}
