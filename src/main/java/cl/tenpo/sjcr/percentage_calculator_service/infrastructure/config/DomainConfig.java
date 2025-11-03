package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config;

import cl.tenpo.sjcr.percentage_calculator_service.domain.factory.CallHistoryFactory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CachePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.PercentageServicePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.CalculationDomainService;
import cl.tenpo.sjcr.percentage_calculator_service.domain.service.PercentageResilienceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public CalculationDomainService calculationDomainService() {
        return new CalculationDomainService();
    }

    @Bean
    public PercentageResilienceService percentageResilienceService(
            PercentageServicePort percentageServicePort,
            CachePort cachePort) {
        return new PercentageResilienceService(percentageServicePort, cachePort);
    }

    @Bean
    public CallHistoryFactory callHistoryFactory(ObjectMapper objectMapper) {
        return new CallHistoryFactory(objectMapper);
    }
}
