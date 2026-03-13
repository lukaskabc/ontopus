package cz.lukaskabc.ontology.ontopus.plugin.git.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.DataFileImportingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyLoadingService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitRepositoryUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.exception.FileImportingException;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.ArrayList;
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
    private final List<DataFileImportingService> dataFileImportingServices;

    public GitOntologyLoadingService(
            ObjectMapper objectMapper, Validator validator, List<DataFileImportingService> dataFileImportingServices) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.dataFileImportingServices = dataFileImportingServices;
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
        GitRepositoryUtils.cloneRepository(request, targetDir.toFile());

        try {
            final List<File> files = FileUtils.listFilesRecursively(targetDir).stream()
                    .filter(path -> !path.toString().contains(File.separator + ".git" + File.separator))
                    .map(Path::toFile)
                    .toList();
            importFiles(files, context);
        } catch (IOException e) {
            throw new FileImportingException("Failed to import files from cloned repository", e);
        }

        return null;
    }

    private void importFiles(List<File> files, ImportProcessContext context) throws IOException {
        final ArrayList<File> remainingFiles = new ArrayList<>(files);
        final ArrayList<File> toImport = new ArrayList<>(files.size());
        for (DataFileImportingService importingService : dataFileImportingServices) {
            for (File file : remainingFiles) {
                if (importingService.supports(file)) {
                    toImport.add(file);
                }
            }
            if (!toImport.isEmpty()) {
                importingService.importFiles(toImport, context);
                remainingFiles.removeAll(toImport);
                toImport.clear();
            }
        }
        if (!remainingFiles.isEmpty()) {
            log.trace("Failed to import files from cloned repository: {}", ArrayUtils.toString(files.toArray()));
        }
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
