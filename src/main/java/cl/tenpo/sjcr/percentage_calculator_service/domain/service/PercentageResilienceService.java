package cl.tenpo.sjcr.percentage_calculator_service.domain.service;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.PercentageServiceUnavailableException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CachePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.PercentageServicePort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PercentageResilienceService {

    private static final Logger log = LoggerFactory.getLogger(PercentageResilienceService.class);

    private final PercentageServicePort percentageService;
    private final CachePort cache;

    public PercentageResilienceService(
            PercentageServicePort percentageService,
            CachePort cache
    ) {
        this.percentageService = percentageService;
        this.cache = cache;
    }

    public PercentageResolutionResult getPercentageWithFallback() {

        Optional<Percentage> servicePercentage = tryGetFromService();

        if (servicePercentage.isPresent()) {
            Percentage percentage = servicePercentage.get();

            cache.put(percentage);
            return PercentageResolutionResult.fromService(percentage);
        }

        Optional<Percentage> cachedPercentage = cache.get();

        if (cachedPercentage.isPresent()) {
            return PercentageResolutionResult.fromCache(cachedPercentage.get());
        }

        throw new PercentageServiceUnavailableException(
                "Percentage service is unavailable and no cached value exists"
        );
    }

    private Optional<Percentage> tryGetFromService() {
        try {
            Percentage percentage = percentageService.getCurrentPercentage();
            return Optional.of(percentage);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static class PercentageResolutionResult {
        private final Percentage percentage;
        private final boolean fromCache;

        private PercentageResolutionResult(Percentage percentage, boolean fromCache) {
            this.percentage = percentage;
            this.fromCache = fromCache;
        }

        public static PercentageResolutionResult fromService(Percentage percentage) {
            return new PercentageResolutionResult(percentage, Boolean.FALSE);
        }

        public static PercentageResolutionResult fromCache(Percentage percentage) {
            return new PercentageResolutionResult(percentage, Boolean.TRUE);
        }

        public Percentage getPercentage() {
            return percentage;
        }

        public boolean isFromCache() {
            return fromCache;
        }
    }
}
