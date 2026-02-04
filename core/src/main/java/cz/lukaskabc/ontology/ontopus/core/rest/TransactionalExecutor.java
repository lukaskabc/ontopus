package cz.lukaskabc.ontology.ontopus.core.rest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class TransactionalExecutor {
    @Transactional
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Transactional
    public <T> T execute(Supplier<T> supplier) {
        return supplier.get();
    }
}
