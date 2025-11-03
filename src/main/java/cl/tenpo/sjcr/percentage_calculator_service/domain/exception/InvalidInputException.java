package cl.tenpo.sjcr.percentage_calculator_service.domain.exception;

public class InvalidInputException extends CalculationException {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_INPUT";
    }
}
