package cl.tenpo.sjcr.percentage_calculator_service.application.usecase;

import cl.tenpo.sjcr.percentage_calculator_service.application.event.publisher.CalculationEventPublisher;
import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.DomainException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.in.CalculateUseCase;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.CalculationDomainService;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService.PercentageResolutionResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CalculateWithPercentageUseCase implements CalculateUseCase {

    private static final Logger log = LoggerFactory.getLogger(CalculateWithPercentageUseCase.class);

    private final CalculationDomainService calculationService;
    private final PercentageResilienceService percentageResolver;
    private final CalculationEventPublisher eventPublisher;
    private final Counter successCounter;
    private final Counter failureCounter;

    public CalculateWithPercentageUseCase(
            CalculationDomainService calculationService,
            PercentageResilienceService percentageResilienceService,
            CalculationEventPublisher eventPublisher,
            MeterRegistry meterRegistry
    ) {
        this.calculationService = calculationService;
        this.percentageResolver = percentageResilienceService;
        this.eventPublisher = eventPublisher;
        this.successCounter = Counter.builder("calculation.success")
                .description("Number of successful calculations")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("calculation.failure")
                .description("Number of failed calculations")
                .register(meterRegistry);
    }

    @Override
    public CalculationResult execute(CalculationRequest request) {
        log.info("Starting calculation for request: {}", request);

        try {

            PercentageResolutionResult resolutionResult = resolvePercentage();
            Percentage percentage = resolutionResult.getPercentage();
            boolean usedCache = resolutionResult.isFromCache();

            CalculationResult result = calculationService.calculate(request, percentage, usedCache);

            calculationService.validateResult(result);

            eventPublisher.publishSuccess(request, result);

            successCounter.increment();
            log.info("Calculation completed successfully: {}", result);

            return result;

        } catch (DomainException e) {
            log.error("Domain error during calculation: {}", e.getMessage(), e);

            eventPublisher.publishFailure(request, e);

            failureCounter.increment();
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during calculation: {}", e.getMessage(), e);

            eventPublisher.publishFailure(request, e);

            failureCounter.increment();
            throw new RuntimeException("Unexpected error during calculation", e);
        }
    }

    private PercentageResolutionResult resolvePercentage() {
        log.debug("Resolving percentage from external service or cache");
        return percentageResolver.getPercentageWithFallback();
    }
}
