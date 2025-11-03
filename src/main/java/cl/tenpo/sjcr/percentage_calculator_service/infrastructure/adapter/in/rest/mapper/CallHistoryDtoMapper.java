package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.mapper;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response.CallHistoryResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CallHistoryDtoMapper {

    public CallHistoryResponseDto toDto(CallHistory domain) {
        if (domain == null) {
            return null;
        }

        return CallHistoryResponseDto.builder()
                .id(domain.getId().getValue().toString())
                .timestamp(domain.getTimestamp())
                .endpoint(domain.getEndpoint())
                .method(domain.getHttpMethod())
                .requestParams(domain.getRequestParameters())
                .response(domain.getResponse())
                .errorMessage(domain.getErrorMessage())
                .success(domain.isSuccessful())
                .build();
    }
}
