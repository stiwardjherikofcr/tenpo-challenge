package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.cache;

import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CachePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class CaffeineCacheAdapter implements CachePort {

    private static final String CACHE_NAME = "percentageCache";
    private static final String CACHE_KEY = "currentPercentage";

    private final Cache cache;

    public CaffeineCacheAdapter(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
        if (this.cache == null) {
            throw new IllegalStateException("Cache '" + CACHE_NAME + "' is not configured");
        }
    }

    @Override
    public void put(Percentage percentage) {
        log.debug("Caching percentage: {}", percentage);
        cache.put(CACHE_KEY, percentage);
        log.info("Percentage cached successfully");
    }

    @Override
    public Optional<Percentage> get() {
        log.debug("Retrieving percentage from cache");
        Percentage cached = cache.get(CACHE_KEY, Percentage.class);
        if (cached != null) {
            log.info("Cache hit: {}", cached);
            return Optional.of(cached);
        }
        log.info("Cache miss");
        return Optional.empty();
    }

    @Override
    public void invalidate() {
        log.debug("Evicting percentage from cache");
        cache.evict(CACHE_KEY);
        log.info("Cache evicted successfully");
    }

    @Override
    public boolean containsKey(String key) {
        log.debug("checking cache presence for {}", key);
        return cache.get(key) != null;
    }

}
