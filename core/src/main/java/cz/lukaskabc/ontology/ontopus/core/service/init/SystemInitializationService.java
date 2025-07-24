package cz.lukaskabc.ontology.ontopus.core.service.init;

import java.util.Set;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class SystemInitializationService implements SmartInitializingSingleton {

    private final Set<InitService> initServices;

    public SystemInitializationService(Set<InitService> initServices) {
        this.initServices = initServices;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (InitService initService : initServices) {
            initService.init();
        }
    }
}
