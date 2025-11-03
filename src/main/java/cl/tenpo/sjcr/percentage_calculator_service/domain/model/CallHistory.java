package cl.tenpo.sjcr.percentage_calculator_service.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CallHistory {

    private final CallHistoryId id;
    private final String endpoint;
    private final String httpMethod;
    private final String requestParameters;
    private final String response;
    private final Integer httpStatusCode;
    private final String errorMessage;
    private final LocalDateTime timestamp;
    private final Long executionTimeMs;

    private CallHistory(Builder builder) {
        this.id = builder.id != null ? builder.id : CallHistoryId.generate();
        this.endpoint = Objects.requireNonNull(builder.endpoint, "Endpoint cannot be null");
        this.httpMethod = Objects.requireNonNull(builder.httpMethod, "HTTP method cannot be null");
        this.requestParameters = builder.requestParameters;
        this.response = builder.response;
        this.httpStatusCode = builder.httpStatusCode;
        this.errorMessage = builder.errorMessage;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.executionTimeMs = builder.executionTimeMs;

        validateInvariants();
    }

    private void validateInvariants() {
        if (endpoint.isBlank()) {
            throw new IllegalArgumentException("Endpoint cannot be blank");
        }

        if (httpMethod.isBlank()) {
            throw new IllegalArgumentException("HTTP method cannot be blank");
        }

        if (httpStatusCode != null && (httpStatusCode < 100 || httpStatusCode > 599)) {
            throw new IllegalArgumentException("Invalid HTTP status code: " + httpStatusCode);
        }

        if (executionTimeMs != null && executionTimeMs < 0) {
            throw new IllegalArgumentException("Execution time cannot be negative");
        }
    }

    public boolean isSuccessful() {
        return httpStatusCode != null && httpStatusCode >= 200 && httpStatusCode < 300;
    }

    public boolean hasError() {
        return errorMessage != null ||
                (httpStatusCode != null && httpStatusCode >= 400);
    }

    public CallHistoryId getId() {
        return id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestParameters() {
        return requestParameters;
    }

    public String getResponse() {
        return response;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CallHistory that = (CallHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CallHistory{" +
                "id=" + id +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", requestParameters='" + requestParameters + '\'' +
                ", response='" + response + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", timestamp=" + timestamp +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }

    public static class Builder {
        private CallHistoryId id;
        private String endpoint;
        private String httpMethod;
        private String requestParameters;
        private String response;
        private Integer httpStatusCode;
        private String errorMessage;
        private LocalDateTime timestamp;
        private Long executionTimeMs;

        public Builder id(CallHistoryId id) {
            this.id = id;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder requestParameters(String parameters) {
            this.requestParameters = parameters;
            return this;
        }

        public Builder response(String response) {
            this.response = response;
            return this;
        }

        public Builder httpStatusCode(Integer statusCode) {
            this.httpStatusCode = statusCode;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder executionTimeMs(Long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
            return this;
        }

        public CallHistory build() {
            return new CallHistory(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class CallHistoryId {
        private final UUID value;

        private CallHistoryId(UUID value) {
            this.value = Objects.requireNonNull(value, "ID value cannot be null");
        }

        public static CallHistoryId of(UUID value) {
            return new CallHistoryId(value);
        }

        public static CallHistoryId of(String value) {
            return new CallHistoryId(UUID.fromString(value));
        }

        public static CallHistoryId generate() {
            return new CallHistoryId(UUID.randomUUID());
        }

        public UUID getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CallHistoryId that = (CallHistoryId) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
