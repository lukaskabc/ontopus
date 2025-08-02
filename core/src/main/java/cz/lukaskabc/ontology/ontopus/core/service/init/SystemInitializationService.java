package cz.lukaskabc.ontology.ontopus.core.service.init;

import java.util.Set;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component(SystemInitializationService.BEAN_NAME)
public class SystemInitializationService implements SmartInitializingSingleton {
    protected static final String BEAN_NAME = "systemInitializationService";

    private final Set<InitService> initServices;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public SystemInitializationService(
            Set<InitService> initServices, DefaultListableBeanFactory defaultListableBeanFactory) {
        this.initServices = initServices;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (InitService initService : initServices) {
            initService.init();
        }
        clearSpringContext();
    }

    private void clearSpringContext() {
        String[] names = defaultListableBeanFactory.getBeanNamesForType(InitService.class);
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
