package cz.lukaskabc.ontology.ontopus.core.service.migration.release.v0_1_0;

import cz.cvut.kbss.model.change.custom.CustomChange;
import cz.cvut.kbss.repository.OntologyRepository;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;

import java.net.URI;

/**
 * Checks whether the localhost based catalog exists and the newer system URI based as well. If the localhost exists but
 * the systemURI does not, the catalog is too old in order to proceed in migration
 */
public class EnsureSystemUriBasedCatalogExists implements CustomChange {
    @Override
    public void apply(OntologyRepository ontologyRepository) {
        final URI systemUri =
                OutOfLocalhostCatalogMigrationChange.getEnvUri(OutOfLocalhostCatalogMigrationChange.ONTOPUS_SYSTEM_URI);
        final boolean localhostExists =
                ontologyRepository.ask("ASK { GRAPH <?catalog> { <http://localhost/ontopus/catalog> a <?catalog> . } }"
                        .replace("?catalog", Vocabulary.s_c_ontopus_OntopusCatalog));
        final boolean systemUriBasedExists =
                ontologyRepository.ask("ASK { GRAPH <?catalog> { <?identifier> a <?catalog> . } }"
                        .replace("?catalog", Vocabulary.s_c_ontopus_OntopusCatalog)
                        .replace(
                                "?identifier", systemUri.resolve("dcat/catalog").toString()));
        if (localhostExists && !systemUriBasedExists) {
            throw new IllegalStateException("Internal DCAT Catalog is too old, update to version 0.0.17 first.");
        }
    }
}
