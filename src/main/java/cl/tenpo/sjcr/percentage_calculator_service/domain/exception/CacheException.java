package cl.tenpo.sjcr.percentage_calculator_service.domain.exception;

public class CacheException extends DomainException {

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "CACHE_ERROR";
    }
}
