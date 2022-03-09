package fr.uge.modules.error;

/**
 * Class representing a non-found log inside the DB
 * This error can mean two possible situation: the specific log has never been inserted,
 * or might be waiting for its tokenization
 */
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
