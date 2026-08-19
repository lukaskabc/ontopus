package cz.lukaskabc.ontology.ontopus.plugin.alias.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.plugin.alias.config.URIAliasPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service.AliasService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/** On each start, creates URI aliases from {@link #globalAliases} */
@Service
public class GlobalAliasesInitializationService implements InitializationService {

    private static final Logger log = LogManager.getLogger(GlobalAliasesInitializationService.class);
    private final AliasService aliasService;
    private final URIAliasPluginConfig config;

    public GlobalAliasesInitializationService(AliasService aliasService, URIAliasPluginConfig config) {
        this.aliasService = aliasService;
        this.config = config;
    }

    @Override
    public void initialize() {
        if (!config.getGlobalAliases().isEmpty()) {
            log.debug("Registering global URI aliases...");
            config.getGlobalAliases().forEach(aliasService::createTemporaryMapping);
        }
    }
}
