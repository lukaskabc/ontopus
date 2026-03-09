package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

/** Copies information from version artifact to version series if the version series is blank */
@Service
@Order(ImportProcessServiceOrder.SERIES_BUILDING_SERVICE)
public class PropertyMappingSeriesBuildingService implements OrderedImportPipelineService<Void> {

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        final VersionSeries series = context.getVersionSeries();
        final VersionArtifact artifact = context.getVersionArtifact();

        PropertyMapper.applyMappingWhenNull(artifact::getTitle, series::setTitle, series::getTitle);
        PropertyMapper.applyMappingWhenNull(artifact::getDescription, series::setDescription, series::getDescription);
        PropertyMapper.applyMappingWhenNull(series::getIdentifier, series::setIdentifier, series::getIdentifier);
        series.setLanguages(artifact.getLanguages());
        return null;
    }
}
