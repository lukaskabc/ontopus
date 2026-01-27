package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyArtifactBuildingService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class PropertyMappingArtifactBuildingService
        implements OntologyArtifactBuildingService, OrderedImportPipelineService<Void> {
    private final List<ArtifactPropertyMappingProviderFactory> providerFactories;

    public PropertyMappingArtifactBuildingService(List<ArtifactPropertyMappingProviderFactory> providerFactories) {
        this.providerFactories = providerFactories;
    }

    /**
     * Supplies the value from {@code supplier} to {@code consumer} if the current value returned by
     * {@code currentValueSupplier} is null.
     */
    private <T> void applyMapping(
            Supplier<@Nullable T> supplier, Consumer<@NonNull T> consumer, Supplier<@Nullable T> currentValueSupplier) {
        if (currentValueSupplier.get() == null) {
            Optional.ofNullable(supplier.get()).ifPresent(consumer);
        }
    }

    private void applyProvider(ArtifactPropertyMappingProvider provider, ImportProcessContext context) {
        final VersionArtifact artifact = context.getVersionArtifact();
        applyMapping(provider::resolveTitle, artifact::setTitle, artifact::getTitle);
        applyMapping(provider::resolveDescription, artifact::setDescription, artifact::getDescription);
        applyMapping(provider::resolveLanguages, artifact::setLanguages, artifact::getLanguages);
        applyMapping(provider::resolveVersion, artifact::setVersion, artifact::getVersion);
        applyMapping(provider::resolveReleaseDate, artifact::setReleaseDate, artifact::getReleaseDate);
        applyMapping(provider::resolveModifiedDate, artifact::setModifiedDate, artifact::getModifiedDate);
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
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
