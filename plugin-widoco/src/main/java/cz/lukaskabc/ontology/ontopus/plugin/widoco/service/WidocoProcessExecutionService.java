package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.exception.WidocoExecutionException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Service capable of running the widoco executable */
@Service
public class WidocoProcessExecutionService {
    private static final Logger log = LogManager.getLogger(WidocoProcessExecutionService.class);
    private static final String WIDOCO_OUTPUT_DIR = "widoco_output";
    private final WidocoPluginConfig config;
    private final ExecutorService executor;

    public WidocoProcessExecutionService(WidocoPluginConfig config) {
        this.config = config;
        executor = Executors.newSingleThreadExecutor(
                Thread.ofPlatform().name("widoco-executor").factory());
    }

    private void ensureWidocoExists() {
        final File executable = config.getPath().toFile();
        if (!executable.exists()) {
            throw new InitializationException("Widoco executable not found at " + executable.getAbsolutePath());
        }
        if (!executable.isFile()) {
            throw new InitializationException("Widoco executable is not a file: " + executable.getAbsolutePath());
        }
    }

    private Path execute(CommandLine cmd, Path workingDirectory, ExecuteStreamHandler streamHandler)
            throws WidocoExecutionException {
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
        executor.setStreamHandler(streamHandler);

        log.debug("Running Widoco in {} with {}", workingDirectory, cmd.toString());

        try {
            executor.execute(cmd);
            return outputFolder;
        } catch (Exception e) {
            throw new WidocoExecutionException("Widoco execution failed: " + e.getMessage(), e);
        }
    }

    public Future<Path> execute(WidocoArguments arguments, Path workDir, ExecuteStreamHandler streamHandler) {
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

        return executor.submit(() -> execute(cmd, workDir, streamHandler));
    }
}
