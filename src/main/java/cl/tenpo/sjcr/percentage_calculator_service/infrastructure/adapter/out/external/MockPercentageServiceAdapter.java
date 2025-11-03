package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.external;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.ExternalServiceException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.PercentageServicePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.properties.PercentageServiceProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class MockPercentageServiceAdapter implements PercentageServicePort {

    private static final Logger log = LoggerFactory.getLogger(MockPercentageServiceAdapter.class);
    private static final String CIRCUIT_BREAKER_NAME = "percentageService";

    private final Random random = new Random();
    private final PercentageServiceProperties.MockConfig mockConfig;

    public MockPercentageServiceAdapter(PercentageServiceProperties percentageServiceProperties) {
        this.mockConfig = percentageServiceProperties.getMock();
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public Percentage getCurrentPercentage() {
        log.debug("Calling mock external percentage service");

        simulateLatency();

        if (random.nextDouble() < mockConfig.getFailureRate()) {
            log.warn("Mock service simulating failure");
            throw new ExternalServiceException("Simulated service failure");
        }

        BigDecimal variation = BigDecimal.valueOf(random.nextDouble() * 10 - 5); // ±5%
        BigDecimal percentage = mockConfig.getDefaultPercentage()
                .add(variation)
                .max(BigDecimal.ZERO)
                .min(new BigDecimal("100"));

        log.info("Mock service returning percentage: {}", percentage);
        return Percentage.of(percentage);
    }

    private void simulateLatency() {
        try {
            Thread.sleep(random.nextInt(100, 500)); // 100-500ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalServiceException("Service call interrupted", e);
        }
    }

    @SuppressWarnings("unused")
    private Percentage fallback(Exception e) {
        log.error("Circuit breaker fallback triggered", e);
        throw new ExternalServiceException("External service unavailable", e);
    }
}
