package cl.tenpo.sjcr.percentage_calculator_service.domain.exception;

public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return "DOMAIN_ERROR";
    }
}
