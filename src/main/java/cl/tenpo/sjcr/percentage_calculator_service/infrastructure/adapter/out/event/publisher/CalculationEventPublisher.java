package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.event.publisher;

import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationFailureEvent;
import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationSuccessEvent;
import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.DomainException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CalculationEventPort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context.HttpRequestContext;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context.HttpRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CalculationEventPublisher implements CalculationEventPort {

    private static final Logger log = LoggerFactory.getLogger(CalculationEventPublisher.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final HttpRequestContextProvider contextProvider;

    public CalculationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher,
            HttpRequestContextProvider contextProvider) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.contextProvider = contextProvider;
    }

    @Override
    public void publishSuccess(CalculationRequest request, CalculationResult result) {
        log.debug("Publishing CalculationSuccessEvent for request: {}", request);

        HttpRequestContext context = contextProvider.getCurrentContext();

        CalculationSuccessEvent event = CalculationSuccessEvent.builder()
                .request(request)
                .result(result)
                .endpoint(context.endpoint())
                .httpMethod(context.httpMethod())
                .build();

        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishFailure(CalculationRequest request, Exception exception) {
        log.debug("Publishing CalculationFailureEvent for request: {}", request);

        HttpRequestContext context = contextProvider.getCurrentContext();

        CalculationFailureEvent event = CalculationFailureEvent.builder()
                .request(request)
                .errorMessage(exception.getMessage())
                .errorCode(extractErrorCode(exception))
                .endpoint(context.endpoint())
                .httpMethod(context.httpMethod())
                .httpStatusCode(determineHttpStatusCode(exception))
                .build();

        applicationEventPublisher.publishEvent(event);
    }

    private String extractErrorCode(Exception exception) {
        if (exception instanceof DomainException domainException) {
            return domainException.getErrorCode();
        }
        return "UNEXPECTED_ERROR";
    }

    private Integer determineHttpStatusCode(Exception exception) {
        if (exception instanceof DomainException domainException) {

            String errorCode = domainException.getErrorCode();
            return switch (errorCode) {
                case "CALCULATION_ERROR", "INVALID_INPUT" -> 400;
                case "PERCENTAGE_SERVICE_UNAVAILABLE" -> 503;
                default -> 500;
            };
        }
        return 500;
    }

}
