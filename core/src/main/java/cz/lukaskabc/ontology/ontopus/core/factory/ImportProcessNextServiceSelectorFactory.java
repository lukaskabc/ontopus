package cz.lukaskabc.ontology.ontopus.core.factory;

import org.springframework.stereotype.Service;

@Service
public class ImportProcessNextServiceSelectorFactory {
    // TODO use or remove?
    // private final ListableBeanFactory beanFactory;
    // private final ObjectMapper objectMapper;
    //
    // public ImportProcessNextServiceSelectorFactory(ListableBeanFactory
    // beanFactory, ObjectMapper objectMapper) {
    // this.beanFactory = beanFactory;
    // this.objectMapper = objectMapper;
    // }
    //
    // public <R, T extends ImportProcessingService<R>>
    // ImportProcessNextServiceSelector<T> forService(Class<T> clazz) {
    // return new ImportProcessNextServiceSelector<>(
    // new ArrayList<>(beanFactory.getBeansOfType(clazz).values()), objectMapper);
    // }
}
