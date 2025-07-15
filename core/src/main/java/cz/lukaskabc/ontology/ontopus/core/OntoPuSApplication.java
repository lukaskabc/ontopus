package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ServiceLoader;

@SpringBootApplication
public class OntoPuSApplication {

	public static void main(String[] args) {
		var loader = ServiceLoader.load(Plugin.class);
		for (Plugin<?> pluginProvider : loader) {
			var plugin = pluginProvider.initialize();
			System.out.println("Loaded plugin: " + plugin.getName());
		}
//		SpringApplication.run(OntoPuSApplication.class, args);
	}

}
