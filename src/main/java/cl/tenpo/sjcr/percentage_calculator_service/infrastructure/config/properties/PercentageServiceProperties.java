package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "percentage.service")
@Validated
public class PercentageServiceProperties {

    @Valid
    @NotNull(message = "Mock service configuration must not be null")
    private MockConfig mock = new MockConfig();

    public MockConfig getMock() {
        return mock;
    }

    public void setMock(MockConfig mock) {
        this.mock = mock;
    }

    public static class MockConfig {

        private boolean enabled = true;

        @NotNull(message = "Default percentage must not be null")
        @DecimalMin(value = "0.0", message = "Default percentage must be at least 0")
        @DecimalMax(value = "100.0", message = "Default percentage must be at most 100")
        private BigDecimal defaultPercentage = new BigDecimal("15.0");

        @DecimalMin(value = "0.0", message = "Failure rate must be at least 0.0")
        @DecimalMax(value = "1.0", message = "Failure rate must be at most 1.0")
        private double failureRate = 0.3;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public BigDecimal getDefaultPercentage() {
            return defaultPercentage;
        }

        public void setDefaultPercentage(BigDecimal defaultPercentage) {
            this.defaultPercentage = defaultPercentage;
        }

        public double getFailureRate() {
            return failureRate;
        }

        public void setFailureRate(double failureRate) {
            this.failureRate = failureRate;
        }
    }
}
