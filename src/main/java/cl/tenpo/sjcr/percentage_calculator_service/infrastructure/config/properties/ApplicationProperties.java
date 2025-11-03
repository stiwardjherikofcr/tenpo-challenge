package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
                CacheProperties.class,
                PercentageServiceProperties.class
})
public class ApplicationProperties {
}
