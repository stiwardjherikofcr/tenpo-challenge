package cl.tenpo.sjcr.percentage_calculator_service.domain.port.out;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;

public interface CalculationEventPort {

    void publishSuccess(CalculationRequest request, CalculationResult result);

    void publishFailure(CalculationRequest request, Exception exception);
}
