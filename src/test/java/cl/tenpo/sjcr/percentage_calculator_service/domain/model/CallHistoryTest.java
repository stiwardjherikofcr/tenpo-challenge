package cl.tenpo.sjcr.percentage_calculator_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CallHistory Domain Model Tests")
class CallHistoryTest {

    @Test
    @DisplayName("Should build CallHistory with required fields")
    void shouldBuildCallHistoryWithRequiredFields() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        assertThat(callHistory.getId()).isNotNull();
        assertThat(callHistory.getEndpoint()).isEqualTo("/api/calculate");
        assertThat(callHistory.getHttpMethod()).isEqualTo("POST");
        assertThat(callHistory.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should build CallHistory with all fields")
    void shouldBuildCallHistoryWithAllFields() {
        CallHistory.CallHistoryId id = CallHistory.CallHistoryId.generate();
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30);
        String params = "{\"amount\":100,\"percentage\":10}";
        String response = "{\"result\":110}";

        CallHistory callHistory = CallHistory.builder()
                .id(id)
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .requestParameters(params)
                .response(response)
                .httpStatusCode(200)
                .timestamp(timestamp)
                .executionTimeMs(150L)
                .build();

        assertThat(callHistory.getId()).isEqualTo(id);
        assertThat(callHistory.getEndpoint()).isEqualTo("/api/calculate");
        assertThat(callHistory.getHttpMethod()).isEqualTo("POST");
        assertThat(callHistory.getRequestParameters()).isEqualTo(params);
        assertThat(callHistory.getResponse()).isEqualTo(response);
        assertThat(callHistory.getHttpStatusCode()).isEqualTo(200);
        assertThat(callHistory.getTimestamp()).isEqualTo(timestamp);
        assertThat(callHistory.getExecutionTimeMs()).isEqualTo(150L);
    }

