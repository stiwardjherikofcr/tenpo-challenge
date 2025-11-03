package cl.tenpo.sjcr.percentage_calculator_service.domain.service;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.ExternalServiceException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.PercentageServiceUnavailableException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CachePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.PercentageServicePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService.PercentageResolutionResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Percentage Resilience Service Tests")
class PercentageResilienceServiceTest {

    @Mock
    private PercentageServicePort percentageServicePort;

    @Mock
    private CachePort cachePort;

    private PercentageResilienceService service;

    @BeforeEach
    void setUp() {
        service = new PercentageResilienceService(percentageServicePort, cachePort);
    }

    @Test
    @DisplayName("Should return percentage from service and cache it")
    void shouldReturnFromServiceAndCache() {

        Percentage expectedPercentage = Percentage.of(new BigDecimal("15"));
        when(percentageServicePort.getCurrentPercentage()).thenReturn(expectedPercentage);

        PercentageResolutionResult result = service.getPercentageWithFallback();

        assertThat(result.getPercentage()).isEqualTo(expectedPercentage);
        assertThat(result.isFromCache()).isFalse();
        verify(percentageServicePort).getCurrentPercentage();
        verify(cachePort).put(expectedPercentage);
    }

    @Test
    @DisplayName("Should use cached value when service fails")
    void shouldUseCachedValueWhenServiceFails() {

        Percentage cachedPercentage = Percentage.of(new BigDecimal("10"));
        when(percentageServicePort.getCurrentPercentage())
                .thenThrow(new ExternalServiceException("Service unavailable"));
        when(cachePort.get()).thenReturn(Optional.of(cachedPercentage));

        PercentageResolutionResult result = service.getPercentageWithFallback();

        assertThat(result.getPercentage()).isEqualTo(cachedPercentage);
        assertThat(result.isFromCache()).isTrue();
        verify(percentageServicePort).getCurrentPercentage();
        verify(cachePort).get();
        verify(cachePort, never()).put(any());
    }

    @Test
    @DisplayName("Should throw exception when service fails and cache is empty")
    void shouldThrowWhenServiceFailsAndCacheEmpty() {

        when(percentageServicePort.getCurrentPercentage())
                .thenThrow(new ExternalServiceException("Service unavailable"));
        when(cachePort.get()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPercentageWithFallback())
                .isInstanceOf(PercentageServiceUnavailableException.class)
                .hasMessageContaining("unavailable");

        verify(percentageServicePort).getCurrentPercentage();
        verify(cachePort).get();
    }

    @Test
    @DisplayName("Should not cache when service throws exception")
    void shouldNotCacheWhenServiceThrowsException() {

        when(percentageServicePort.getCurrentPercentage())
                .thenThrow(new ExternalServiceException("Service error"));
        when(cachePort.get()).thenReturn(Optional.of(Percentage.of(BigDecimal.TEN)));

        service.getPercentageWithFallback();

        verify(cachePort, never()).put(any());
    }
}
