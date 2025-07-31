package cz.lukaskabc.ontology.ontopus.plugin.git;

import static cz.lukaskabc.ontology.ontopus.api.util.DataHelper.getStringValue;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyImporter;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.plugin.git.form.ImportFormData;
import jakarta.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
@NullMarked
public class GitOntologyImporter implements OntologyImporter {
    private static final String NON_ALPHANUMERIC_MATCHER = "[^a-zA-Z0-9]";

    private static Git cloneRepository(ImportFormData importFormData) {
        final CloneCommand cmd = Git.cloneRepository()
                .setDirectory(createTempDirectory(importFormData))
                .setURI(importFormData.getRepositoryUrl());
        if (importFormData.getUsername() != null || importFormData.getPassword() != null) {
            Objects.requireNonNull(importFormData.getUsername());
            Objects.requireNonNull(importFormData.getPassword());
            cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                    importFormData.getUsername(), importFormData.getPassword()));
        }
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

    private final Validator validator;

    @Autowired
    public GitOntologyImporter(Validator validator) {
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

    private void importOntology(ImportFormData importFormResult) {
        try (Git git = cloneRepository(importFormResult)) {
            System.out.println("Repository cloned");
            // TODO: let user pick files to import
        }
    }

    @Override
    public void importOntology(Map<String, String[]> formData, Map<String, MultipartFile> files) {
        final ImportFormData data = new ImportFormData();
        data.setRepositoryUrl(getStringValue(formData, "repositoryUrl"));
        data.setUsername(getStringValue(formData, "username"));
        data.setPassword(getStringValue(formData, "password"));
        // TODO add mandatory branch parameter
        validator.validateObject(data).failOnError(ValidationException::new); // TODO: exception
        importOntology(data);
    }
}
