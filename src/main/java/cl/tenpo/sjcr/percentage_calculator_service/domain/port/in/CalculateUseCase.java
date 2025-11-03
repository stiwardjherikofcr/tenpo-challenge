package cl.tenpo.sjcr.percentage_calculator_service.domain.port.in;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;

public interface CalculateUseCase {

    CalculationResult execute(CalculationRequest request);
}
