package cz.lukaskabc.ontology.ontopus.core_model.progress;

public enum ProgressState {
    /** The future is scheduled and waiting for execution */
    SCHEDULED,
    /** The future is being executed */
    RUNNING,
    /** The future is completed (successfully, failed or was canceled) */
    COMPLETED
}
