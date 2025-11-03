package cl.tenpo.sjcr.percentage_calculator_service.domain.event;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;

public class CalculationFailureEvent extends CalculationEvent {

    private final String errorMessage;
    private final String errorCode;
    private final String endpoint;
    private final String httpMethod;
    private final Integer httpStatusCode;

    public CalculationFailureEvent(Builder builder) {
        super(builder.request);
        this.errorMessage = builder.errorMessage;
        this.errorCode = builder.errorCode;
        this.endpoint = builder.endpoint;
        this.httpMethod = builder.httpMethod;
        this.httpStatusCode = builder.httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public String toString() {
        return "CalculationFailureEvent{" +
                "request=" + getRequest() +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", timestamp=" + getTimestamp() +
                '}';
    }

    @Override
    public String getEventType() {
        return "CALCULATION_FAILURE";
    }

    public static class Builder {
        private CalculationRequest request;
        private String errorMessage;
        private String errorCode;
        private String endpoint;
        private String httpMethod;
        private Integer httpStatusCode;

        public Builder request(CalculationRequest request) {
            this.request = request;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
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

        public Builder httpStatusCode(Integer httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        public CalculationFailureEvent build() {
            return new CalculationFailureEvent(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
