package fr.uge.modules.error;

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
