package fr.uge.modules.error;

public abstract class AbstractRootCauseError extends RuntimeException implements RootCauseError {
    public AbstractRootCauseError() {
        super();
    }
}
