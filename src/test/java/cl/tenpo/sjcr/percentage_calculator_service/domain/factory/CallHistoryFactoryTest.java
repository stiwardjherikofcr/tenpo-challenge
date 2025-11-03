package cl.tenpo.sjcr.percentage_calculator_service.domain.factory;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CallHistory Factory Tests")
class CallHistoryFactoryTest {

    @Mock
    private ObjectMapper objectMapper;

    private CallHistoryFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CallHistoryFactory(objectMapper);
    }

    @Test
    @DisplayName("Should create CallHistory from successful calculation")
    void shouldCreateFromSuccess() throws JsonProcessingException {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();

        CalculationResult result = CalculationResult.builder()
                .sum(new BigDecimal("30"))
                .appliedPercentage(Percentage.of(new BigDecimal("15")))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        String endpoint = "/api/v1/calculate";
        String httpMethod = "POST";
        LocalDateTime timestamp = LocalDateTime.of(2025, 10, 31, 10, 30, 0);

        when(objectMapper.writeValueAsString(request)).thenReturn("{\"num1\":10,\"num2\":20}");
        when(objectMapper.writeValueAsString(result)).thenReturn("{\"result\":34.50}");

        CallHistory callHistory = factory.createFromSuccess(request, result, endpoint, httpMethod, timestamp);

        assertThat(callHistory).isNotNull();
        assertThat(callHistory.getEndpoint()).isEqualTo(endpoint);
        assertThat(callHistory.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(callHistory.getHttpStatusCode()).isEqualTo(200);
        assertThat(callHistory.getRequestParameters()).isEqualTo("{\"num1\":10,\"num2\":20}");
        assertThat(callHistory.getResponse()).isEqualTo("{\"result\":34.50}");
        assertThat(callHistory.getErrorMessage()).isNull();
        assertThat(callHistory.getTimestamp()).isEqualTo(timestamp);
        assertThat(callHistory.isSuccessful()).isTrue();
        assertThat(callHistory.hasError()).isFalse();
    }

    @Test
    @DisplayName("Should create CallHistory from failed calculation")
    void shouldCreateFromFailure() throws JsonProcessingException {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();

        String errorMessage = "Percentage service unavailable";
        int httpStatusCode = 503;
        String endpoint = "/api/v1/calculate";
        String httpMethod = "POST";
        LocalDateTime timestamp = LocalDateTime.of(2025, 10, 31, 10, 30, 0);

        when(objectMapper.writeValueAsString(request)).thenReturn("{\"num1\":10,\"num2\":20}");

        CallHistory callHistory = factory.createFromFailure(
                request, errorMessage, httpStatusCode, endpoint, httpMethod, timestamp);

        assertThat(callHistory).isNotNull();
        assertThat(callHistory.getEndpoint()).isEqualTo(endpoint);
        assertThat(callHistory.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(callHistory.getHttpStatusCode()).isEqualTo(503);
        assertThat(callHistory.getRequestParameters()).isEqualTo("{\"num1\":10,\"num2\":20}");
        assertThat(callHistory.getResponse()).isNull();
        assertThat(callHistory.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(callHistory.getTimestamp()).isEqualTo(timestamp);
        assertThat(callHistory.isSuccessful()).isFalse();
        assertThat(callHistory.hasError()).isTrue();
    }

    @Test
    @DisplayName("Should handle JSON serialization failure gracefully with toString fallback")
    void shouldHandleSerializationFailureGracefully() throws JsonProcessingException {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();

        CalculationResult result = CalculationResult.builder()
                .sum(new BigDecimal("30"))
                .appliedPercentage(Percentage.of(new BigDecimal("15")))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        String endpoint = "/api/v1/calculate";
        String httpMethod = "POST";
        LocalDateTime timestamp = LocalDateTime.of(2025, 10, 31, 10, 30, 0);

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization error") {
        });

        CallHistory callHistory = factory.createFromSuccess(request, result, endpoint, httpMethod, timestamp);

        assertThat(callHistory).isNotNull();
        assertThat(callHistory.getRequestParameters()).isEqualTo(request.toString());
        assertThat(callHistory.getResponse()).isEqualTo(result.toString());
    }

    @Test
    @DisplayName("Should create CallHistory with different HTTP status codes")
    void shouldCreateWithDifferentStatusCodes() throws JsonProcessingException {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();

        when(objectMapper.writeValueAsString(request)).thenReturn("{\"num1\":10,\"num2\":20}");

        CallHistory badRequest = factory.createFromFailure(
                request, "Invalid input", 400, "/api/v1/calculate", "POST", LocalDateTime.now());
        assertThat(badRequest.getHttpStatusCode()).isEqualTo(400);
        assertThat(badRequest.isSuccessful()).isFalse();

        CallHistory serverError = factory.createFromFailure(
                request, "Internal error", 500, "/api/v1/calculate", "POST", LocalDateTime.now());
        assertThat(serverError.getHttpStatusCode()).isEqualTo(500);
        assertThat(serverError.isSuccessful()).isFalse();

        CallHistory serviceUnavailable = factory.createFromFailure(
                request, "Service unavailable", 503, "/api/v1/calculate", "POST", LocalDateTime.now());
        assertThat(serviceUnavailable.getHttpStatusCode()).isEqualTo(503);
        assertThat(serviceUnavailable.isSuccessful()).isFalse();
    }

    @Test
    @DisplayName("Should create CallHistory with different endpoints and methods")
    void shouldCreateWithDifferentEndpointsAndMethods() throws JsonProcessingException {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();

        CalculationResult result = CalculationResult.builder()
                .sum(new BigDecimal("30"))
                .appliedPercentage(Percentage.of(new BigDecimal("15")))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        CallHistory history1 = factory.createFromSuccess(
                request, result, "/api/v1/calculate", "POST", LocalDateTime.now());
        assertThat(history1.getEndpoint()).isEqualTo("/api/v1/calculate");
        assertThat(history1.getHttpMethod()).isEqualTo("POST");

        CallHistory history2 = factory.createFromSuccess(
                request, result, "/api/v2/calculate", "PUT", LocalDateTime.now());
        assertThat(history2.getEndpoint()).isEqualTo("/api/v2/calculate");
        assertThat(history2.getHttpMethod()).isEqualTo("PUT");
    }
}
