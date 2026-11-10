package cz.lukaskabc.ontology.ontopus.tests.util;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core.CorePlugin;
import cz.lukaskabc.ontology.ontopus.core.PluginRegistryBeanRegistryPostProcessor;
import cz.lukaskabc.ontology.ontopus.plugin.file.FileUploadPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.rdf.RDFPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.versioning.VersioningPlugin;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({LoadPlugins.PluginsRegistrar.class})
public @interface LoadPlugins {
    Set<Plugin> REQUIRED_PLUGINS =
            Set.of(new CorePlugin(), new RDFPlugin(), new FileUploadPlugin(), new VersioningPlugin());

    Class<? extends Plugin>[] value();

    class PluginsRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            Map<String, Object> attrs = metadata.getAnnotationAttributes(LoadPlugins.class.getName(), false);
            if (attrs == null) {
                return;
            }
            Set<Plugin> plugins = new HashSet<>(REQUIRED_PLUGINS);
            for (Class<?> type : (Class<?>[]) attrs.get("value")) {
                plugins.add((Plugin) BeanUtils.instantiateClass(type));
            }

            registry.registerBeanDefinition(
                    "pluginRegistry",
                    BeanDefinitionBuilder.genericBeanDefinition(PluginRegistryBeanRegistryPostProcessor.class)
                            .addConstructorArgValue(Collections.unmodifiableSet(plugins))
                            .getBeanDefinition());
        }
    }
}
