package fr.uge.modules.error;

/**
 * Class representing an empty report generation
 * An EmptyReportError is handy to avoid NPE on an empty generated report
 * Most of the time this error is caused by the report generation parameters,
 * meaning that the returned status code might not be 500 (server error) but a 400 one.
 */
public class EmptyReportError extends AbstractRootCauseError{

    @Override
    public int getStatus() {
        return 500;
    }

    @Override
    public String getMessage() {
        return "Empty report generated. Try to fix your report parameters";
    }
}
