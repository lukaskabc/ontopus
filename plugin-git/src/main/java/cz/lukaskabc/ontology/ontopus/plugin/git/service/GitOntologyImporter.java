package cz.lukaskabc.ontology.ontopus.plugin.git.service;

import static cz.lukaskabc.ontology.ontopus.api.util.DataHelper.getStringValue;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyImporter;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.git.form.ImportFormData;
import cz.lukaskabc.ontology.ontopus.plugin.git.rest.RepositoryController;
import jakarta.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Component
@NullMarked
public class GitOntologyImporter implements OntologyImporter {
    private static final String NON_ALPHANUMERIC_MATCHER = "[^a-zA-Z0-9]";

    private static Git cloneRepository(ImportFormData importFormData) {
        final File directory = createTempDirectory(importFormData);
        final CloneCommand cmd = Git.cloneRepository()
                .setDirectory(directory)
                // we don't need full history, just the current state
                .setDepth(1)
                .setURI(importFormData.getRepositoryUrl());

        if (importFormData.getBranch() != null) {
            cmd.setBranch(importFormData.getBranch());
        }

        boolean usingCredentials = false;
        if (importFormData.getUsername() != null && importFormData.getPassword() != null) {
            usingCredentials = true;
            cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                    importFormData.getUsername(), importFormData.getPassword()));
        }

        log.debug(
                "Cloning repository {} with branch {} using credentials: {} to directory {}",
                importFormData.getRepositoryUrl(),
                importFormData.getBranch(),
                usingCredentials,
                directory.getAbsolutePath());

        try {
            return cmd.call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private static File createTempDirectory(ImportFormData importFormData) {
        final String canonicalName = importFormData.getRepositoryUrl().replaceAll(NON_ALPHANUMERIC_MATCHER, "");
        try {
            final File tempDir = Files.createTempDirectory("ontopus-plugin-git-repository_" + canonicalName)
                    .toFile();
            tempDir.deleteOnExit();
            return tempDir;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    private final RepositoryRegistry repositoryRegistry;
    private final AsyncTaskExecutor taskExecutor;
    private final Validator validator;

    @Autowired
    public GitOntologyImporter(
            RepositoryRegistry repositoryRegistry, AsyncTaskExecutor taskExecutor, Validator validator) {
        this.repositoryRegistry = repositoryRegistry;
        this.taskExecutor = taskExecutor;
        this.validator = validator;
    }

    @Override
    public JsonNode getImportFormSchema() {
        return JsonResourceLoader.loadJsonSchema(GitPlugin.FORM_RESOURCE_PATH, ImportFormData.class.getSimpleName());
    }

    @Override
    public @Nullable JsonNode getImportFormUiSchema() {
        return JsonResourceLoader.loadUiSchema(GitPlugin.FORM_RESOURCE_PATH, ImportFormData.class.getSimpleName());
    }

    @Override
    public String getSourceName() {
        return "ontopus.plugin.git.sourceName";
    }

    private File importOntology(ImportFormData importFormResult) {
        try (Git git = cloneRepository(importFormResult)) {
            log.info(
                    "Git repository successfully cloned ({} branch: {})",
                    importFormResult.getRepositoryUrl(),
                    git.getRepository().getBranch());
            return Objects.requireNonNull(git.getRepository().getWorkTree());
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    @Override
    public String importOntology(Map<String, String[]> formData, Map<String, MultipartFile> files) {
        final ImportFormData data = new ImportFormData();
        data.setRepositoryUrl(getStringValue(formData, "repositoryUrl"));
        data.setUsername(getStringValue(formData, "username"));
        data.setPassword(getStringValue(formData, "password"));
        data.setBranch(getStringValue(formData, "branch"));
        validator.validateObject(data).failOnError(ValidationException::new); // TODO: exception
        final UUID repositoryId = UUID.randomUUID();
        repositoryRegistry.register(repositoryId, taskExecutor.submit(() -> importOntology(data)));
        return RepositoryController.REPOSITORY_FILE_SELECT_FORM_PATH + repositoryId;
    }
}
