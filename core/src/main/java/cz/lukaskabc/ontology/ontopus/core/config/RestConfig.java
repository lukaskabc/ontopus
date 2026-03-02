package cz.lukaskabc.ontology.ontopus.core.config;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.io.IOException;

@NullMarked
@Configuration
public class RestConfig implements WebMvcConfigurer {
    private final FileSystemResource frontendIndexFile;

    public RestConfig(OntopusConfig config) {
        this.frontendIndexFile =
                new FileSystemResource(config.getFrontendIndexFile().getAbsolutePath());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String frontendDirectory = frontendIndexFile.getFile().getParentFile().getAbsolutePath() + File.separator;

        registry.addResourceHandler("/admin/", "/admin/**")
                .addResourceLocations(new FileSystemResource(frontendDirectory))
                .resourceChain(true)
                .addResolver(new IndexFallbackPathResolver());
    }

    private class IndexFallbackPathResolver extends PathResourceResolver {

        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            // Attempt to resolve the actual requested resource (e.g., .js, .css, .png)
            Resource requestedResource = location.createRelative(resourcePath);

            // If the file exists, return it
            if (requestedResource.exists()) {
                return requestedResource;
            }

            // Fallback: If the file doesn't exist (it's a frontend route), return
            // index.html
            return frontendIndexFile;
        }
    }
}
