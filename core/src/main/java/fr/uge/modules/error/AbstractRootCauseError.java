package fr.uge.modules.error;

import javax.ws.rs.core.Response;
import java.util.function.Function;

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
