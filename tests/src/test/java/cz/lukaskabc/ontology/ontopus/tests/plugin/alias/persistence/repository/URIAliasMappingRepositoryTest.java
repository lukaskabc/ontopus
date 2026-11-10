package cz.lukaskabc.ontology.ontopus.tests.plugin.alias.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.dao.URIAliasMappingDao;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.repository.URIAliasMappingRepository;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import cz.lukaskabc.ontology.ontopus.tests.util.EntityGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

@Import({URIAliasMappingRepository.class, URIAliasMappingDao.class})
public class URIAliasMappingRepositoryTest extends BaseDaoTest {
    @Autowired
    URIAliasMappingRepository sut;

    @Test
    void replaceAllRemovesExistingMappingsAndPersistsNewOnes() {
        List<URIAliasMapping> existingMappings = EntityGenerator.aliasMappings();
        existingMappings.forEach(sut::save);

        assertThat(sut.findAll()).containsExactlyInAnyOrderElementsOf(existingMappings);

        List<URIAliasMapping> newMappings = EntityGenerator.aliasMappings();
        sut.replaceAll(Set.copyOf(newMappings));

        assertThat(sut.findAll()).containsExactlyInAnyOrderElementsOf(newMappings);
    }
}
