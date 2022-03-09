package fr.uge.modules.error;

import javax.ws.rs.core.Response;

/**
 * Abstract object representing a custom RootCause error, extending RuntimeException in order to throw those
 * without modifying method's signature with classic throwable errors.
 */
public abstract class AbstractRootCauseError extends RuntimeException implements RootCauseError {
    AbstractRootCauseError() {
        super();
    }

    public static Response fromError(RootCauseError rootCauseError) {
        return Response
                .status(rootCauseError.getStatus())
                .entity(rootCauseError.getMessage())
                .build();
    }
}
