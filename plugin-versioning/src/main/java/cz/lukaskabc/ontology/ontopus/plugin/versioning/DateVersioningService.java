package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DateVersioningService implements OntologyVersioningService {
    static final String SEPARATOR = "_";
    private final VersionArtifactRepository artifactRepository;
    private String versionValue = null;

    public DateVersioningService(VersionArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @Override
    public void afterStackPush(ImportProcessContext context) {
        String previousVersion = null;
        VersionArtifactURI latestVersionUri = context.getVersionSeries().getLast();
        if (latestVersionUri != null) {
            previousVersion = artifactRepository.findRequired(latestVersionUri).getVersion();
        }
        versionValue = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (previousVersion != null && previousVersion.startsWith(versionValue)) {
            String suffix = previousVersion.substring(versionValue.length() + 1);
            if (!StringUtils.hasText(suffix)) {
                suffix = "1";
            }
            int value = Integer.parseInt(suffix) + 1;
            versionValue += SEPARATOR + value;
        }
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OntologyVersioningService.DateVersioningService.name";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        context.getVersionArtifact().setVersion(versionValue);
        return null;
    }
}
