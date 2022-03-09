package fr.uge.modules.error;

/**
 * Interface representing custom RootCause errors
 * This ITF is used to retrieve HTTP status response code according to the error and its message
 * This ITF and its implementing classes are prototype based and might be upgraded.
 */
public interface RootCauseError {
    int getStatus();
    String getMessage();
}
