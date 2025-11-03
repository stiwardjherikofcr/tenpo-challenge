package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config;

import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.properties.CacheProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NonNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private final CacheProperties cacheProperties;

    public CacheConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(cacheProperties.getName());
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<@NonNull Object, @NonNull Object> caffeineCacheBuilder() {
        Caffeine<@NonNull Object, @NonNull Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(cacheProperties.getExpirationMinutes(), TimeUnit.MINUTES)
                .maximumSize(cacheProperties.getMaximumSize());

        if (cacheProperties.isRecordStats()) {
            builder.recordStats();
        }

        return builder;
    }

}
