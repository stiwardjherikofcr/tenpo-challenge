package cl.tenpo.sjcr.percentage_calculator_service.application.usecase;

import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.event.publisher.CalculationEventPublisher;
import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationFailureEvent;
import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationSuccessEvent;
import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.PercentageServiceUnavailableException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.CalculationDomainService;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService.PercentageResolutionResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context.HttpRequestContext;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context.HttpRequestContextProvider;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Calculate With Percentage Use Case Tests")
class CalculateWithPercentageUseCaseTest {

        @Mock
        private PercentageResilienceService percentageResilienceService;

        @Mock
        private ApplicationEventPublisher applicationEventPublisher;

        @Mock
        private HttpRequestContextProvider contextProvider;

        @Captor
        private ArgumentCaptor<Object> eventCaptor;

        private CalculationEventPublisher eventPublisher;
        private CalculationDomainService calculationService;
        private MeterRegistry meterRegistry;
        private CalculateWithPercentageUseCase useCase;

        @BeforeEach
        void setUp() {

                when(contextProvider.getCurrentContext())
                                .thenReturn(new HttpRequestContext("/api/v1/calculate", "POST"));

                eventPublisher = new CalculationEventPublisher(applicationEventPublisher, contextProvider);
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

                verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
                assertThat(eventCaptor.getValue()).isInstanceOf(CalculationSuccessEvent.class);
                CalculationSuccessEvent event = (CalculationSuccessEvent) eventCaptor.getValue();
                assertThat(event.getEndpoint()).isEqualTo("/api/v1/calculate");
                assertThat(event.getHttpMethod()).isEqualTo("POST");
                assertThat(event.getRequest()).isEqualTo(request);
                assertThat(event.getResult()).isEqualTo(result);

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

                verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
                assertThat(eventCaptor.getValue()).isInstanceOf(CalculationFailureEvent.class);
                CalculationFailureEvent event = (CalculationFailureEvent) eventCaptor.getValue();
                assertThat(event.getEndpoint()).isEqualTo("/api/v1/calculate");
                assertThat(event.getHttpMethod()).isEqualTo("POST");
                assertThat(event.getErrorCode()).isEqualTo("PERCENTAGE_SERVICE_UNAVAILABLE");
                assertThat(event.getHttpStatusCode()).isEqualTo(503);

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

                verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
                assertThat(eventCaptor.getValue()).isInstanceOf(CalculationFailureEvent.class);
                CalculationFailureEvent event = (CalculationFailureEvent) eventCaptor.getValue();
                assertThat(event.getErrorCode()).isEqualTo("UNEXPECTED_ERROR");
                assertThat(event.getHttpStatusCode()).isEqualTo(500);
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

                verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
                assertThat(eventCaptor.getValue()).isInstanceOf(CalculationSuccessEvent.class);
        }
}
