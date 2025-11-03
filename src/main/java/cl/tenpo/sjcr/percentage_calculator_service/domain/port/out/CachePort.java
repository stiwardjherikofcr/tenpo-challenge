package cl.tenpo.sjcr.percentage_calculator_service.domain.port.out;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;

import java.util.Optional;

public interface CachePort {

    void put(Percentage percentage);

    Optional<Percentage> get();

    void invalidate();

    boolean containsKey(String key);
}
