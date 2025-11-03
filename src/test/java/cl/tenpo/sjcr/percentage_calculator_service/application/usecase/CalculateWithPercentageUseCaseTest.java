package cl.tenpo.sjcr.percentage_calculator_service.application.usecase;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.PercentageServiceUnavailableException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CalculationEventPort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.CalculationDomainService;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService.PercentageResolutionResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Calculate With Percentage Use Case Tests")
class CalculateWithPercentageUseCaseTest {

        @Mock
        private PercentageResilienceService percentageResilienceService;

        @Mock
        private CalculationEventPort eventPublisher;

        private CalculationDomainService calculationService;
        private MeterRegistry meterRegistry;
        private CalculateWithPercentageUseCase useCase;

        @BeforeEach
        void setUp() {
                calculationService = new CalculationDomainService();
                meterRegistry = new SimpleMeterRegistry();
                useCase = new CalculateWithPercentageUseCase(
                                calculationService,
                                percentageResilienceService,
                                eventPublisher,
                                meterRegistry);
        }

        @Test
        @DisplayName("Should calculate successfully and publish success event")
        void shouldCalculateSuccessfully() {

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("10"))
                                .num2(new BigDecimal("20"))
                                .build();
                Percentage percentage = Percentage.of(new BigDecimal("15"));
                PercentageResolutionResult resolutionResult = PercentageResolutionResult.fromService(percentage);

                when(percentageResilienceService.getPercentageWithFallback()).thenReturn(resolutionResult);

                CalculationResult result = useCase.execute(request);

                assertThat(result).isNotNull();
                assertThat(result.getResult()).isEqualByComparingTo("34.50");
                assertThat(result.getAppliedPercentage()).isEqualTo(percentage);

                verify(eventPublisher).publishSuccess(eq(request), any(CalculationResult.class));

                Counter successCounter = meterRegistry.find("calculation.success").counter();
                assertThat(successCounter).isNotNull();
                assertThat(successCounter.count()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("Should publish failure event when percentage service fails")
        void shouldPublishFailureEventWhenServiceFails() {

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("10"))
                                .num2(new BigDecimal("20"))
                                .build();

                PercentageServiceUnavailableException exception = new PercentageServiceUnavailableException(
                                "Service unavailable");
                when(percentageResilienceService.getPercentageWithFallback()).thenThrow(exception);

                assertThatThrownBy(() -> useCase.execute(request))
                                .isInstanceOf(PercentageServiceUnavailableException.class);

                verify(eventPublisher).publishFailure(eq(request), eq(exception));

                Counter failureCounter = meterRegistry.find("calculation.failure").counter();
                assertThat(failureCounter).isNotNull();
                assertThat(failureCounter.count()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("Should not publish success event when calculation fails")
        void shouldNotPublishSuccessWhenCalculationFails() {

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("10"))
                                .num2(new BigDecimal("20"))
                                .build();

                when(percentageResilienceService.getPercentageWithFallback())
                                .thenThrow(new RuntimeException("Unexpected error"));

                assertThatThrownBy(() -> useCase.execute(request))
                                .isInstanceOf(RuntimeException.class);

                verify(eventPublisher).publishFailure(eq(request), any(RuntimeException.class));
        }

        @Test
        @DisplayName("Should calculate with decimals and publish event")
        void shouldCalculateWithDecimals() {

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("15.75"))
                                .num2(new BigDecimal("24.25"))
                                .build();
                Percentage percentage = Percentage.of(new BigDecimal("12.5"));
                PercentageResolutionResult resolutionResult = PercentageResolutionResult.fromService(percentage);

                when(percentageResilienceService.getPercentageWithFallback()).thenReturn(resolutionResult);

                CalculationResult result = useCase.execute(request);

                assertThat(result.getSum()).isEqualByComparingTo("40");

                assertThat(result.getResult()).isEqualByComparingTo("45.00");

                verify(eventPublisher).publishSuccess(eq(request), any(CalculationResult.class));
        }
}
