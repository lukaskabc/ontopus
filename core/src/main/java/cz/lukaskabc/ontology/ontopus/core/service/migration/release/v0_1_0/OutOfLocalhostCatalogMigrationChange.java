package cz.lukaskabc.ontology.ontopus.core.service.migration.release.v0_1_0;

import cz.cvut.kbss.jopa.model.annotations.Context;
import cz.cvut.kbss.model.change.custom.CustomChange;
import cz.cvut.kbss.repository.OntologyRepository;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog_;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom change capable of migrating catalog and related resource identifiers.
 *
 * <p>Requires {@code ONTOPUS_SYSTEM_URL} and {@link #ONTOPUS_CATALOG_PREFIX_MIGRATION_TARGET} environment variables set
 * to the original identifier prefix and the new prefix.
 */
public class OutOfLocalhostCatalogMigrationChange implements CustomChange {
    /** The new prefix to use in entity identifiers. */
    private static final String ONTOPUS_CATALOG_PREFIX_MIGRATION_TARGET = "ONTOPUS_CATALOG_PREFIX_MIGRATION_TARGET";

    static final String ONTOPUS_SYSTEM_URI = "ONTOPUS_SYSTEM_URI";

    private static final String BASE_PACKAGE = "cz.lukaskabc.ontology.ontopus";
    private static final Logger log = LogManager.getLogger(OutOfLocalhostCatalogMigrationChange.class);

    static URI getEnvUri(String envVar) {
        try {
            return UriComponentsBuilder.fromUriString(System.getenv(envVar))
                    .path("/")
                    .build()
                    .toUri();
        } catch (Exception e) {
            throw new CatalogMigrationException(
                    "Invalid or missing URI for environment variable: " + envVar
                            + "\n Please refer to the v0.1.0 release notes: https://github.com/lukaskabc/ontopus/releases/tag/v0.1.0");
        }
    }

    private static Set<URI> resolveContexts() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Context.class));
        return provider.findCandidateComponents(BASE_PACKAGE).stream()
                .map(BeanDefinition::getBeanClassName)
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new CatalogMigrationException("Class not found: " + className);
                    }
                })
                .map(clazz -> clazz.getAnnotation(Context.class))
                .map(Context::value)
                .map(URI::create)
                .collect(Collectors.toSet());
    }

    @Override
    public void apply(OntologyRepository ontologyRepository) {
        if (!catalogExists(ontologyRepository)) {
            log.warn("Catalog not found, skipping catalog identifier migration");
            return;
        }
        final URI source = getEnvUri(ONTOPUS_SYSTEM_URI).resolve("/dcat/");
        final URI target = getEnvUri(ONTOPUS_CATALOG_PREFIX_MIGRATION_TARGET);
        log.warn("Performing catalog migration from identifier prefix <{}> to <{}>", source, target);
        final Set<URI> contexts = resolveContexts();
        if (contexts.isEmpty()) {
            throw new CatalogMigrationException("No contexts for migration found!");
        }
        log.warn(
                "Selected database graphs for identifier prefix migration: <{}>",
                contexts.stream().map(URI::toString).collect(Collectors.joining(">, <")));

        mergeLocalCatalog(target, ontologyRepository);

        for (URI graph : contexts) {
            log.info("Performing identifier prefix migration in graph <{}>", graph);
            runReplacement(graph, source, target, ontologyRepository);
            runReplacement(
                    graph, Vocabulary.u_c_ontopus_VersionSeries, target.resolve("version-series"), ontologyRepository);
            runReplacement(
                    graph,
                    Vocabulary.u_c_ontopus_VersionArtifact,
                    target.resolve("version-artifact"),
                    ontologyRepository);
        }
    }

    private boolean catalogExists(OntologyRepository repository) {
        return repository.ask("""
				ASK {
				    GRAPH <?catalog> {
				        ?s a <?catalog> .
				    }
				}
				""".replace("?catalog", OntopusCatalog_.entityClassIRI.toString()));
    }

    private void mergeLocalCatalog(URI target, OntologyRepository ontologyRepository) {
        ontologyRepository.update(
                """
				PREFIX dcat: <http://www.w3.org/ns/dcat#>
				PREFIX localhost: <http://localhost/ontopus/>
				PREFIX ontopus: <http://ontology.lukaskabc.cz/application/ontopus/>

				DELETE {
				    GRAPH ontopus:OntopusCatalog {
				        localhost:catalog dcat:dataset ?o .
				    }
				}
				INSERT {
				    GRAPH ontopus:OntopusCatalog {
				        <?newCatalog> dcat:dataset ?o .
				    }
				}
				WHERE {
				  localhost:catalog dcat:dataset ?o .

				  FILTER NOT EXISTS {
				    <?newCatalog> dcat:dataset ?o .
				  }
				}
				""".replace("?newCatalog", target.resolve("catalog").toString()));
    }

    private void replaceObjects(OntologyRepository ontologyRepository, Replacement replacement) {
        final String sparqlUpdateObjects = """
				DELETE {
				    GRAPH <?graph> {
				        ?s ?p ?o .
				    }
				}
				INSERT {
				    GRAPH <?graph> {
				        ?s ?p ?newO .
				    }
				}
				WHERE {
				    GRAPH <?graph> {
				        {
				            ?s ?p ?o .
				            FILTER (isIRI(?o) && STRSTARTS(STR(?o), "?sourcePrefix")) .
				            BIND(IRI(CONCAT("?targetPrefix", SUBSTR(STR(?o), ?sourceLen))) AS ?newO)
				        } UNION {
				            ?s ?p ?o .
				            FILTER(isLITERAL(?o) && ?p = <?identifier> && STRSTARTS(STR(?o), "?sourcePrefix")) .
				            BIND(CONCAT("?targetPrefix", SUBSTR(STR(?o), ?sourceLen)) AS ?newO)
				        }
				    }
				}
				""".replace(
                        "?graph", replacement.graph().toString())
                .replace("?sourcePrefix", replacement.source().toString())
                .replace("?targetPrefix", replacement.target().toString())
                .replace("?identifier", Vocabulary.s_p_dcterms_identifier)
                .replace("?sourceLen", String.valueOf(replacement.sourceSparqlLength()));

        ontologyRepository.update(sparqlUpdateObjects);
    }

    private void replaceSubjects(OntologyRepository ontologyRepository, Replacement replacement) {
        final String sparqlUpdateSubjects = """
				DELETE {
				    GRAPH <?graph> {
				        ?s ?p ?o .
				    }
				}
				INSERT {
				    GRAPH <?graph> {
				        ?newS ?p ?o .
				    }
				}
				WHERE {
				    GRAPH <?graph> {
				        ?s ?p ?o .
				        FILTER (isIRI(?s) && STRSTARTS(STR(?s), "?sourcePrefix")) .
				        BIND(IRI(CONCAT("?targetPrefix", SUBSTR(STR(?s), ?sourceLen))) AS ?newS)
				    }
				}
				""".replace(
                        "?graph", replacement.graph().toString())
                .replace("?sourcePrefix", replacement.source().toString())
                .replace("?targetPrefix", replacement.target().toString())
                .replace("?sourceLen", String.valueOf(replacement.sourceSparqlLength()));

        ontologyRepository.update(sparqlUpdateSubjects);
    }

    private void runReplacement(URI graph, URI source, URI target, OntologyRepository ontologyRepository) {
        final Replacement replacement = new Replacement(graph, source, target);
        replaceSubjects(ontologyRepository, replacement);
        replaceObjects(ontologyRepository, replacement);
    }

    private static class CatalogMigrationException extends RuntimeException {
        public CatalogMigrationException(String message) {
            super(message);
        }
    }

    private record Replacement(URI graph, URI source, URI target) {
        public int sourceSparqlLength() {
            return source.toString().length() + 1;
        }
    }
}
