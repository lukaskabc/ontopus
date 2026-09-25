package cz.lukaskabc.ontology.ontopus.tests.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

public abstract class TransactionalRunner {
    @Autowired
    protected PlatformTransactionManager txManager;

    protected void readOnlyTransactional(Runnable procedure) {
        TransactionTemplate transaction = new TransactionTemplate(txManager);
        transaction.setReadOnly(true);
        transaction.executeWithoutResult(_ -> procedure.run());
    }

    protected <T> T readOnlyTransactional(Supplier<T> procedure) {
        TransactionTemplate transaction = new TransactionTemplate(txManager);
        transaction.setReadOnly(true);
        return transaction.execute(_ -> procedure.get());
    }

    protected void transactional(Runnable procedure) {
        new TransactionTemplate(txManager).executeWithoutResult(_ -> procedure.run());
    }
}
