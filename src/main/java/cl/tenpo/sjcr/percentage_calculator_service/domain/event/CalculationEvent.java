package cl.tenpo.sjcr.percentage_calculator_service.domain.event;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;

import java.time.LocalDateTime;

public abstract class CalculationEvent {

    private final CalculationRequest request;
    private final LocalDateTime timestamp;

    protected CalculationEvent(CalculationRequest request) {
        this.request = request;
        this.timestamp = LocalDateTime.now();
    }

    public CalculationRequest getRequest() {
        return request;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public abstract String getEventType();

}
