package cz.lukaskabc.ontology.ontopus.core.service.resource_fallback;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TrailingSlashFallback implements ResourceRequestFallback {
    private final OntopusConfig.Resource resourceConfig;

    public TrailingSlashFallback(OntopusConfig ontopusConfig) {
        this.resourceConfig = ontopusConfig.getResource();
    }

    @Override
    public void accept(UriComponentsBuilder builder) {
        UriComponents components = builder.build();
        final String path = components.getPath();
        if (path == null) {
            return;
        }

        if (resourceConfig.isTrailingSlashFallsBackToNoSlash() && path.endsWith("/")) {
            final String withoutSlash = StringUtils.withoutTrailingSlash(path);
            builder.replacePath(withoutSlash);
        } else if (resourceConfig.isNoSlashFallsBackToTrailingSlash() && !path.endsWith("/")) {
            builder.replacePath(path + "/");
        }
    }
}
