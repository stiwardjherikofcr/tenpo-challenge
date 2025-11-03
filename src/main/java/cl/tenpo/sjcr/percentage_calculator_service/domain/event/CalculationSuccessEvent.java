package cl.tenpo.sjcr.percentage_calculator_service.domain.event;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;

public class CalculationSuccessEvent extends CalculationEvent {

    private final CalculationResult result;
    private final String endpoint;
    private final String httpMethod;

    private CalculationSuccessEvent(Builder builder) {
        super(builder.request);
        this.result = builder.result;
        this.endpoint = builder.endpoint;
        this.httpMethod = builder.httpMethod;
    }

    public CalculationResult getResult() {
        return result;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String toString() {
        return "CalculationSuccessEvent{" +
                "request=" + getRequest() +
                ", result=" + result +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                '}';
    }

    @Override
    public String getEventType() {
        return "CALCULATION_SUCCESS";
    }

    public static class Builder {
        private CalculationRequest request;
        private CalculationResult result;
        private String endpoint;
        private String httpMethod;

        public Builder request(CalculationRequest request) {
            this.request = request;
            return this;
        }

        public Builder result(CalculationResult result) {
            this.result = result;
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

        public CalculationSuccessEvent build() {
            return new CalculationSuccessEvent(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
