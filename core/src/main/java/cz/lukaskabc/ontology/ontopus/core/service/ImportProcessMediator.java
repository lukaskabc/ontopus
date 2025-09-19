package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.core.factory.ImportProcessContextHolder;
import java.util.concurrent.Future;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class ImportProcessMediator {
    private final ImportProcessContextHolder holder;

    public ImportProcessMediator(ImportProcessContextHolder contextHolder) {
        this.holder = contextHolder;
    }

    public Future<JsonForm> getCurrentForm() {
        return holder.runWithContextNow(Operations::getJsonForm);
    }

    private static final class Operations {
        @Nullable private static JsonForm getJsonForm(ImportProcessContext context) {
            if (context.hasUnprocessedService()) {
                return context.peekService().getJsonForm();
            }
            return null;
        }

        private Operations() {
            throw new AssertionError();
        }
    }
}
