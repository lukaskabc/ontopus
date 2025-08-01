package cz.lukaskabc.ontology.ontopus.plugin.git.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.StagedJsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.FileImporter;
import cz.lukaskabc.ontology.ontopus.plugin.git.form.RepositoryFileSelectFormSchema;
import cz.lukaskabc.ontology.ontopus.plugin.git.service.RepositoryRegistry;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class RepositoryController {
    private static final String BASE_PATH = "/plugins/git/repository";
    public static final String REPOSITORY_FILE_SELECT_FORM_PATH = BASE_PATH + "/files/";

    private final RepositoryRegistry repositoryRegistry;
    private final ApplicationContext applicationContext;

    public RepositoryController(RepositoryRegistry repositoryRegistry, ApplicationContext applicationContext) {
        this.repositoryRegistry = repositoryRegistry;
        this.applicationContext = applicationContext;
    }

    private void includeFileIfSupported(
            List<String> files, File file, String rootDir, Set<String> supportedFileExtensions) {
        // TODO: use type probe? Files.probeContentType(file.toPath())
        int dotIndex = file.getName().lastIndexOf('.');
        if (dotIndex < 0) {
            return;
        }
        String extension = file.getName().substring(dotIndex + 1);
        if (supportedFileExtensions.contains(extension)) {
            files.add(file.getAbsolutePath().replace(rootDir, ""));
        }
    }

    private List<FileImporter> getFileImporters() {
        // allows importers to be prototypes
        final String[] names = applicationContext.getBeanNamesForType(FileImporter.class);
        List<FileImporter> importers = new ArrayList<>(names.length);
        for (String name : names) {
            importers.add(applicationContext.getBean(name, FileImporter.class));
        }
        return Collections.unmodifiableList(importers);
    }

    private Set<String> getSupportedFileExtensions() {
        return getFileImporters().stream()
                .map(FileImporter::getSupportedFileExtensions)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<String> listAllFiles(File directory) {
        final Set<String> supportedFileExtensions = getSupportedFileExtensions();
        final String rootDir = directory.getAbsolutePath();
        List<String> allFiles = new ArrayList<>();
        Stack<File> directories = new Stack<>();
        directories.add(directory);
        while (!directories.isEmpty()) {
            File dir = directories.pop();
            File[] files = dir.listFiles();
            if (files == null) {
                continue;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    if (!".git".equals(file.getName())) {
                        directories.push(file);
                    }
                } else {
                    includeFileIfSupported(allFiles, file, rootDir, supportedFileExtensions);
                }
            }
        }
        allFiles.sort(String::compareTo);
        return allFiles;
    }

    private StagedJsonForm constructRepositoryFileSelectForm(File repositoryDirectory, UUID repositoryId) {
        Objects.requireNonNull(repositoryDirectory);
        if (!repositoryDirectory.isDirectory()) {
            throw new IllegalStateException(repositoryDirectory.getAbsolutePath() + " is not a directory");
            // TODO exception
        }

        ObjectMapper objectMapper = new ObjectMapper();

        RepositoryFileSelectFormSchema schema = new RepositoryFileSelectFormSchema(listAllFiles(repositoryDirectory));

        // TODO next path
        return new StagedJsonForm(
                objectMapper.valueToTree(schema),
                RepositoryFileSelectFormSchema.loadUiSchema(),
                REPOSITORY_FILE_SELECT_FORM_PATH + repositoryId,
                null);
    }

    @GetMapping(REPOSITORY_FILE_SELECT_FORM_PATH + "{repository}") // TODO use combined constant
    public ResponseEntity<StagedJsonForm> getRepositoryFileSelectForm(@PathVariable("repository") UUID repositoryId) {
        final Future<File> cloningFuture = repositoryRegistry.lookup(repositoryId);
        if (cloningFuture == null) {
            return ResponseEntity.notFound().build();
        }
        return switch (cloningFuture.state()) {
            // TODO: return some helpful information for the user to display on UI
            case FAILED, CANCELLED -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            case SUCCESS ->
                ResponseEntity.ok(constructRepositoryFileSelectForm(cloningFuture.resultNow(), repositoryId));
            case RUNNING -> ResponseEntity.noContent().build();
            case null, default -> {
                log.error("Unknown Future state: {}", cloningFuture.state());
                yield ResponseEntity.internalServerError().build();
            }
        };
    }

    private static String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i < 0) {
            return fileName;
        }
        return fileName.substring(i + 1);
    }

    private String requireAllFilesSameFormat(File[] files) {
        if (files.length == 0) {
            return "";
        }
        final String firstFormat = getExtension(files[0].getName());
        long differCount = Arrays.stream(files)
                .map(File::getName)
                .map(RepositoryController::getExtension)
                .filter(s -> !s.equals(firstFormat))
                .count();
        if (differCount > 0) {
            throw new IllegalStateException("The files have different formats"); // TODO exception
        }
        return firstFormat;
    }

    @PostMapping(REPOSITORY_FILE_SELECT_FORM_PATH + "{repository}")
    public ResponseEntity<?> importRepositoryFiles(
            @PathVariable("repository") UUID repositoryId, HttpServletRequest request) {
        final Future<File> future = repositoryRegistry.lookup(repositoryId);
        Objects.requireNonNull(future); // TODO proper exception for 404
        Map<String, String[]> parameters = request.getParameterMap();
        File[] filesToImport = parameters.values().stream()
                .flatMap(Arrays::stream)
                .map(name -> new File(future.resultNow(), name))
                .toArray(File[]::new);

        final String fileFormat = requireAllFilesSameFormat(filesToImport);

        List<FileImporter> importers = getFileImporters().stream()
                .filter(importer -> importer.getSupportedFileExtensions().contains(fileFormat))
                .toList();

        for (FileImporter importer : importers) {
            try {
                importer.importFiles(filesToImport);
                break; // all imported
            } catch (Exception e) {
                throw new RuntimeException(e); // TODO exception
            }
        }

        return ResponseEntity.ok().build();
    }
}
