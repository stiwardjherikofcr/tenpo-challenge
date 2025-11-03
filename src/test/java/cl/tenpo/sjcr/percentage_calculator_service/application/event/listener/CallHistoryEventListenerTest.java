package cl.tenpo.sjcr.percentage_calculator_service.application.event.listener;

import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationFailureEvent;
import cl.tenpo.sjcr.percentage_calculator_service.domain.event.CalculationSuccessEvent;
import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.PercentageServiceUnavailableException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.factory.CallHistoryFactory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Call History Event Listener Tests")
class CallHistoryEventListenerTest {

        @Mock
        private CallHistoryRepositoryPort repository;

        private ObjectMapper objectMapper;
        private CallHistoryFactory callHistoryFactory;
        private CallHistoryEventListener listener;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                callHistoryFactory = new CallHistoryFactory(objectMapper);
                listener = new CallHistoryEventListener(repository, callHistoryFactory);
        }

        @Test
        @DisplayName("Should handle success event and persist call history")
        void shouldHandleSuccessEvent() {

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("10"))
                                .num2(new BigDecimal("20"))
                                .build();

                CalculationResult result = CalculationResult.builder()
                                .result(new BigDecimal("34.50"))
                                .sum(new BigDecimal("30"))
                                .appliedPercentage(Percentage.of(new BigDecimal("15")))
                                .percentageAmount(new BigDecimal("4.50"))
                                .build();

                CalculationSuccessEvent event = CalculationSuccessEvent.builder()
                                .request(request)
                                .result(result)
                                .endpoint("/api/v1/calculate")
                                .httpMethod("POST")
                                .build();

                listener.handleCalculationSuccess(event);

                ArgumentCaptor<CallHistory> captor = ArgumentCaptor.forClass(CallHistory.class);
                verify(repository).save(captor.capture());

                CallHistory saved = captor.getValue();
                assertThat(saved.getEndpoint()).isEqualTo("/api/v1/calculate");
                assertThat(saved.getHttpMethod()).isEqualTo("POST");
                assertThat(saved.isSuccessful()).isTrue();
                assertThat(saved.getRequestParameters()).contains("num1");
                assertThat(saved.getRequestParameters()).contains("num2");
                assertThat(saved.getResponse()).contains("result");
                assertThat(saved.getErrorMessage()).isNull();
        }

        @Test
        @DisplayName("Should handle failure event and persist call history with error")
        void shouldHandleFailureEvent() {

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("10"))
                                .num2(new BigDecimal("20"))
                                .build();

                Exception exception = new PercentageServiceUnavailableException("Service unavailable");
                CalculationFailureEvent event = CalculationFailureEvent.builder()
                                .request(request)
                                .errorMessage(exception.getMessage())
                                .errorCode("SERVICE_UNAVAILABLE")
                                .endpoint("/api/v1/calculate")
                                .httpMethod("POST")
                                .httpStatusCode(503)
                                .build();

                listener.handleCalculationFailure(event);

                ArgumentCaptor<CallHistory> captor = ArgumentCaptor.forClass(CallHistory.class);
                verify(repository).save(captor.capture());

                CallHistory saved = captor.getValue();
                assertThat(saved.getEndpoint()).isEqualTo("/api/v1/calculate");
                assertThat(saved.getHttpMethod()).isEqualTo("POST");
                assertThat(saved.isSuccessful()).isFalse();
                assertThat(saved.getRequestParameters()).contains("num1");
                assertThat(saved.getErrorMessage()).isEqualTo("Service unavailable");
                assertThat(saved.getResponse()).isNull();
        }

        @Test
        @DisplayName("Should not throw exception if persistence fails (graceful degradation)")
        void shouldNotThrowIfPersistenceFails() {

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("10"))
                                .num2(new BigDecimal("20"))
                                .build();

                CalculationResult result = CalculationResult.builder()
                                .result(new BigDecimal("34.50"))
                                .sum(new BigDecimal("30"))
                                .appliedPercentage(Percentage.of(new BigDecimal("15")))
                                .percentageAmount(new BigDecimal("4.50"))
                                .build();

                CalculationSuccessEvent event = CalculationSuccessEvent.builder()
                                .request(request)
                                .result(result)
                                .endpoint("/api/v1/calculate")
                                .httpMethod("POST")
                                .build();

                when(repository.save(any())).thenThrow(new RuntimeException("Database error"));

                listener.handleCalculationSuccess(event);

                verify(repository).save(any());

        }

        @Test
        @DisplayName("Should include timestamp from event in call history")
        void shouldIncludeTimestampFromEvent() {

                LocalDateTime before = LocalDateTime.now();

                CalculationRequest request = CalculationRequest.builder()
                                .num1(new BigDecimal("10"))
                                .num2(new BigDecimal("20"))
                                .build();

                CalculationResult result = CalculationResult.builder()
                                .result(new BigDecimal("34.50"))
                                .sum(new BigDecimal("30"))
                                .appliedPercentage(Percentage.of(new BigDecimal("15")))
                                .percentageAmount(new BigDecimal("4.50"))
                                .build();

                CalculationSuccessEvent event = CalculationSuccessEvent.builder()
                                .request(request)
                                .result(result)
                                .endpoint("/api/v1/calculate")
                                .httpMethod("POST")
                                .build();

                listener.handleCalculationSuccess(event);

                ArgumentCaptor<CallHistory> captor = ArgumentCaptor.forClass(CallHistory.class);
                verify(repository).save(captor.capture());

                CallHistory saved = captor.getValue();

                assertThat(saved.getTimestamp()).isNotNull();
                assertThat(saved.getTimestamp()).isAfter(before.minusSeconds(1));
        }
}
