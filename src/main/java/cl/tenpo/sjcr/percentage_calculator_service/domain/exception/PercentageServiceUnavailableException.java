package cl.tenpo.sjcr.percentage_calculator_service.domain.exception;

public class PercentageServiceUnavailableException extends ExternalServiceException {

    public PercentageServiceUnavailableException(String message) {
        super(message);
    }

    public PercentageServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "PERCENTAGE_SERVICE_UNAVAILABLE";
    }
}
