package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * Finalizing services are executed at the end of the import process. All finalizing services are executed as a part of
 * the same database transaction. The current transaction should not be suspended by the service. The service cannot
 * receive input from the user.
 *
 * <p>Services are ordered using the {@link org.springframework.core.annotation.Order @Order} annotation. In case of
 * {@link TransactionSynchronization} implementation, the {@link TransactionSynchronization#getOrder()} applies only to
 * the transaction synchronization and does not affect the order of execution of the finalizing services.
 *
 * @implNote The service may implement {@link TransactionSynchronization} to receive callbacks related to the
 *     transaction of the import process. This may be used to perform actions after the transaction is committed or
 *     rolled back.
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface ImportFinalizingService {
    void finalizeImport(ImportProcessContext context);
}
