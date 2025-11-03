package cl.tenpo.sjcr.percentage_calculator_service.domain.exception;

public class CalculationException extends DomainException {

    public CalculationException(String message) {
        super(message);
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "CALCULATION_ERROR";
    }
}
