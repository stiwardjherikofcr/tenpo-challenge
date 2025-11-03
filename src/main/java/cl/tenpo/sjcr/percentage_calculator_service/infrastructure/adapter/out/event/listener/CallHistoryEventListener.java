package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.event.listener;

import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationFailureEvent;
import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationSuccessEvent;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.factory.CallHistoryFactory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CallHistoryEventListener {

    private static final Logger log = LoggerFactory.getLogger(CallHistoryEventListener.class);

    private final CallHistoryRepositoryPort repository;
    private final CallHistoryFactory callHistoryFactory;

    public CallHistoryEventListener(
            CallHistoryRepositoryPort repository,
            CallHistoryFactory callHistoryFactory
    ) {
        this.repository = repository;
        this.callHistoryFactory = callHistoryFactory;
    }

    @EventListener
    @Async("asyncHistoryExecutor")
    public void handleCalculationSuccess(CalculationSuccessEvent event) {
        try {
            log.debug("Processing CalculationSuccessEvent: {}", event);

            CallHistory history = callHistoryFactory.createFromSuccess(
                    event.getRequest(),
                    event.getResult(),
                    event.getEndpoint(),
                    event.getHttpMethod(),
                    event.getTimestamp());

            repository.save(history);
            log.info("Call history saved successfully for successful calculation");

        } catch (Exception e) {

            log.error("Failed to save call history for successful calculation", e);
        }
    }

    @EventListener
    @Async("asyncHistoryExecutor")
    public void handleCalculationFailure(CalculationFailureEvent event) {
        try {
            log.debug("Processing CalculationFailureEvent: {}", event);

            CallHistory history = callHistoryFactory.createFromFailure(
                    event.getRequest(),
                    event.getErrorMessage(),
                    event.getHttpStatusCode(),
                    event.getEndpoint(),
                    event.getHttpMethod(),
                    event.getTimestamp());

            repository.save(history);
            log.info("Call history saved successfully for failed calculation");

        } catch (Exception e) {

            log.error("Failed to save call history for failed calculation", e);
        }
    }
}
