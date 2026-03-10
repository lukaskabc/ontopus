package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.ServiceAwareFormResult;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormDataDto;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Serializes the {@link ImportProcessContext} into a {@link SerializableImportProcessContext}. */
@Service
@Order(FinalizationServiceOrder.CONTEXT_SERIALIZATION)
public class ContextSerializationFinalizationService implements ImportFinalizingService {
    private static final Logger log = LogManager.getLogger(ContextSerializationFinalizationService.class);

    private final ObjectMapper objectMapper;

    public ContextSerializationFinalizationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void finalizeImport(ImportProcessContext context) {
        final SerializableImportProcessContext serializedContext = new SerializableImportProcessContext();
        Objects.requireNonNull(context.getVersionSeries().getOntologyURI(), "Ontology URI must not be null");

        final int servicesCount = context.getProcessedServices().size();
        final int resultsCount = context.getProcessedResults().size();
        if (servicesCount < resultsCount) {
            throw new IllegalStateException("There are more processed results than processed services");
        }

        final Map<String, FormDataDto> serviceToFormResultMap = new HashMap<>(resultsCount);

        serializedContext.setServiceToFormResultMap(serviceToFormResultMap);

        int resultIndex = 0;
        for (int serviceIndex = 0; serviceIndex < servicesCount; serviceIndex++) {
            final ImportProcessingService<?> service =
                    context.getProcessedServices().get(serviceIndex);
            final String serviceId = service.getUniqueContextIdentifier(context);
            final ServiceAwareFormResult result = context.getProcessedResults().get(resultIndex);
            if (result.service() == service) {
                FormDataDto serializedFormData =
                        serializeFormData(result.formResult().formData());
                if (serviceToFormResultMap.containsKey(serviceId)) {
                    throw new IllegalStateException("Duplicate service identifier: " + serviceId);
                }

                serviceToFormResultMap.put(serviceId, serializedFormData);
                resultIndex++;
            }
        }

        context.getVersionSeries().setSerializableImportProcessContext(serializedContext);

        log.warn(() -> objectMapper.writeValueAsString(serializedContext));
    }

    private FormDataDto serializeFormData(Map<String, JsonNode> stringJsonNodeMap) {
        FormDataDto data = new FormDataDto(stringJsonNodeMap.size());
        for (Map.Entry<String, JsonNode> entry : stringJsonNodeMap.entrySet()) {
            final String value = objectMapper.writeValueAsString(entry.getValue());
            data.put(entry.getKey(), value);
        }
        return data;
    }
}
