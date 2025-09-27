package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessNextServiceSelector;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

@Service
public class ImportProcessNextServiceSelectorFactory {
    private static ImportProcessingService<?> orElseThrow() {
        throw new RuntimeException(); // TODO exception
    }

    private final ListableBeanFactory beanFactory;

    public ImportProcessNextServiceSelectorFactory(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public <T extends ImportProcessingService<?>> ImportProcessNextServiceSelector<T> forService(Class<T> clazz) {
        return new ImportProcessNextServiceSelector<>(
                beanFactory.getBeansOfType(clazz).values());
    }
}