    @Test
    @DisplayName("Should throw exception when endpoint is null")
    void shouldThrowExceptionWhenEndpointIsNull() {
        assertThatThrownBy(() -> CallHistory.builder()
                .httpMethod("POST")
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Endpoint cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when endpoint is blank")
    void shouldThrowExceptionWhenEndpointIsBlank() {
        assertThatThrownBy(() -> CallHistory.builder()
                .endpoint("  ")
                .httpMethod("POST")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Endpoint cannot be blank");
    }

    @Test
    @DisplayName("Should throw exception when httpMethod is null")
    void shouldThrowExceptionWhenHttpMethodIsNull() {
        assertThatThrownBy(() -> CallHistory.builder()
                .endpoint("/api/calculate")
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("HTTP method cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when httpMethod is blank")
    void shouldThrowExceptionWhenHttpMethodIsBlank() {
        assertThatThrownBy(() -> CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("  ")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP method cannot be blank");
    }

    @Test
    @DisplayName("Should throw exception for invalid HTTP status code")
    void shouldThrowExceptionForInvalidHttpStatusCode() {
        assertThatThrownBy(() -> CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(999)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid HTTP status code");

        assertThatThrownBy(() -> CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(50)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid HTTP status code");
    }

    @Test
    @DisplayName("Should throw exception for negative execution time")
    void shouldThrowExceptionForNegativeExecutionTime() {
        assertThatThrownBy(() -> CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .executionTimeMs(-100L)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Execution time cannot be negative");
    }

    @Test
    @DisplayName("isSuccessful should return true for 2xx status codes")
    void isSuccessfulShouldReturnTrueFor2xxStatusCodes() {
        CallHistory callHistory200 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(200)
                .build();

        CallHistory callHistory201 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(201)
                .build();

        CallHistory callHistory299 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(299)
                .build();

        assertThat(callHistory200.isSuccessful()).isTrue();
        assertThat(callHistory201.isSuccessful()).isTrue();
        assertThat(callHistory299.isSuccessful()).isTrue();
    }

    @Test
    @DisplayName("isSuccessful should return false for non-2xx status codes")
    void isSuccessfulShouldReturnFalseForNon2xxStatusCodes() {
        CallHistory callHistory400 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(400)
                .build();

        CallHistory callHistory500 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(500)
                .build();

        assertThat(callHistory400.isSuccessful()).isFalse();
        assertThat(callHistory500.isSuccessful()).isFalse();
    }

    @Test
    @DisplayName("hasError should return true when error message exists")
    void hasErrorShouldReturnTrueWhenErrorMessageExists() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .errorMessage("Internal server error")
                .build();

        assertThat(callHistory.hasError()).isTrue();
    }

    @Test
    @DisplayName("hasError should return true for 4xx and 5xx status codes")
    void hasErrorShouldReturnTrueFor4xxAnd5xxStatusCodes() {
        CallHistory callHistory400 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(400)
                .build();

        CallHistory callHistory500 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(500)
                .build();

        assertThat(callHistory400.hasError()).isTrue();
        assertThat(callHistory500.hasError()).isTrue();
    }

    @Test
    @DisplayName("hasError should return false for successful calls")
    void hasErrorShouldReturnFalseForSuccessfulCalls() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(200)
                .build();

        assertThat(callHistory.hasError()).isFalse();
    }

    @Test
    @DisplayName("isSuccessful should return false when httpStatusCode is null")
    void isSuccessfulShouldReturnFalseWhenHttpStatusCodeIsNull() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        assertThat(callHistory.isSuccessful()).isFalse();
    }

    @Test
    @DisplayName("hasError should return false when no error message and no error status code")
    void hasErrorShouldReturnFalseWhenNoErrorMessageAndNoErrorStatusCode() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        assertThat(callHistory.hasError()).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        assertThat(callHistory).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        assertThat(callHistory).isNotEqualTo("Not a CallHistory");
    }

    @Test
    @DisplayName("Equality should be based on ID")
    void equalityShouldBeBasedOnId() {
        CallHistory.CallHistoryId id = CallHistory.CallHistoryId.generate();

        CallHistory callHistory1 = CallHistory.builder()
                .id(id)
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        CallHistory callHistory2 = CallHistory.builder()
                .id(id)
                .endpoint("/api/different")
                .httpMethod("GET")
                .build();

        assertThat(callHistory1).isEqualTo(callHistory2);
        assertThat(callHistory1.hashCode()).isEqualTo(callHistory2.hashCode());
    }

    @Test
    @DisplayName("Different IDs should not be equal")
    void differentIdsShouldNotBeEqual() {
        CallHistory callHistory1 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        CallHistory callHistory2 = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .build();

        assertThat(callHistory1).isNotEqualTo(callHistory2);
    }

    @Test
    @DisplayName("toString should contain relevant information")
    void toStringShouldContainRelevantInformation() {
        CallHistory callHistory = CallHistory.builder()
                .endpoint("/api/calculate")
                .httpMethod("POST")
                .httpStatusCode(200)
                .build();

        String toString = callHistory.toString();

        assertThat(toString).contains("CallHistory");
        assertThat(toString).contains("/api/calculate");
        assertThat(toString).contains("POST");
        assertThat(toString).contains("200");
    }

    @Test
    @DisplayName("CallHistoryId should be created from UUID")
    void callHistoryIdShouldBeCreatedFromUuid() {
        UUID uuid = UUID.randomUUID();
        CallHistory.CallHistoryId id = CallHistory.CallHistoryId.of(uuid);

        assertThat(id.getValue()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("CallHistoryId should be created from String")
    void callHistoryIdShouldBeCreatedFromString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        CallHistory.CallHistoryId id = CallHistory.CallHistoryId.of(uuidString);

        assertThat(id.getValue()).isEqualTo(UUID.fromString(uuidString));
    }

    @Test
    @DisplayName("CallHistoryId generate should create unique IDs")
    void callHistoryIdGenerateShouldCreateUniqueIds() {
        CallHistory.CallHistoryId id1 = CallHistory.CallHistoryId.generate();
        CallHistory.CallHistoryId id2 = CallHistory.CallHistoryId.generate();

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("CallHistoryId equality should be based on UUID value")
    void callHistoryIdEqualityShouldBeBasedOnUuidValue() {
        UUID uuid = UUID.randomUUID();
        CallHistory.CallHistoryId id1 = CallHistory.CallHistoryId.of(uuid);
        CallHistory.CallHistoryId id2 = CallHistory.CallHistoryId.of(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("CallHistoryId toString should return UUID string")
    void callHistoryIdToStringShouldReturnUuidString() {
        UUID uuid = UUID.randomUUID();
        CallHistory.CallHistoryId id = CallHistory.CallHistoryId.of(uuid);

        assertThat(id.toString()).isEqualTo(uuid.toString());
    }

    @Test
    @DisplayName("CallHistoryId should throw exception for null value")
    void callHistoryIdShouldThrowExceptionForNullValue() {
        assertThatThrownBy(() -> CallHistory.CallHistoryId.of((UUID) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ID value cannot be null");
    }
}
