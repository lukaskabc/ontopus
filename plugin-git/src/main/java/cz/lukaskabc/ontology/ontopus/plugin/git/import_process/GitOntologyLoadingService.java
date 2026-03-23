package cz.lukaskabc.ontology.ontopus.plugin.git.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.core.FileToDatabaseImportingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyLoadingService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitRepositoryUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.exception.FileImportingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class GitOntologyLoadingService implements OntologyLoadingService {
    private static final JsonNode JSON_SCHEMA = JsonResourceLoader.loadJsonSchema(
            GitPlugin.FORM_RESOURCE_PATH, GitRepositoryClonningRequest.class.getSimpleName());
    private static final JsonNode UI_SCHEMA = JsonResourceLoader.loadUiSchema(
            GitPlugin.FORM_RESOURCE_PATH, GitRepositoryClonningRequest.class.getSimpleName());
    private static final Logger log = LogManager.getLogger(GitOntologyLoadingService.class);

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final FileToDatabaseImportingService fileImportingService;
    private final GitRepositoryUtils gitRepositoryUtils;

    public GitOntologyLoadingService(
            ObjectMapper objectMapper,
            Validator validator,
            FileToDatabaseImportingService fileImportingService,
            GitRepositoryUtils gitRepositoryUtils) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.fileImportingService = fileImportingService;
        this.gitRepositoryUtils = gitRepositoryUtils;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return new JsonForm(JSON_SCHEMA, UI_SCHEMA, previousFormData);
    }

    @Override
    public String getServiceName() {
        return "ontopus.plugin.git.service.GitOntologyLoadingService.name";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        final GitRepositoryClonningRequest request = parseFormData(formResult);
        final String canonicalRepoUrl = StringUtils.sanitize(request.repositoryUrl());
        final Path targetDir = context.createTempFolder(Path.of(canonicalRepoUrl));
        gitRepositoryUtils.cloneRepository(request, targetDir.toFile());

        try {
            final List<File> files = FileUtils.listFilesRecursively(targetDir).stream()
                    .filter(path -> !path.toString().contains(File.separator + ".git" + File.separator))
                    .map(Path::toFile)
                    .toList();
            fileImportingService.importFiles(files, context);
        } catch (IOException e) {
            throw new FileImportingException("Failed to import files from cloned repository", e);
        }

        return null;
    }

    private GitRepositoryClonningRequest parseFormData(FormResult formResult) {
        final ObjectNode formData = formResult.jsonFormData(objectMapper);
        final GitRepositoryClonningRequest result =
                objectMapper.convertValue(formData, GitRepositoryClonningRequest.class);
        BeanPropertyBindingResult errors =
                new BeanPropertyBindingResult(result, GitRepositoryClonningRequest.class.getSimpleName());
        validator.validate(result, errors);
        errors.failOnError(ValidationException::new);
        return result;
    }
}
