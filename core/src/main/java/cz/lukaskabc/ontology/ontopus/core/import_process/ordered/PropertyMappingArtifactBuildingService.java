package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyArtifactBuildingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(ImportProcessServiceOrder.ARTIFACT_BUILDING_SERVICE)
public class PropertyMappingArtifactBuildingService
        implements OntologyArtifactBuildingService, OrderedImportPipelineService<Void> {
    private final List<ArtifactPropertyMappingProviderFactory> providerFactories;

    public PropertyMappingArtifactBuildingService(List<ArtifactPropertyMappingProviderFactory> providerFactories) {
        this.providerFactories = providerFactories;
    }

    private void applyProvider(ArtifactPropertyMappingProvider provider, ImportProcessContext context) {
        final VersionArtifact artifact = context.getVersionArtifact();
        PropertyMapper.applyMappingWhenNull(provider::resolveTitle, artifact::setTitle, artifact::getTitle);
        PropertyMapper.applyMappingWhenNull(
                provider::resolveDescription, artifact::setDescription, artifact::getDescription);
        PropertyMapper.applyMappingWhenNull(provider::resolveLanguages, artifact::setLanguages, artifact::getLanguages);
        PropertyMapper.applyMappingWhenNull(provider::resolveVersion, artifact::setVersion, artifact::getVersion);
        PropertyMapper.applyMappingWhenNull(
                provider::resolveReleaseDate, artifact::setReleaseDate, artifact::getReleaseDate);
        PropertyMapper.applyMappingWhenNull(
                provider::resolveModifiedDate, artifact::setModifiedDate, artifact::getModifiedDate);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context) {
        return null;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OntologyArtifactBuildingService.PropertyMappingArtifactBuildingService.name";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        for (final ArtifactPropertyMappingProviderFactory providerFactory : providerFactories) {
            final ArtifactPropertyMappingProvider provider = providerFactory.getProvider(context);
            applyProvider(provider, context);
        }
        return null;
    }
}
