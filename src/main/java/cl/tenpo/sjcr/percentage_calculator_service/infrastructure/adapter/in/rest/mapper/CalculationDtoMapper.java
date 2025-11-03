package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.mapper;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.request.CalculationRequestDto;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response.CalculationResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CalculationDtoMapper {

    public CalculationRequest toDomain(CalculationRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return CalculationRequest.builder()
                .num1(dto.getNum1())
                .num2(dto.getNum2())
                .build();
    }

    public CalculationResponseDto toDto(CalculationResult result) {
        if (result == null) {
            return null;
        }

        return CalculationResponseDto.builder()
                .result(result.getResult())
                .originalSum(result.getSum())
                .appliedPercentage(result.getAppliedPercentage().getValue())
                .timestamp(result.getCalculatedAt())
                .build();
    }
}
