package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.commons.exec.CommandLine;
import org.springframework.stereotype.Service;
import widoco.gui.GuiController;

import java.io.File;
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
        ensureWidocoExists();
    }

    private void ensureWidocoExists() {
        final File executable = config.getPath();
        if (!executable.exists()) {
            throw new IllegalStateException("Widoco executable not found at " + executable.getAbsolutePath());
        }
        if (!executable.isFile()) {
            throw new IllegalStateException("Widoco executable is not a file: " + executable.getAbsolutePath());
        }
    }

    private void executeWidoco(WidocoArguments arguments) {
        CommandLine cmd = new CommandLine("java")
                .addArgument("-cp")
                .addArgument(config.getPath().getAbsolutePath())
                .addArgument(GuiController.class.getName());

        arguments.forEachArgument(arg -> {
            cmd.addArgument(arg.name()).addArgument(arg.variable());
        });
        // TODO resolve ontology main file

        cmd.setSubstitutionMap(arguments);
    }

    public void runWidoco() {}
}
