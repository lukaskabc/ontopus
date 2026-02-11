package cz.lukaskabc.ontology.ontopus.core.config;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core.util.MultilingualStringMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import tools.jackson.databind.module.SimpleModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class SystemConfig {
    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public SimpleModule multilingualStringModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(MultilingualString.class, new MultilingualStringMapper());
        return module;
    }
}
