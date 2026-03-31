package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WidocoExecutionService {
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

    private void execute(CommandLine cmd, Path workingDirectory) {
        ExecuteWatchdog watchdog = ExecuteWatchdog.builder()
                .setTimeout(config.getExecutionTimeout())
                .get();
        DefaultExecutor executor =
                DefaultExecutor.builder().setWorkingDirectory(workingDirectory).get();
        executor.setExitValue(0);
        executor.setWatchdog(watchdog);
        try {
            int exitCode = executor.execute(cmd);
        } catch (IOException e) {
            throw new OntopusException(e); // TODO exception
        }
    }

    private void executeWidoco(WidocoArguments arguments) {
        CommandLine cmd = new CommandLine("java")
                .addArgument("-jar")
                .addArgument(config.getPath().toFile().getAbsolutePath());

        arguments.forEachArgument((arg, value) -> {
            cmd.addArgument(arg.name());
            if (value != null && !value.isEmpty()) {
                cmd.addArgument(arg.variable());
            }
        });

        // TODO resolve ontology main file
        cmd.setSubstitutionMap(arguments);
        try {
            final Path tmp = Files.createTempDirectory("widoco-working-dir");
            executor.submit(() -> execute(cmd, tmp));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
