package fr.uge.modules.error;

public class NotYetTokenizedError extends AbstractRootCauseError {
    @Override
    public int getStatus() {
        return 404;
    }

    @Override
    public String getMessage() {
        return "Log not yet tokenized";
    }
}
