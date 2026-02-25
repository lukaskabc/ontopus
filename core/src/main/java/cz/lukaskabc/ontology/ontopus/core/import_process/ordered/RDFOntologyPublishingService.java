package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.core.EndpointRegistrationService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.persistence.ContextDao;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

// TODO move to a plugin

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class RDFOntologyPublishingService implements OrderedImportPipelineService<Void> {
    private static Map<String, Set<String>> mapByHost(Collection<URI> uris) {
        Map<String, Set<String>> map = new HashMap<>();
        uris.forEach(uri -> {
            final String stringUri = uri.toString();
            map.computeIfAbsent(uri.getHost(), k -> new HashSet<>()).add(stringUri);
        });
        return map;
    }

    private final ContextDao contextDao;

    private final EndpointRegistrationService endpointRegistrationService;

    public RDFOntologyPublishingService(
            ContextDao contextDao, EndpointRegistrationService endpointRegistrationService) {
        this.contextDao = contextDao;
        this.endpointRegistrationService = endpointRegistrationService;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context) {
        return null;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        List<URI> subjects = contextDao.findAllSubjects(context.getDatabaseContext());
        // TODO: subjects may be possible very large?
        Map<String, Set<String>> map = mapByHost(subjects);
        // map.forEach((host, paths) -> {
        // EndpointRegistrationInfo info =
        // EndpointRegistrationInfo.builder().host(host).paths(paths).build();
        //
        // endpointRegistrationService.register(info);
        // });
        return null;
    }
}
