package cl.tenpo.sjcr.percentage_calculator_service.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain Exceptions Tests")
class DomainExceptionsTest {

    @Test
    @DisplayName("CacheException should be created with message")
    void shouldCreateCacheExceptionWithMessage() {
        String message = "Cache operation failed";
        CacheException exception = new CacheException(message);

        assertThat(exception).isInstanceOf(DomainException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("CACHE_ERROR");
    }

    @Test
    @DisplayName("CacheException should be created with message and cause")
    void shouldCreateCacheExceptionWithMessageAndCause() {
        String message = "Cache operation failed";
        Throwable cause = new RuntimeException("Underlying cause");
        CacheException exception = new CacheException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("CACHE_ERROR");
    }

    @Test
    @DisplayName("CalculationException should be created with message")
    void shouldCreateCalculationExceptionWithMessage() {
        String message = "Calculation failed";
        CalculationException exception = new CalculationException(message);

        assertThat(exception).isInstanceOf(DomainException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("CALCULATION_ERROR");
    }

    @Test
    @DisplayName("CalculationException should be created with message and cause")
    void shouldCreateCalculationExceptionWithMessageAndCause() {
        String message = "Calculation failed";
        Throwable cause = new ArithmeticException("Division by zero");
        CalculationException exception = new CalculationException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("CALCULATION_ERROR");
    }

    @Test
    @DisplayName("InvalidInputException should be created with message")
    void shouldCreateInvalidInputExceptionWithMessage() {
        String message = "Invalid input provided";
        InvalidInputException exception = new InvalidInputException(message);

        assertThat(exception).isInstanceOf(CalculationException.class);
        assertThat(exception).isInstanceOf(DomainException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_INPUT");
    }

    @Test
    @DisplayName("InvalidInputException should be created with message and cause")
    void shouldCreateInvalidInputExceptionWithMessageAndCause() {
        String message = "Invalid input provided";
        Throwable cause = new IllegalArgumentException("Negative value");
        InvalidInputException exception = new InvalidInputException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_INPUT");
    }

    @Test
    @DisplayName("ExternalServiceException should be created with message")
    void shouldCreateExternalServiceExceptionWithMessage() {
        String message = "External service call failed";
        ExternalServiceException exception = new ExternalServiceException(message);

        assertThat(exception).isInstanceOf(DomainException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("EXTERNAL_SERVICE_ERROR");
    }

    @Test
    @DisplayName("ExternalServiceException should be created with message and cause")
    void shouldCreateExternalServiceExceptionWithMessageAndCause() {
        String message = "External service call failed";
        Throwable cause = new RuntimeException("Network timeout");
        ExternalServiceException exception = new ExternalServiceException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("EXTERNAL_SERVICE_ERROR");
    }

    @Test
    @DisplayName("PercentageServiceUnavailableException should be created with message")
    void shouldCreatePercentageServiceUnavailableExceptionWithMessage() {
        String message = "Percentage service is unavailable";
        PercentageServiceUnavailableException exception = new PercentageServiceUnavailableException(message);

        assertThat(exception).isInstanceOf(ExternalServiceException.class);
        assertThat(exception).isInstanceOf(DomainException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("PERCENTAGE_SERVICE_UNAVAILABLE");
    }

    @Test
    @DisplayName("PercentageServiceUnavailableException should be created with message and cause")
    void shouldCreatePercentageServiceUnavailableExceptionWithMessageAndCause() {
        String message = "Percentage service is unavailable";
        Throwable cause = new RuntimeException("Service down");
        PercentageServiceUnavailableException exception = new PercentageServiceUnavailableException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("PERCENTAGE_SERVICE_UNAVAILABLE");
    }

    @Test
    @DisplayName("All domain exceptions should be RuntimeExceptions")
    void allDomainExceptionsShouldBeRuntimeExceptions() {
        assertThat(new CacheException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new CalculationException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new InvalidInputException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new ExternalServiceException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new PercentageServiceUnavailableException("test")).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Exception error codes should be unique and descriptive")
    void exceptionErrorCodesShouldBeUniqueAndDescriptive() {
        assertThat(new CacheException("test").getErrorCode()).isEqualTo("CACHE_ERROR");
        assertThat(new CalculationException("test").getErrorCode()).isEqualTo("CALCULATION_ERROR");
        assertThat(new InvalidInputException("test").getErrorCode()).isEqualTo("INVALID_INPUT");
        assertThat(new ExternalServiceException("test").getErrorCode()).isEqualTo("EXTERNAL_SERVICE_ERROR");
        assertThat(new PercentageServiceUnavailableException("test").getErrorCode())
                .isEqualTo("PERCENTAGE_SERVICE_UNAVAILABLE");
    }
}
