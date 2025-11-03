package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "cache")
@Validated
public class CacheProperties {

    @NotBlank(message = "Cache name must not be blank")
    private String name = "percentageCache";

    @Min(value = 1, message = "Cache expiration must be at least 1 minute")
    private int expirationMinutes = 30;

    @Min(value = 1, message = "Cache maximum size must be at least 1")
    private int maximumSize = 100;

    private boolean recordStats = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(int expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public boolean isRecordStats() {
        return recordStats;
    }

    public void setRecordStats(boolean recordStats) {
        this.recordStats = recordStats;
    }
}
