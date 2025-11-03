package cl.tenpo.sjcr.percentage_calculator_service.domain.exception;

public class ExternalServiceException extends DomainException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "EXTERNAL_SERVICE_ERROR";
    }
}
