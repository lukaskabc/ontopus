package cz.lukaskabc.ontology.ontopus.core.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component(SystemInitializationService.BEAN_NAME)
public class SystemInitializationService implements SmartInitializingSingleton {
    protected static final String BEAN_NAME = "systemInitializationService";

    private final Set<InitializationService> initServices;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public SystemInitializationService(
            Set<InitializationService> initServices, DefaultListableBeanFactory defaultListableBeanFactory) {
        this.initServices = initServices;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (InitializationService initService : initServices) {
            initService.initialize();
        }
        clearSpringContext();
    }

    private void clearSpringContext() {
        String[] names = defaultListableBeanFactory.getBeanNamesForType(InitializationService.class);
        for (String beanName : names) {
            removeBean(beanName);
        }
        removeBean(BEAN_NAME);
    }

    private void removeBean(String beanName) {
        defaultListableBeanFactory.destroySingleton(beanName);
        defaultListableBeanFactory.removeBeanDefinition(beanName);
    }
}
