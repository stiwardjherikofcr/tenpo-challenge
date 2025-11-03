package cl.tenpo.sjcr.percentage_calculator_service.domain.port.out;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;

public interface PercentageServicePort {

    Percentage getCurrentPercentage();
}
